package com.back.moment.photos.repository.feedSearch;

import com.back.moment.love.entity.Love;
import com.back.moment.photos.dto.PhotoFeedResponseDto;
import com.back.moment.photos.entity.Photo;
import com.back.moment.photos.entity.QPhoto;
import com.back.moment.photos.entity.QPhotoHashTag;
import com.back.moment.photos.entity.QTag_Photo;
import com.back.moment.users.entity.QUsers;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@Primary
public class FeedSearchImpl implements FeedSearch{
    private final JPAQueryFactory queryFactory;

    public FeedSearchImpl(EntityManager entityManager){
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public Page<PhotoFeedResponseDto> feedSearch(String userNickName, String tag, Pageable pageable, Long currentUserId) {
        QPhoto photo = QPhoto.photo;
        QUsers users = QUsers.users;
        QTag_Photo tag_photo = QTag_Photo.tag_Photo;
        QPhotoHashTag photoHashTag = QPhotoHashTag.photoHashTag;

        JPQLQuery<Photo> query = queryFactory.selectDistinct(photo)
                .from(photo)
                .leftJoin(photo.users, users)
                .leftJoin(photo.tag_photoList, tag_photo)
                .leftJoin(tag_photo.photoHashTag, photoHashTag);

        BooleanExpression conditions = null;

        if (userNickName != null) {
            conditions = users.nickName.like("%" + userNickName + "%");
        }
        if (tag != null) {
            if (conditions != null) {
                conditions = conditions.and(photoHashTag.hashTag.like("%" + tag + "%"));
            } else {
                conditions = photoHashTag.hashTag.like("%" + tag + "%");
            }
        }

        if (conditions != null) {
            query.where(conditions);
        }

        query.offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        QueryResults<Photo> results = query.fetchResults();
        List<Photo> photoList = results.getResults();
        long totalCount = results.getTotal();

        List<PhotoFeedResponseDto> responseDtoList = photoList.stream()
                .map(p -> new PhotoFeedResponseDto(p, checkIfUserLovesPhoto(p.getLoveList(), currentUserId)))
                .toList();

        return new PageImpl<>(responseDtoList, pageable, totalCount);
    }


    private boolean checkIfUserLovesPhoto(List<Love> loveList, Long currentUserId) {
        if (currentUserId == null) {
            return false;
        }

        for (Love love : loveList) {
            if (love.getUsers().getId().equals(currentUserId)) {
                return true;
            }
        }
        return false;
    }

}
