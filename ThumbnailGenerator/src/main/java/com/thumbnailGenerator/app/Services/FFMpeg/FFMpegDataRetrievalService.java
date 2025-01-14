package com.thumbnailGenerator.app.Services.FFMpeg;
import com.thumbnailGenerator.app.Models.EnvironmentVariables;
import com.thumbnailGenerator.app.Models.Records.ThumbnailsData;
import com.thumbnailGenerator.app.Services.FFMpeg.Interfaces.FFMpegDataRetrievalInterface;
import com.thumbnailGenerator.app.Services.JsonUtils;
import com.thumbnailGenerator.app.Services.Kafka.KafkaSenderService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class FFMpegDataRetrievalService implements FFMpegDataRetrievalInterface {

    private final Logger logger = LogManager.getLogger(FFMpegDataRetrievalService.class);

    private final EnvironmentVariables environmentVariables;

    private final KafkaSenderService kafkaSenderService;

    public FFMpegDataRetrievalService(EnvironmentVariables environmentVariables, KafkaSenderService kafkaSenderService) {
        this.environmentVariables = environmentVariables;
        this.kafkaSenderService = kafkaSenderService;
    }

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

    private void generatorSuccessEvent(String videoId, List<String>thumbnails){
        ThumbnailsData data = new ThumbnailsData(
                videoId,
                thumbnails
        );
        String json = JsonUtils.toJson(data);
        kafkaSenderService.send(
                "thumbnails_generated",
                json
        );
    }

    private void conversionErrorEvent(String videoId,int exitCode){
        kafkaSenderService.send(
                "thumbnail_generator_error",
                "Generator failed for "
                        +videoId
                        +" EXIT CODE: "+exitCode
                        +", check out [ThumbnailGenerator] logs..."
        );
    }

    @Override
    public double getVideoDuration(String rawPath) throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder("ffprobe", "-v", "error", "-show_entries",
                "format=duration", "-of", "default=noprint_wrappers=1:nokey=1", rawPath);
        Process process = builder.start();
        String output = inputStreamToString(process.getInputStream());
        logger.info("Video duration: "+output);
        process.waitFor();
        return Double.parseDouble(output.trim());
    }

    @Override
    public CompletableFuture<List<String>> generateVideoThumbnails(String videoId, String rawPath) {
        return CompletableFuture.supplyAsync(() -> {
            List<String> generatedPaths = new ArrayList<>();
            try {
                double videoLength = getVideoDuration(rawPath);
                File rawFile = new File(rawPath);
                String outputPath = environmentVariables.getThumbnailsPath() + File.separator + videoId;

                int framesNumber = (int) Math.floor(videoLength / 2);
                if (framesNumber > 5) {
                    framesNumber = 5;
                } else if (framesNumber < 1) {
                    framesNumber = 1;
                }

                new File(outputPath).mkdirs();

                List<String> command = Arrays.asList(
                        environmentVariables.getFfmpegPath(), "-i", rawPath,
                        "-vf", "fps=" + (videoLength < 2 ? "1" : "1/2") + ",scale=320:-1",
                        "-vframes", String.valueOf(framesNumber),
                        "-compression_level", "6", "-preset", "photo",
                        "-f", "image2", outputPath + File.separator + "thumbnail%03d.png"
                );

                ProcessBuilder builder = new ProcessBuilder(command);
                builder.redirectErrorStream(true);
                Process process = builder.start();

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        logger.info(line);
                    }
                }

                process.waitFor();
                int exitValue = process.exitValue();
                if (exitValue != 0) {
                    logger.error("FFmpeg process failed with exit code " + exitValue);
                    conversionErrorEvent(videoId,exitValue);
                    throw new RuntimeException("FFmpeg failed to generate thumbnails");
                }
                File[] thumbnails = new File(outputPath).listFiles((dir, name) -> name.startsWith("thumbnail"));
                if (thumbnails != null) {
                    for (File thumbnail : thumbnails) {
                        generatedPaths.add("/api/video-manager/media/thumbnails/"+videoId+"/"+thumbnail.getName());
                    }
                }

            } catch (Exception e) {
                logger.error("Error during thumbnail generation", e);
                throw new RuntimeException(e);
            }
            generatorSuccessEvent(videoId,generatedPaths);
            return generatedPaths;
        });
    }
}
