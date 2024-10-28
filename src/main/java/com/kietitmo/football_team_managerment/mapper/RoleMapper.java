package com.kietitmo.football_team_managerment.mapper;

import com.kietitmo.football_team_managerment.dto.request.RoleRequest;
import com.kietitmo.football_team_managerment.dto.request.TeamRequest;
import com.kietitmo.football_team_managerment.dto.response.RoleResponse;
import com.kietitmo.football_team_managerment.entities.Role;
import com.kietitmo.football_team_managerment.entities.Team;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "permissions", ignore = true)
    void updateRole(@MappingTarget Role team, RoleRequest request);

    RoleResponse toRoleResponse(Role role);
}