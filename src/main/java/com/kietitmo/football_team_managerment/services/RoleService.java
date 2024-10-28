package com.kietitmo.football_team_managerment.services;

import com.kietitmo.football_team_managerment.dto.request.RoleRequest;
import com.kietitmo.football_team_managerment.dto.response.*;
import com.kietitmo.football_team_managerment.entities.Role;
import com.kietitmo.football_team_managerment.entities.Vote;
import com.kietitmo.football_team_managerment.mapper.RoleMapper;
import com.kietitmo.football_team_managerment.repositories.PermissionRepository;
import com.kietitmo.football_team_managerment.repositories.RoleRepository;
import com.kietitmo.football_team_managerment.repositories.VoteRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
//@PreAuthorize("hasRole('ADMIN')")
public class RoleService extends BaseService<Role, String, RoleResponse, RoleRepository>{
    RoleRepository roleRepository;
    PermissionRepository permissionRepository;
    RoleMapper roleMapper;

    public RoleResponse create(RoleRequest request){
        Role role = roleMapper.toRole(request);

        var permissions = permissionRepository.findAllById(request.getPermissions());

        role.setPermissions(new HashSet<>(permissions));

        role = roleRepository.save(role);
        return roleMapper.toRoleResponse(role);
    }

    public Role getRole(String roleName){
        return roleRepository.getByName(roleName);
    }

    public List<RoleResponse> getAll(){
        return roleRepository.findAll()
                .stream()
                .map(roleMapper::toRoleResponse)
                .toList();
    }

    public RoleResponse updateRole(String name, RoleRequest request) {
        Role role = roleRepository.findById(name)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        roleMapper.updateRole(role, request);

        var permissions = permissionRepository.findAllById(request.getPermissions());
        role.setPermissions(new HashSet<>(permissions));

        return roleMapper.toRoleResponse(roleRepository.save(role));
    }

    public void delete(String role){
        roleRepository.deleteById(role);
    }

    @Override
    protected RoleRepository getRepository() {
        return roleRepository;
    }

    @Override
    protected PageResponse<List<RoleResponse>> convertToPageResponse(Page<Role> items, Pageable pageable) {
        List<RoleResponse> response = items.stream().map(item ->
             RoleResponse.builder()
                    .name(item.getName())
                    .description(item.getDescription())
                    .permissions(item.getPermissions().stream().map(p ->
                         new PermissionResponse(p.getName(), p.getDescription())
                    ).collect(Collectors.toSet()))
                    .build()
        ).toList();

        return PageResponse.<List<RoleResponse>>builder()
                .page(pageable.getPageNumber())
                .size(pageable.getPageSize())
                .totalPage(items.getTotalPages())
                .totalItem(items.getTotalElements())
                .items(response)
                .build();
    }
}