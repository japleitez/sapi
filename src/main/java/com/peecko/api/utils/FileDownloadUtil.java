package com.peecko.api.utils;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

// https://www.codejava.net/frameworks/spring-boot/file-download-upload-rest-api-examples

public class FileDownloadUtil {
    Path foundFile;
    public Resource getFileAsResource(String fileCode) throws IOException {

        Path dirPath = Paths.get("videos");

        Files.list(dirPath).forEach(file -> {
            if (file.getFileName().toString().startsWith(fileCode)) {
                foundFile = file;
            }
        });

        if (foundFile != null) {
            return new UrlResource(foundFile.toUri());
        }

        return null;
    }
}
