package com.videoProcessor.app.Services.FFMpeg;
import com.videoProcessor.app.Services.FFMpeg.Interfaces.FFMpegDataRetrievalInterface;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import java.io.*;
import java.nio.charset.StandardCharsets;
@Service
@Slf4j
public class FFMpegDataRetrievalService implements FFMpegDataRetrievalInterface {

    private static String inputStreamToString(InputStream inputStream) throws IOException {
        StringBuilder textBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                textBuilder.append(line);
            }
        }
        return textBuilder.toString();
    }

    @Override
    public double getVideoDuration(String rawPath) throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder("ffprobe", "-v", "error", "-show_entries",
                "format=duration", "-of", "default=noprint_wrappers=1:nokey=1", rawPath);
        Process process = builder.start();
        String output = inputStreamToString(process.getInputStream());
        log.info("Video duration: "+output);
        process.waitFor();
        return Double.parseDouble(output.trim());
    }
}
