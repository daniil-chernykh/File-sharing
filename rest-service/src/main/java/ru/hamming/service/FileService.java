package ru.hamming.service;

import org.springframework.core.io.FileSystemResource;
import ru.hamming.entity.AppDocument;
import ru.hamming.entity.AppPhoto;
import ru.hamming.entity.BinaryContent;

public interface FileService {
    AppDocument getDocument(String id);
    AppPhoto getPhoto(String id);
//    FileSystemResource getFileSystemResource(BinaryContent binaryContent);
}
