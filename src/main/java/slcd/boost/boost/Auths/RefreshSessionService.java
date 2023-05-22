package slcd.boost.boost.Auths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import slcd.boost.boost.Auths.Entities.RefreshSessionEntity;
import slcd.boost.boost.Auths.Exceptions.RefreshTokenExpireException;
import slcd.boost.boost.Auths.Repos.RefreshSessionRepository;
import slcd.boost.boost.Users.Entities.UserEntity;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RefreshSessionService {

    @Autowired
    private RefreshSessionRepository refreshSessionRepository;

    @Value("${SkillOver.app.refreshTokenExpirationMs}")
    private long refreshTokenExpiration;

    public String generateRefreshToken(String fingerprint, UserEntity user){
        RefreshSessionEntity refreshSession = findByFingerprint(fingerprint);

        if(refreshSession != null)
            delete(refreshSession);

        RefreshSessionEntity newRefreshSession
                = newRefreshSessionEntity(fingerprint, user);

        save(newRefreshSession);

        return newRefreshSession.getRefreshToken().toString();
    }

    public UserEntity validateRefreshToken(String fingerprint, String refreshToken){
        RefreshSessionEntity refreshSession = findByFingerprintAndRefreshToken(fingerprint, refreshToken);

        int comparison = refreshSession
                .getExpires()
                .compareTo(LocalDateTime.now());

        if(comparison < 0){
            throw new RefreshTokenExpireException(
                    AuthConstants.REFRESH_TOKEN_EXPIRE_MESSAGE,
                    refreshToken
            );
        }

        return refreshSession.getUser();
    }

    public void save(RefreshSessionEntity refreshSessionEntity){
        refreshSessionRepository.save(refreshSessionEntity);
    }

    public void delete(RefreshSessionEntity refreshSessionEntity) {
        refreshSessionRepository.delete(refreshSessionEntity);
    }

    //Фомирование нового рефреш-сессии
    public RefreshSessionEntity newRefreshSessionEntity(String fingerprint, UserEntity user){
        RefreshSessionEntity refreshSession = new RefreshSessionEntity();
        refreshSession.setRefreshToken(UUID.randomUUID());
        refreshSession.setUser(user);
        refreshSession.setFingerprint(fingerprint);
        refreshSession.setCreated(LocalDateTime.now());
        LocalDateTime expires = LocalDateTime.now().plus(
                Duration.ofMillis(refreshTokenExpiration)
        );
        refreshSession.setExpires(expires);

        return refreshSession;
    }

    public RefreshSessionEntity findByFingerprint(String fingerprint){
        return refreshSessionRepository
                .findByFingerprint(fingerprint);
    }

    public RefreshSessionEntity findByFingerprintAndRefreshToken(String fingerprint, String refreshToken){
        return refreshSessionRepository.
                findByFingerprintAndRefreshToken(
                        fingerprint,
                        UUID.fromString(refreshToken)
                ).orElseThrow();
    }
}
