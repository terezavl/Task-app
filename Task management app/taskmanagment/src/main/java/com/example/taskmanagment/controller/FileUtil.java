package com.example.taskmanagment.controller;

import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class FileUtil {
    public static StreamingResponseBody streamingResponse(InputStream csvStream){
        return outputStream -> {
            try (OutputStream os = new BufferedOutputStream(outputStream)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = csvStream.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
            } finally {
                csvStream.close();
            }
        };
    }
}
