package com.videoManager.app.Services.Videos;
import com.videoManager.app.Config.Exceptions.FileStoreException;
import com.videoManager.app.Config.Exceptions.ThumbnailsNotReadyYetException;
import com.videoManager.app.Models.EnvironmentVariables;
import com.videoManager.app.Models.Records.VideoProcessingData;
import com.videoManager.app.Models.ThumbnailsData;
import com.videoManager.app.Models.Video;
import com.videoManager.app.Repositories.ThumbnailsDataRepository;
import com.videoManager.app.Repositories.VideoRepository;
import com.videoManager.app.Services.Files.FileSaveService;
import com.videoManager.app.Services.JsonUtils;
import com.videoManager.app.Services.Kafka.KafkaSenderService;
import com.videoManager.app.Services.Notifications.NotificationGeneratorService;
import com.videoManager.app.Services.Videos.Interfaces.VideoUploadInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class VideoUploadService implements VideoUploadInterface {
    private final FileSaveService fileSaveService;

    private final VideoRepository videoRepository;

    private final KafkaSenderService kafkaSenderService;

    private final NotificationGeneratorService notificationGenerator;

    private final ThumbnailsDataRepository thumbnailsDataRepository;

    private final EnvironmentVariables environmentVariables;



    @Override
    public void uploadVideo(MultipartFile file) throws FileStoreException{
        String accountId = SecurityContextHolder.getContext().getAuthentication().getName();
        Video video = new Video();
        video.setRawPath(this.fileSaveService.saveRawFile(file));
        video.setDatePublished(new Date());
        video.setAuthorId(accountId);
        video.setProcessing(true);
        videoRepository.save(video);
        VideoProcessingData vpd = new VideoProcessingData(
                video.getId(),
                video.getRawPath(),
                null,
                null
        );
        String json = JsonUtils.toJson(vpd);
        kafkaSenderService.send("video_uploaded",json);
    }

    @Override
    public void finishProcessing(VideoProcessingData vpf) {
        Optional<Video> finishedVideo = videoRepository.findById(vpf.videoId());
        if(finishedVideo.isEmpty()){
            throw new RuntimeException("Video not found by id: "+vpf.videoId());
        }
        Video video = finishedVideo.get();
        video.setPlaylistName(vpf.playlistName());
        video.setProcessing(false);
        videoRepository.save(video);
        if(video.getThumbnail() != null){
            notificationGenerator.videoProcessingFinished(
                    video.getAuthorId(),
                    video.getName(),
                    video.getId(),
                    video.getThumbnail()
            );
        }
    }

    @Override
    public void thumbnailsGeneratedProcess(ThumbnailsData data) {
        List<String> processedPaths = data.getThumbnails().stream().map(
                absPath -> absPath.replace(environmentVariables.getThumbnailsPath(), "/media/thumbnails")
        ).toList();
        data.setThumbnails(processedPaths);
        thumbnailsDataRepository.save(data);
    }
    @Override
    public List<String> getThumbnails(String videoId) throws ThumbnailsNotReadyYetException {
        Optional<ThumbnailsData>data = thumbnailsDataRepository.findByVideoId(videoId);
        if (data.isEmpty()){
            throw new ThumbnailsNotReadyYetException("Thumbnails not generated yet.");
        }
        return data.get().getThumbnails();
    }


}
