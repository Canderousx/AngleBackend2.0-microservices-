package com.videoManager.app.Services.Files.Interfaces;

import com.videoManager.app.Config.Exceptions.FileStoreException;
import org.springframework.web.multipart.MultipartFile;

public interface FilesSaveInterface {

    String saveRawFile(MultipartFile file) throws FileStoreException;
}
