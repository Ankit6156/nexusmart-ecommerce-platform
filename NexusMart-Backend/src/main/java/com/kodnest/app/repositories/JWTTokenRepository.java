package com.kodnest.app.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.kodnest.app.entities.JWTToken;


@Repository
public interface JWTTokenRepository extends JpaRepository<JWTToken, Integer> {

    @Query("SELECT j FROM JWTToken j WHERE j.user.userId = :userId")
    Optional<JWTToken> findByUser_UserId(Integer userId);
    Optional<JWTToken> findByToken(String token);
    
 // Custom query to delete tokens by user ID
    @Modifying
    @Transactional
    @Query("DELETE FROM JWTToken t WHERE t.user.userId = :userId")
    int deleteByUserId(@Param("userId") int userId);

}

