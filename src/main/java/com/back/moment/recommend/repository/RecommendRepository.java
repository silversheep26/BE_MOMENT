package com.back.moment.recommend.repository;

import com.back.moment.recommend.entity.Recommend;
import com.back.moment.users.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RecommendRepository extends JpaRepository<Recommend, Long> {
    @Query("select r from Recommend r where r.recommender.nickName = :recommendNickName and r.recommended.nickName = :recommendedNickName")
    Recommend existRecommend(@Param("recommendNickName") String recommendNickName, @Param("recommendedNickName") String recommendedNickName);

    boolean existsByRecommendedIdAndRecommenderId(Long recommendedId, Long recommenderId);
}
