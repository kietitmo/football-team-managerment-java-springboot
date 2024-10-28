package com.kietitmo.football_team_managerment.services;
import com.kietitmo.football_team_managerment.dto.request.UserCreationRequest;
import com.kietitmo.football_team_managerment.dto.request.UserUpdateRequest;
import com.kietitmo.football_team_managerment.dto.response.PageResponse;
import com.kietitmo.football_team_managerment.dto.response.UserResponse;
import com.kietitmo.football_team_managerment.entities.User;
import com.kietitmo.football_team_managerment.exceptions.AppException;
import com.kietitmo.football_team_managerment.exceptions.ErrorCode;
import com.kietitmo.football_team_managerment.mapper.UserMapper;
import com.kietitmo.football_team_managerment.repositories.RoleRepository;
import com.kietitmo.football_team_managerment.repositories.UserRepository;
import com.kietitmo.football_team_managerment.entities.Role;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
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
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService extends BaseService<User, String, UserResponse, UserRepository>{
    private static final String SEARCH_SPEC_OPERATOR = "(\\w+?)([<:>~!])(.*)";
    UserRepository userRepository;

    RoleRepository roleRepository;

    UserMapper userMapper;

    PasswordEncoder passwordEncoder;

    @NonFinal
    @Value("${file.upload-dir.user-avatar}")
    String uploadDir;

    public UserResponse createUser(UserCreationRequest request){
        if (userRepository.existsByUsername(request.getUsername()))
            throw new AppException(ErrorCode.USER_EXISTED);

        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        HashSet<Role> roles = new HashSet<>();
        Role userRole= roleRepository.findById(com.kietitmo.football_team_managerment.enums.Role.ADMIN.name())
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));
        roles.add(userRole);
        user.setRoles(roles);
        
        return userMapper.toUserResponse(userRepository.save(user));
    }

    public UserResponse updateUser(String userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_EXISTED));

        userMapper.updateUser(user, request);
        if (request.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        if (request.getRoleIds() != null) {
            var roles = roleRepository.findAllById(request.getRoleIds());
            user.setRoles(new HashSet<>(roles));
        }

        return userMapper.toUserResponse(userRepository.save(user));
    }

    public void deleteUser(String userId){
        userRepository.deleteById(userId);
    }

    public List<UserResponse> getUsers(){
        return userRepository.findAll().stream()
                .map(userMapper::toUserResponse).toList();
    }

    @PostAuthorize("returnObject.username == authentication.name")
    public UserResponse getUser(String id){
        return userMapper.toUserResponse(userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_EXISTED)));
    }

    public User findById(String id){
        return userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_EXISTED));
    }

    @PostAuthorize("returnObject.username == authentication.name")
    public UserResponse getProfile(String username){
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return userMapper.toUserResponse(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public String testRole(){
        return "ROLE OK";
    }

    @PreAuthorize("hasAuthority('ALL_PERMISSIONS')")
    public String testPermission(){
        return "PERMISSION OK";
    }

    @Override
    protected UserRepository getRepository() {
        return userRepository;
    }

    @Override
    protected PageResponse<List<UserResponse>> convertToPageResponse(Page<User> users, Pageable pageable) {
        List<UserResponse> response = users.stream().map(user -> UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .dob(user.getDob())
                .roleIds(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()))
                .avatar(user.getAvatar())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build()).toList();

        return PageResponse.<List<UserResponse>>builder()
                .page(pageable.getPageNumber())
                .size(pageable.getPageSize())
                .totalPage(users.getTotalPages())
                .totalItem(users.getTotalElements())
                .items(response)
                .build();
    }

    public UserResponse uploadUserAvatar(String userId, MultipartFile avatarFile) throws IOException {
        // Tìm user theo id
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }

        User user = userOptional.get();

        File uploadsDir = new File(uploadDir);
        if (!uploadsDir.exists()) {
            uploadsDir.mkdirs(); // Create the directory if it does not exist
        }

        // Tạo đường dẫn file
        String avatarFileName = user.getUsername() + "_avatar_" + avatarFile.getOriginalFilename();
        Path avatarPath = Paths.get(uploadDir + File.separator + avatarFileName);

        // Lưu file vào thư mục
        Files.write(avatarPath, avatarFile.getBytes());

        // Cập nhật avatar cho user và lưu vào database
        user.setAvatar(avatarFileName);

        return userMapper.toUserResponse(userRepository.save(user));
    }
}

