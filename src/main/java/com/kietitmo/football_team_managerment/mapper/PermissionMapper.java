package com.kietitmo.football_team_managerment.mapper;

import com.kietitmo.football_team_managerment.dto.request.PermissionRequest;
import com.kietitmo.football_team_managerment.dto.response.PermissionResponse;
import com.kietitmo.football_team_managerment.entities.Permission;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest request);
    PermissionResponse toPermissionResponse(Permission Permission);
}