package slcd.boost.boost.Syncs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import slcd.boost.boost.General.Exceptions.ResourceNotFoundException;
import slcd.boost.boost.Syncs.DTOs.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class InternalApiService {

    private static String token;
    
    @Value("${internal.url}")
    private String internal_url;
    @Value("${internal.credentials}")
    private String internal_credentials;

    private LocalDateTime authTime = null;

    private final static int AUTH_RATE = 10;


    private final static Logger LOGGER = LoggerFactory.getLogger(InternalApiService.class);

    public void authorizeInternal(){

        if(authTime == null)
            authTime = LocalDateTime.now();
        else if(!checkLastAuth()){
            return;
        }

        String url = internal_url.concat("api/auth");
        RestTemplate authorizeRequest = new RestTemplate();

        InternalAuthorizeRequest request = new InternalAuthorizeRequest(internal_credentials);
        HttpEntity< InternalAuthorizeRequest > requestHttpEntity = new HttpEntity<>(request);

        ResponseEntity<InternalAuthorizeResponse> response = authorizeRequest.exchange(
                        url,
                        HttpMethod.POST,
                        requestHttpEntity,
                        InternalAuthorizeResponse.class
        );
        if (response.getStatusCode() == HttpStatus.CREATED){
            token = Objects.requireNonNull(response.getBody()).getToken();
        }
    }

    public InternalSubdivisionResponse getInternalUsersBySubdivision(String subdivisionUuid) {
        authorizeInternal();

        String url = String.format(internal_url + "api/subdivisions/%s", subdivisionUuid);
        RestTemplate authorizeRequest = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<InternalSubdivisionResponse> response = authorizeRequest.exchange(
                url,
                HttpMethod.GET,
                entity,
                InternalSubdivisionResponse.class
        );
        if (response.getStatusCode() == HttpStatus.OK){
            LOGGER.info("Отдел с uuid " + subdivisionUuid + " найден");
            return response.getBody();
        }
        else{
            LOGGER.info("Отдел с uuid " + subdivisionUuid + " не найден");
            return null;
        }
    }

    public InternalUserResponse  getInternalUser(String uuid){
        authorizeInternal();

        String url = String.format(internal_url + "api/users/%s", uuid);
        RestTemplate authorizeRequest = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<InternalUserResponse> response = authorizeRequest.exchange(
                url,
                HttpMethod.GET,
                entity,
                InternalUserResponse.class
        );
        if (response.getStatusCode() == HttpStatus.OK){
            LOGGER.info("Пользователь с " + uuid + " найден");
            return response.getBody();
        }
        else{
            LOGGER.info("Пользователь с " + uuid + " не найден");
            return null;
        }
    }

    public List<InternalPostResponse> getInternalPosts(){
        authorizeInternal();

        String url = String.format(internal_url + "api/posts");
        RestTemplate authorizeRequest = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<List<InternalPostResponse>> response = authorizeRequest.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<InternalPostResponse>>() {}
        );
        if (response.getStatusCode() == HttpStatus.OK){
            LOGGER.info("Должности найдены");
            return response.getBody();
        }
        else{
            LOGGER.info("Ошибка при получении должностей из Internal");
            return null;
        }
    }

    public List<InternalProductResponse> getInternalProducts(){
        authorizeInternal();

        String url = String.format(internal_url + "api/products");
        RestTemplate authorizeRequest = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<List<InternalProductResponse>> response = authorizeRequest.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}
        );
        if (response.getStatusCode() == HttpStatus.OK){
            LOGGER.info("Продукты/проекты найдены");
            return response.getBody();
        }
        else{
            LOGGER.info("Ошибка при получении продуктов из Internal");
            return null;
        }
    }

    private boolean checkLastAuth(){
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(authTime, now);

        return duration.toMinutes() >= AUTH_RATE;
    }
}
