package com.kietitmo.football_team_managerment.services;

import com.kietitmo.football_team_managerment.dto.request.PermissionRequest;
import com.kietitmo.football_team_managerment.dto.response.PageResponse;
import com.kietitmo.football_team_managerment.dto.response.PermissionResponse;
import com.kietitmo.football_team_managerment.entities.Permission;
import com.kietitmo.football_team_managerment.mapper.PermissionMapper;
import com.kietitmo.football_team_managerment.repositories.PermissionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@PreAuthorize("hasRole('ADMIN')")
public class PermissionService extends BaseService<Permission, String, PermissionResponse, PermissionRepository>{

    PermissionRepository permissionRepository;
    PermissionMapper permissionMapper;

    public PermissionResponse create(PermissionRequest request){
        Permission permission = permissionMapper.toPermission(request);
        permission = permissionRepository.save(permission);
        return permissionMapper.toPermissionResponse(permission);
    }

    public List<PermissionResponse> getAll(){
        var permissions = permissionRepository.findAll();
        return permissions.stream().map(permissionMapper::toPermissionResponse).toList();
    }

    public void delete(String permission){
        permissionRepository.deleteById(permission);
    }

    @Override
    protected PermissionRepository getRepository() {
        return permissionRepository;
    }

    @Override
    protected PageResponse<List<PermissionResponse>> convertToPageResponse(Page<Permission> items, Pageable pageable) {
        List<PermissionResponse> response = items.stream()
                .map(item -> PermissionResponse.builder()
                        .name(item.getName())
                        .description(item.getDescription())
                        .build())
                .toList();

        return PageResponse.<List<PermissionResponse>>builder()
                .page(pageable.getPageNumber())
                .size(pageable.getPageSize())
                .totalPage(items.getTotalPages())
                .totalItem(items.getTotalElements())
                .items(response)
                .build();
    }

}
