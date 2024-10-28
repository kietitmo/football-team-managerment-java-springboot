package com.kietitmo.football_team_managerment.repositories;


import com.kietitmo.football_team_managerment.entities.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String>, JpaSpecificationExecutor<RefreshToken> {
    @Query("select r from RefreshToken r where r.user.username = :username")
    Optional<RefreshToken> findByUsername(String username);

}