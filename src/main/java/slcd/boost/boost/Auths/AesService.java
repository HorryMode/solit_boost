package slcd.boost.boost.Auths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import slcd.boost.boost.Auths.Exceptions.BadCredentialsException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

@Service
public class AesService {

    @Value("${SkillOver.app.aes-secret}")
    private String key;
    private final static String ALGORITHM = "AES/CBC/PKCS5Padding";

    public String decrypt(String encryptedData) {
        try {
            byte[] decodedData = Base64.getDecoder().decode(encryptedData);
            Cipher cipher = Cipher.getInstance(ALGORITHM);

            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(Arrays.copyOfRange(decodedData, 0, 16));
            decodedData = Arrays.copyOfRange(decodedData, 16, decodedData.length);

            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            byte[] decryptedData = cipher.doFinal(decodedData);
            return new String (decryptedData, StandardCharsets.UTF_8);

        } catch (Exception e) {

            e.printStackTrace();
            throw new BadCredentialsException("Переданны некорректные данные");
        }
    }

    public String encrypt(String dataForEncrypt){
        String plainText = "";
        try {
            byte[] iv = new byte[16];
            SecureRandom random = new SecureRandom();
            random.nextBytes(iv);

            // Создание ключа из строки
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");

            // Инициализация шифрования в режиме CBC с использованием сгенерированного IV
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(iv));

            // Шифрование данных
            byte[] ciphertext = cipher.doFinal(dataForEncrypt.getBytes());

            // Объединение IV и зашифрованных данных в один массив
            byte[] ivAndCiphertext = new byte[iv.length + ciphertext.length];
            System.arraycopy(iv, 0, ivAndCiphertext, 0, iv.length);
            System.arraycopy(ciphertext, 0, ivAndCiphertext, iv.length, ciphertext.length);

            // Кодирование в Base64
            plainText = Base64.getEncoder().encodeToString(ivAndCiphertext);
            return plainText;
        } catch (Exception e){
            e.printStackTrace();
        }
        return plainText;
    }
}
