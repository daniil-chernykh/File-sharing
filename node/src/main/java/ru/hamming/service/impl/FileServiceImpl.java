package ru.hamming.service.impl;

import lombok.extern.log4j.Log4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import ru.hamming.dao.AppDocumentDAO;
import ru.hamming.dao.AppPhotoDAO;
import ru.hamming.dao.BinaryContentDAO;
import ru.hamming.entity.AppDocument;
import ru.hamming.entity.AppPhoto;
import ru.hamming.entity.BinaryContent;
import ru.hamming.exceptions.UploadFileException;
import ru.hamming.service.FileService;
import ru.hamming.service.enums.LinkType;
import ru.hamming.utils.CryptoTool;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;


@Log4j
@Service
@Component
public class FileServiceImpl implements FileService {
    @Value("${token}")
    private String token;

    @Value("${service.file_info.uri}")
    private String fileInfoUri;

    @Value("${service.file_storage.uri}")
    private String fileStorageUri;

    @Value("${link.address}")
    private String linkAddress;

    private final AppDocumentDAO appDocumentDAO;
    private final BinaryContentDAO binaryContentDAO;
    private final AppPhotoDAO appPhotoDAO;
    private final CryptoTool cryptoTool;



    public FileServiceImpl(AppDocumentDAO appDocumentDAO, BinaryContentDAO binaryContentDAO, AppPhotoDAO appPhotoDAO, CryptoTool cryptoTool) {
        this.appDocumentDAO = appDocumentDAO;
        this.binaryContentDAO = binaryContentDAO;
        this.appPhotoDAO = appPhotoDAO;
        this.cryptoTool = cryptoTool;
    }

    @Override
    public AppDocument processDoc(Message telegramMessage) {
        try {
            log.info("Start processing document: " + telegramMessage.getDocument());
            Document telegramDoc = telegramMessage.getDocument();
            if (telegramDoc == null) {
                log.error("Telegram document is null: " + telegramMessage);
                throw new UploadFileException("Telegram document is null");
            }
            String fileId = telegramDoc.getFileId();
            ResponseEntity<String> response = getFilePath(fileId);

            if (response.getStatusCode() == HttpStatus.OK) {
                BinaryContent persistentBinaryContent = getPersistentBinaryContent(response);
                AppDocument transientAppDoc = buildTransientAppDoc(telegramDoc, persistentBinaryContent);
                AppDocument appDocument = appDocumentDAO.save(transientAppDoc);
                log.info("Document processed successfully: " + appDocument);
                return appDocument;
            } else {
                log.error("Bad response from telegram service: " + response);
                throw new UploadFileException("Bad response from telegram service: " + response);
            }
        } catch (Exception e) {
            log.error("Error while processing doc: ", e);
            e.printStackTrace();
            throw new UploadFileException("Error while processing doc", e);
        }
    }

    @Override
    public AppPhoto processPhoto(Message telegramMessage) {
        try {
            log.info("Start processing photo: " + telegramMessage.getPhoto());
            if (telegramMessage.getPhoto() == null || telegramMessage.getPhoto().isEmpty()) {
                log.error("Telegram photo is null: " + telegramMessage);
                throw new UploadFileException("Telegram photo is null");
            }

            var photoSizeCount = telegramMessage.getPhoto().size();
            var photoIndex = photoSizeCount > 1 ? telegramMessage.getPhoto().size() - 1 : 0;

            PhotoSize telegramPhoto = telegramMessage.getPhoto().get(photoIndex);
            String fileId = telegramPhoto.getFileId();
            ResponseEntity<String> response = getFilePath(fileId);

            if (response.getStatusCode() == HttpStatus.OK) {
                BinaryContent persistentBinaryContent = getPersistentBinaryContent(response);
                AppPhoto transientAppPhoto = buildTransientAppPhoto(telegramPhoto, persistentBinaryContent);
                AppPhoto appPhoto = appPhotoDAO.save(transientAppPhoto);
                log.info("Photo processed successfully: " + appPhoto);
                return appPhoto;
            } else {
                log.error("Bad response from telegram service: " + response);
                throw new UploadFileException("Bad response from telegram service: " + response);
            }
        } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            log.error("IndexOutOfBoundsException while processing photo: ", indexOutOfBoundsException);
            throw new UploadFileException("IndexOutOfBoundsException while processing photo: ", indexOutOfBoundsException);
        } catch (Exception e) {
            log.error("Error while processing photo: ", e);
            e.printStackTrace();
            throw new UploadFileException("Error while processing photo: ", e);
        }
    }


    private AppPhoto buildTransientAppPhoto(PhotoSize telegramPhoto, BinaryContent persistentBinaryContent) {
        return AppPhoto.builder()
                .telegramField(telegramPhoto.getFileId())
                .binaryContent(persistentBinaryContent)
                .fileSize(telegramPhoto.getFileSize())
                .build();
    }

    private BinaryContent getPersistentBinaryContent(ResponseEntity<String> response) {
        try {
            String filePath = getFilePath(response);

            byte[] fileInByte = downloadFile(filePath);
            BinaryContent transientBinaryContent = BinaryContent
                    .builder()
                    .fileAsArrayOfBytes(fileInByte)
                    .build();
            BinaryContent persistentBinaryContent = binaryContentDAO.save(transientBinaryContent);
            log.info("Binary content saved: " + persistentBinaryContent.getId());
            return persistentBinaryContent;
        } catch (Exception e) {
            log.error("Error while saving binary content: ", e);
            e.printStackTrace();
            throw new UploadFileException("Error while saving binary content", e);
        }
    }

    private String getFilePath(ResponseEntity<String> response) {
        try {
            JSONObject jsonObject = new JSONObject(response.getBody());
            String filePath = String.valueOf(
                    jsonObject
                            .getJSONObject("result")
                            .getString("file_path")
            );
            log.info("File path: " + filePath);
            return filePath;
        } catch (Exception e) {
            log.error("Error while getting file path: ", e);
            e.printStackTrace();
            throw new UploadFileException("Error while getting file path", e);
        }
    }

    private AppDocument buildTransientAppDoc(Document telegramDoc, BinaryContent persistentBinaryContent) {
        return AppDocument.builder()
                .telegramField(telegramDoc.getFileId())
                .docName(telegramDoc.getFileName())
                .binaryContent(persistentBinaryContent)
                .mimeType(telegramDoc.getMimeType())
                .fileSize(telegramDoc.getFileSize())
                .build();
    }

    private ResponseEntity<String> getFilePath(String fileId) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> request = new HttpEntity<>(headers);
        try {
            log.info("Sending request to telegram service, fileId:" + fileId);
            ResponseEntity<String> response = restTemplate.exchange(
                    fileInfoUri,
                    HttpMethod.GET,
                    request,
                    String.class,
                    token, fileId
            );
            log.info("Response from telegram service: " + response);
            return response;
        } catch (Exception e){
            log.error("Error while sending request to telegram service: " + fileId, e);
            throw new UploadFileException("Error while sending request to telegram service: " + fileId, e);
        }
    }

    private byte[] downloadFile(String filePath) {
        String fullUri = fileStorageUri.replace("{token}", token)
                .replace("{filePath}", filePath);

        URL urlObj = null;
        try {
            urlObj = new URL(fullUri);
            log.info("Start downloading file from: " + urlObj.toExternalForm());
        } catch (MalformedURLException malformedURLException) {
            log.error("MalformedURLException in downloadFile: " + fullUri, malformedURLException);
            throw new UploadFileException("MalformedURLException in downloadFile: " + fullUri, malformedURLException);
        }

        try (InputStream inputStream = urlObj.openStream()) {
            byte[] fileInByte = inputStream.readAllBytes();
            log.info("File downloaded successfully from " + urlObj.toExternalForm());
            return fileInByte;
        } catch (IOException ioException) {
            log.error("IOException in downloadFile: " + urlObj.toExternalForm(), ioException);
            throw new UploadFileException(urlObj.toExternalForm(), ioException);
        }
    }

    @Override
    public String generateLink(Long docId, LinkType linkType) {
        var hash = cryptoTool.hashOf(docId);

        return "http://" + linkAddress + "/" + linkType + "?id=" + hash;
    }
}