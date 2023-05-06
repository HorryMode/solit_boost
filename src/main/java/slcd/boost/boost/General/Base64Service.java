package slcd.boost.boost.General;

import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class Base64Service {

    //Декодирование сообщения, закодированного в формат Base64
    public static String decodeBase64String(String base64String){
        byte[] decodedBytes = Base64.getDecoder().decode(base64String);
        return new String(decodedBytes);
    }
}
