package com.back.moment.users.repository;

import com.back.moment.users.dto.ForMainResponseDto;
import com.back.moment.users.entity.RoleEnum;
import com.back.moment.users.entity.Users;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByEmail(String email);
    Optional<Users> findByNickName(String nickName);
    Optional<Users> findByKakaoId(Long id);

    @Query("select new com.back.moment.users.dto.ForMainResponseDto(u) from Users u where u.role = :role ORDER BY u.totalLoveCnt DESC")
    List<ForMainResponseDto> findTop3ByRole(@Param("role") RoleEnum role, Pageable pageable);

    @Query("select new com.back.moment.users.dto.ForMainResponseDto(u) from Users u ORDER BY u.totalLoveCnt DESC")
    List<ForMainResponseDto> findTop3(Pageable pageable);

}
