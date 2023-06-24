package com.back.moment.photos.repository.getAll;

import com.back.moment.global.service.RedisService;
import com.back.moment.photos.entity.*;
import com.back.moment.photos.entity.QPhoto;
import com.back.moment.photos.entity.QPhotoHashTag;
import com.back.moment.photos.entity.QTag_Photo;
import com.back.moment.users.entity.QUsers;
import com.back.moment.users.entity.Users;
import com.querydsl.core.JoinType;
import com.querydsl.core.Tuple;
import com.querydsl.core.group.GroupBy;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@Primary
public class GetAllPhotoImpl implements GetAllPhoto{
    private final JPAQueryFactory queryFactory;

    public GetAllPhotoImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public List<Photo> getAllPhoto() {
        QPhoto photo = QPhoto.photo;
        QTag_Photo tag_photo = QTag_Photo.tag_Photo;
        QPhotoHashTag photoHashTag = QPhotoHashTag.photoHashTag;

        QPhoto photoWithTags = new QPhoto("photoWithTags");
        QTag_Photo tag_photoWithTags = new QTag_Photo("tag_photoWithTags");
        QPhotoHashTag photoHashTagWithTags = new QPhotoHashTag("photoHashTagWithTags");

        JPQLQuery<Photo> query = queryFactory
                .selectDistinct(photo)
                .from(photo)
                .leftJoin(photo.tag_photoList, tag_photo)
                .leftJoin(tag_photo.photoHashTag, photoHashTag)
                .leftJoin(photoWithTags).on(photo.createdAt.eq(photoWithTags.createdAt), photo.users.eq(photoWithTags.users))
                .leftJoin(photoWithTags.tag_photoList, tag_photoWithTags)
                .leftJoin(tag_photoWithTags.photoHashTag, photoHashTagWithTags)
                .groupBy(photo.users, photo.uploadCnt) // 그룹화 추가
                .orderBy(photo.createdAt.desc());
//                .offset(pageable.getOffset())
//                .limit(pageable.getPageSize());

//        List<Photo> photoList = query.fetch();
//        long total = query.fetchCount();
//
//        return new PageImpl<>(photoList, pageable, total);
        return query.fetch();
    }
}
