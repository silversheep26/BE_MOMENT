package com.back.moment.boards.repository;

import com.back.moment.boards.entity.LocationTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationTagRepository extends JpaRepository<LocationTag, Long> {
    LocationTag findByLocation(String location);
}
