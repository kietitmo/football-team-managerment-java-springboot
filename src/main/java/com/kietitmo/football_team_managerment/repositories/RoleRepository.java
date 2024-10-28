package com.kietitmo.football_team_managerment.repositories;

import com.kietitmo.football_team_managerment.entities.Role;
import com.kietitmo.football_team_managerment.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, String>, JpaSpecificationExecutor<Role> {
    @Query("select r from Role r where r.name = :name")
    Role getByName(String name);
}
