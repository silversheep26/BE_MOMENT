package com.back.moment.photos.repository.getPhotoWhoLove;

import com.back.moment.love.entity.QLove;
import com.back.moment.photos.entity.QPhoto;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Primary
public class GetPhotoWhoLoveImpl implements GetPhotoWhoLove {
    private final JPAQueryFactory queryFactory;

    public GetPhotoWhoLoveImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public Map<Long, Boolean> findPhotoLoveMap(List<Long> photoIdList, Long usersId) {
        QPhoto photo = QPhoto.photo;
        QLove love = QLove.love;

        CaseBuilder caseBuilder = new CaseBuilder();
        BooleanExpression caseExpression = caseBuilder
                .when(love.id.isNotNull()).then(true)
                .otherwise(false);

        BooleanExpression usersIdExpression = usersId != null ? love.users.id.eq(usersId) : love.users.isNull();

        List<Tuple> tuples = queryFactory
                .select(Projections.tuple(photo.id, caseExpression))
                .from(photo)
                .leftJoin(photo.loveList, love)
                .on(usersIdExpression, love.photo.id.eq(photo.id))
                .where(photo.id.in(photoIdList))
                .groupBy(photo.id)
                .fetch();

        Map<Long, Boolean> photoLoveMap = new HashMap<>();
        for (Tuple tuple : tuples) {
            Long photoId = tuple.get(photo.id);
            Boolean isLoved = tuple.get(1, Boolean.class);
            photoLoveMap.put(photoId, isLoved);
        }
        return photoLoveMap;
    }
}

