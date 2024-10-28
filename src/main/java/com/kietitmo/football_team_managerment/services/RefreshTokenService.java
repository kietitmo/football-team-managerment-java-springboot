package com.kietitmo.football_team_managerment.services;

import com.kietitmo.football_team_managerment.entities.RefreshToken;
import com.kietitmo.football_team_managerment.exceptions.AppException;
import com.kietitmo.football_team_managerment.repositories.RefreshTokenRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.kietitmo.football_team_managerment.exceptions.ErrorCode.TOKEN_NOT_EXISTED;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RefreshTokenService {
    RefreshTokenRepository refreshTokenRepository;

    RefreshToken getRefreshTokenByUsername(String username) {
        Optional<RefreshToken> optional = refreshTokenRepository.findByUsername(username);
        if (optional.isEmpty()) {
            throw new AppException(TOKEN_NOT_EXISTED);
        }

        return optional.get();
    }

    public String save(RefreshToken token) {
        Optional<RefreshToken> optional = refreshTokenRepository.findByUsername(token.getUser().getUsername());
        if (optional.isEmpty()) {
            refreshTokenRepository.save(token);
            return token.getId();
        } else {
            RefreshToken t = optional.get();
            t.setToken(token.getToken());
            refreshTokenRepository.save(t);
            return t.getId();
        }
    }


    public void delete(String username) {
        RefreshToken token = getRefreshTokenByUsername(username);
        refreshTokenRepository.delete(token);
    }

    public boolean isRefreshTokenExists(String username) {
        Optional<RefreshToken> optional = refreshTokenRepository.findByUsername(username);
        return optional.isPresent();
    }
}
