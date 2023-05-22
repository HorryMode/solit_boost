package slcd.boost.boost.Auths.Exceptions;

import lombok.Getter;

@Getter
public class RefreshTokenExpireException extends RuntimeException{

    String refreshToken;

    public RefreshTokenExpireException(String message, String refreshToken){
        super(message);
        this.refreshToken = refreshToken;
    }
}
