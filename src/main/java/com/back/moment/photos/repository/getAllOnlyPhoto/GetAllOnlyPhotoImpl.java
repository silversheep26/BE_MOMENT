package com.back.moment.photos.repository.getAllOnlyPhoto;

import com.back.moment.photos.dto.OnlyPhotoResponseDto;
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
import java.util.stream.Collectors;

@Repository
@Primary
public class GetAllOnlyPhotoImpl implements GetAllOnlyPhoto {
    private final JPAQueryFactory queryFactory;

    public GetAllOnlyPhotoImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public List<OnlyPhotoResponseDto> findAllOnlyPhoto() {
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
                .groupBy(photo.users, photo.uploadCnt)
                .orderBy(photo.createdAt.desc());

        List<Photo> photos = query.fetch();

        return photos.stream()
                .map(OnlyPhotoResponseDto::new).collect(Collectors.toList());
    }
}
