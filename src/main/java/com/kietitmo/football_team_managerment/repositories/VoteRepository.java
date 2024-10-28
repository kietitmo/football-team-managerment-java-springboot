package com.kietitmo.football_team_managerment.repositories;

import com.kietitmo.football_team_managerment.entities.Content;
import com.kietitmo.football_team_managerment.entities.User;
import com.kietitmo.football_team_managerment.entities.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, String>, JpaSpecificationExecutor<Vote> {
    Optional<Vote> findByUserAndContent(User user, Content content);

}
