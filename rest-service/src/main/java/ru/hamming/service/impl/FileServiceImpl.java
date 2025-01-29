package ru.hamming.service.impl;

import lombok.extern.log4j.Log4j;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.hamming.dao.AppDocumentDAO;
import ru.hamming.dao.AppPhotoDAO;
import ru.hamming.entity.AppDocument;
import ru.hamming.entity.AppPhoto;
import ru.hamming.entity.BinaryContent;
import ru.hamming.service.FileService;
import ru.hamming.utils.CryptoTool;

import java.io.File;
import java.io.IOException;

@Log4j
@Service
@Component
public class FileServiceImpl implements FileService {

    private final AppDocumentDAO appDocumentDAO;
    private final AppPhotoDAO appPhotoDAO;
    private final CryptoTool cryptoTool;

    public FileServiceImpl(AppDocumentDAO appDocumentDAO, AppPhotoDAO appPhotoDAO, CryptoTool cryptoTool) {
        this.appDocumentDAO = appDocumentDAO;
        this.appPhotoDAO = appPhotoDAO;
        this.cryptoTool = cryptoTool;
    }

    @Override
    public AppDocument getDocument(String hash) {
        var docId = cryptoTool.idOf(hash);

        if (docId == null) return null;

        return appDocumentDAO.findById(docId).orElse(null);
    }

    @Override
    public AppPhoto getPhoto(String hash) {

        var photoId = cryptoTool.idOf(hash);

        if (photoId == null) return null;
        return appPhotoDAO.findById(photoId).orElse(null);
    }

//    @Override
//    public FileSystemResource getFileSystemResource(BinaryContent binaryContent) {
//        try {
//            //TODO добавить генерацию имени временного файла
//            File temp = File.createTempFile("tempFile",".bin");
//            temp.deleteOnExit();
//            FileUtils.writeByteArrayToFile(temp, binaryContent.getFileAsArrayOfBytes());
//            return new FileSystemResource(temp);
//        } catch (IOException e) {
//            log.error(e);
//            return null;
//        }
//    }
}
