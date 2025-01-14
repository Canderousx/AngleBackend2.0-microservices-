package com.videoManager.app.Services.Tags;
import com.videoManager.app.Models.Records.VideoDetails;
import com.videoManager.app.Models.Tag;
import com.videoManager.app.Models.Video;
import com.videoManager.app.Repositories.TagRepository;
import com.videoManager.app.Services.Tags.Interface.TagSaverInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class TagSaverService implements TagSaverInterface {

    private TagRepository tagRepository;

    private final CacheManager cacheManager;

    private final Logger logger = LogManager.getLogger(TagSaverService.class);


    @Autowired
    public TagSaverService(TagRepository tagRepository, CacheManager cacheManager) {
        this.tagRepository = tagRepository;
        this.cacheManager = cacheManager;
    }
    @Override
    public Set<Tag> setTags(VideoDetails metaData) {
        Set<Tag> tags = new HashSet<>();
        for(Tag tag: metaData.tags()){
            if(!tagRepository.existsByName(tag.getName().toLowerCase())){
                tagRepository.save(tag);
            }
            tagRepository.findByName(tag.getName()).ifPresent(tags::add);
        }
        if(tags.isEmpty()){
            logger.warn("Warning: Video without any tags detected!");
        }
        return tags;
    }
}
