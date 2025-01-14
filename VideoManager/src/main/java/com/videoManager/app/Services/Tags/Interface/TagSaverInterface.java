package com.videoManager.app.Services.Tags.Interface;


import com.videoManager.app.Models.Records.VideoDetails;
import com.videoManager.app.Models.Tag;
import com.videoManager.app.Models.Video;

import java.util.Set;

public interface TagSaverInterface {


    Set<Tag> setTags(VideoDetails metaData);
}
