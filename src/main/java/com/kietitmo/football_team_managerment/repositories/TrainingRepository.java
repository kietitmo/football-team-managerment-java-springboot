package com.kietitmo.football_team_managerment.repositories;

import com.kietitmo.football_team_managerment.entities.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainingRepository extends JpaRepository<Training, String>, JpaSpecificationExecutor<Training> {

}
