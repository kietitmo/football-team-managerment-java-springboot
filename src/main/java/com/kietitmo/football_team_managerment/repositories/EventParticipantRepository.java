package com.kietitmo.football_team_managerment.repositories;

import com.kietitmo.football_team_managerment.entities.EventParticipant;
import com.kietitmo.football_team_managerment.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventParticipantRepository extends JpaRepository<EventParticipant, String>, JpaSpecificationExecutor<EventParticipant> {
    List<EventParticipant> findByEventId(String eventId);
    List<EventParticipant> findByUserId(String userId);
}
