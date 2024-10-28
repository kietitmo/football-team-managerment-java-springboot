package com.kietitmo.football_team_managerment.repositories;

import com.kietitmo.football_team_managerment.entities.Team;
import com.kietitmo.football_team_managerment.entities.TeamMember;
import com.kietitmo.football_team_managerment.entities.User;
import com.kietitmo.football_team_managerment.mapper.TeamMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, String>, JpaSpecificationExecutor<TeamMember> {
}
