package com.authService.app.Services.Account.Interfaces;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public interface AvatarServiceInterface {

    String[] allowedExtensions = {"jpg","jpeg","webp","png"};

    boolean checkExtension(MultipartFile file);

    boolean checkExtension(File file);

    String saveAvatarFile(String userId,MultipartFile file) throws IOException;




}
