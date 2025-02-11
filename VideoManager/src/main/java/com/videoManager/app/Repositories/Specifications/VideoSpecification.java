package com.videoManager.app.Repositories.Specifications;

import com.videoManager.app.Models.Tag;
import com.videoManager.app.Models.Video;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class VideoSpecification {


    private static List<Predicate>videoActivePredicates(Root<Video> root, CriteriaBuilder cb){
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get("isBanned"), false));
        predicates.add(cb.isNotNull(root.get("thumbnail")));
        predicates.add(cb.isNotNull(root.get("name")));
        return predicates;
    }

    public static Specification<Video>findAllActive(){
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = videoActivePredicates(root,criteriaBuilder);
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

    }

    public static Specification<Video>findMostPopular(){
        return (root,query,criteriaBuilder) ->{
            List<Predicate>predicates = videoActivePredicates(root,criteriaBuilder);
            query.orderBy(criteriaBuilder.desc(root.get("views")));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Video>findBySubscribers(List<String>subIds){
        return (root,query,criteriaBuilder) ->{
            List<Predicate> predicates = videoActivePredicates(root,criteriaBuilder);
            Predicate isSubscribedChannel = root.get("authorId").in(subIds);
            predicates.add(isSubscribedChannel);
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Video> findSimilar(Set<String>tagNames,String currentVideoId){
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = videoActivePredicates(root,criteriaBuilder);
            Join<Video, Tag> tagsJoin = root.join("tags");
            predicates.add(tagsJoin.get("name").in(tagNames));
            predicates.add(criteriaBuilder.notEqual(root.get("id"), currentVideoId));
            query.distinct(true);
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Video> findRandom(List<String>videoIds,String currentId){
        return (root,query,criteriaBuilder) ->{
            List<Predicate> predicates = videoActivePredicates(root,criteriaBuilder);
            predicates.add(criteriaBuilder.notEqual(root.get("id"),currentId));
            predicates.add(criteriaBuilder.not(root.get("id").in(videoIds)));
            query.distinct(true);
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Video> findByTag(String tag){
        return (root,query,criteriaBuilder) ->{
          List<Predicate> predicates = videoActivePredicates(root,criteriaBuilder);
          predicates.add(criteriaBuilder.greaterThan(
                    criteriaBuilder.function("FIND_IN_SET", Integer.class, criteriaBuilder.literal(tag), root.get("tags")),
                    0
          ));
          query.distinct(true);
          return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Video> searchVideos(String searchWord){
        return (root,query,criteriaBuilder) ->{
            List<Predicate> predicates = videoActivePredicates(root,criteriaBuilder);
            Predicate nameLike = criteriaBuilder.like(root.get("name"), "%"+ searchWord + "%");
            Join<Video,Tag> tagJoin = root.join("tags",JoinType.LEFT);
            Predicate tagLike = criteriaBuilder.like(tagJoin.get("name"),"%" + searchWord + "%");

            predicates.add(criteriaBuilder.or(nameLike,tagLike));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
