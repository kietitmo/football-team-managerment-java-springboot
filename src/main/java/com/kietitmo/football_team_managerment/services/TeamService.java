package com.kietitmo.football_team_managerment.services;

import com.kietitmo.football_team_managerment.dto.request.MemberRequest;
import com.kietitmo.football_team_managerment.dto.request.TeamRequest;
import com.kietitmo.football_team_managerment.dto.response.MemberResponse;
import com.kietitmo.football_team_managerment.dto.response.PageResponse;
import com.kietitmo.football_team_managerment.dto.response.TeamResponse;
import com.kietitmo.football_team_managerment.entities.Role;
import com.kietitmo.football_team_managerment.entities.Team;
import com.kietitmo.football_team_managerment.entities.TeamMember;
import com.kietitmo.football_team_managerment.exceptions.AppException;
import com.kietitmo.football_team_managerment.exceptions.ErrorCode;
import com.kietitmo.football_team_managerment.mapper.TeamMapper;
import com.kietitmo.football_team_managerment.repositories.RoleRepository;
import com.kietitmo.football_team_managerment.repositories.TeamMemberRepository;
import com.kietitmo.football_team_managerment.repositories.UserRepository;
import com.kietitmo.football_team_managerment.repositories.TeamRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@PreAuthorize("hasRole('ADMIN')")
public class TeamService extends BaseService<Team, String, TeamResponse, TeamRepository> {
    TeamRepository teamRepository;
    UserRepository userRepository;
    RoleRepository roleRepository;
    TeamMemberRepository teamMemberRepository;
    TeamMapper teamMapper;

    @NonFinal
    @Value("${file.upload-dir.team-logo}")
    String uploadDir;

    public TeamResponse createTeam(TeamRequest request) {
        // Map request thành team object
        var team = teamMapper.toTeam(request);

        // Truy vấn owner một lần
        var creator = userRepository.findById(request.getCreator())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // Lưu team trước khi xử lý members
        var finalTeam = teamRepository.save(team);

        // Lấy tất cả userId từ request để giảm số lần truy vấn
        var userIds = request.getMembers().stream()
                .map(MemberRequest::getUserId)
                .toList();
        var users = userRepository.findAllById(userIds);

        // Kiểm tra xem tất cả userId có tồn tại không
        if (users.size() != userIds.size()) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }

        // Lưu các thành viên vào team
        var teamMembers = new HashSet<TeamMember>();
        request.getMembers().forEach(member -> {
            var user = users.stream()
                    .filter(u -> u.getId().equals(member.getUserId()))
                    .findFirst()
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

            var roles = roleRepository.findAllById(member.getRoles());
            if (roles.size() != member.getRoles().size()) {
                throw new AppException(ErrorCode.ROLE_NOT_EXISTED);
            }

            // Tạo TeamMember và thêm vào danh sách
            var teamMember = TeamMember.builder().user(user).team(finalTeam).roles(roles).build();
            teamMembers.add(teamMember);
        });

        // Sử dụng batch insert cho các TeamMembers
        teamMemberRepository.saveAll(teamMembers);

        // Gán owner và members vào team
        finalTeam.setCreator(creator);
        finalTeam.setMembers(teamMembers);

        // Lưu team đã hoàn chỉnh
        teamRepository.save(finalTeam);

        // Trả về response
        return teamMapper.toTeamResponse(finalTeam);
    }


    public List<TeamResponse> getAll(){
        return teamRepository.findAll()
                .stream()
                .map(teamMapper::toTeamResponse)
                .toList();
    }

    public TeamResponse getTeam(String teamId){
        var team = teamRepository.findById(teamId)
                .orElseThrow(() -> new AppException(ErrorCode.TEAM_NOT_EXISTED));
        return teamMapper.toTeamResponse(team);
    }

    public TeamResponse updateTeam(String teamId, TeamRequest request) {
        // Tìm kiếm team theo ID, ném ngoại lệ nếu không tìm thấy
        var existingTeam = teamRepository.findById(teamId)
                .orElseThrow(() -> new AppException(ErrorCode.TEAM_NOT_EXISTED));

        teamMapper.updateTeam(existingTeam,request);

        // Tìm kiếm chủ sở hữu mới (nếu được cập nhật)
        var newOwner = userRepository.findById(request.getCreator())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // Cập nhật các thông tin cơ bản của team
        existingTeam.setCreator(newOwner);

        // Lấy danh sách thành viên hiện có của team
        var existingMembers = existingTeam.getMembers();

        // Tạo một Set để chứa các thành viên mới sau khi cập nhật
        var updatedTeamMembers = new HashSet<TeamMember>();

        // Duyệt qua các thành viên trong yêu cầu cập nhật
        request.getMembers().forEach(memberRequest -> {
            // Tìm kiếm người dùng theo ID, ném ngoại lệ nếu không tồn tại
            var user = userRepository.findById(memberRequest.getUserId())
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

            // Tìm kiếm tất cả các vai trò của thành viên
            var roles = roleRepository.findAllById(memberRequest.getRoles());
            if (roles.size() != memberRequest.getRoles().size()) {
                throw new AppException(ErrorCode.ROLE_NOT_EXISTED);
            }

            // Kiểm tra nếu thành viên đã tồn tại trong danh sách
            var existingMember = existingMembers.stream()
                    .filter(m -> m.getUser().getId().equals(user.getId()))
                    .findFirst();

            // Nếu thành viên đã tồn tại, cập nhật vai trò
            if (existingMember.isPresent()) {
                var teamMember = existingMember.get();
                teamMember.setRoles(roles);  // Cập nhật vai trò
                updatedTeamMembers.add(teamMember);
            } else {
                // Nếu thành viên chưa tồn tại, tạo thành viên mới và thêm vào danh sách cập nhật
                var newTeamMember = TeamMember.builder()
                        .user(user)
                        .team(existingTeam)
                        .roles(roles)
                        .build();
                updatedTeamMembers.add(newTeamMember);
            }
        });

        // Xóa các thành viên đã bị loại bỏ
        existingMembers.forEach(existingMember -> {
            if (updatedTeamMembers.stream().noneMatch(m -> m.getUser().getId().equals(existingMember.getUser().getId()))) {
                teamMemberRepository.delete(existingMember);
            }
        });

        // Cập nhật danh sách thành viên mới
        existingTeam.setMembers(updatedTeamMembers);

        // Lưu các thành viên đã cập nhật
        teamMemberRepository.saveAll(updatedTeamMembers);

        // Lưu thông tin team đã được cập nhật
        teamRepository.save(existingTeam);

        // Trả về đối tượng TeamResponse thông qua mapper
        return teamMapper.toTeamResponse(existingTeam);
    }

    public void deleteTeam(String teamId){
        teamRepository.deleteById(teamId);
    }

    public TeamResponse uploadteamLogo(String teamId, MultipartFile avatarFile) throws IOException {
        // Tìm user theo id
        Optional<Team> teamOptional = teamRepository.findById(teamId);
        if (teamOptional.isEmpty()) {
            throw new AppException(ErrorCode.TEAM_NOT_EXISTED);
        }

        Team team = teamOptional.get();

        File uploadsDir = new File(uploadDir);
        if (!uploadsDir.exists()) {
            uploadsDir.mkdirs(); // Create the directory if it does not exist
        }

        // Tạo đường dẫn file
        String avatarFileName = team.getName() + "_logo_" + avatarFile.getOriginalFilename();
        Path avatarPath = Paths.get(uploadDir + File.separator + avatarFileName);

        // Lưu file vào thư mục
        Files.write(avatarPath, avatarFile.getBytes());

        // Cập nhật avatar cho user và lưu vào database
        team.setLogo(avatarFileName);

        return teamMapper.toTeamResponse(teamRepository.save(team));
    }

    @Override
    protected TeamRepository getRepository() {
        return teamRepository;
    }

    @Override
    protected PageResponse<List<TeamResponse>> convertToPageResponse(Page<Team> teams, Pageable pageable) {
        List<TeamResponse> response = teams.stream().map(team ->
             TeamResponse.builder()
                    .id(team.getId())
                    .name(team.getName())
                    .creatorId(team.getCreator() != null ? team.getCreator().getId() : null)
                    .teamMembers(team.getMembers().stream().map(member ->
                        MemberResponse.builder()
                                .teamMemberId(member.getId())
                                .userId(member.getUser().getId())
                                .roles(member.getRoles().stream().map(Role::getName).toList()).build()
                    ).collect(Collectors.toSet()))
                     .logo(team.getLogo())
                     .createdAt(team.getCreatedAt())
                     .updatedAt(team.getUpdatedAt())
                    .build()
        ).toList();

        return PageResponse.<List<TeamResponse>>builder()
                .page(pageable.getPageNumber())
                .size(pageable.getPageSize())
                .totalPage(teams.getTotalPages())
                .totalItem(teams.getTotalElements())
                .items(response)
                .build();
    }
}

