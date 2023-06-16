package com.back.moment.photos.repository.getAll;

import com.back.moment.photos.entity.Photo;
import com.back.moment.photos.entity.QPhoto;
import com.back.moment.photos.entity.QPhotoHashTag;
import com.back.moment.photos.entity.QTag_Photo;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;

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

        return query.fetch();
    }
}
