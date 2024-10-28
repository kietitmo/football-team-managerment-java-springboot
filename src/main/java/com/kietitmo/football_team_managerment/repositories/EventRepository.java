package com.kietitmo.football_team_managerment.repositories;

import com.kietitmo.football_team_managerment.entities.Event;
import com.kietitmo.football_team_managerment.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, String>, JpaSpecificationExecutor<Event> {
}
