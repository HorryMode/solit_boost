package slcd.boost.boost;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import slcd.boost.boost.Services.Base64Service;

import java.util.Base64;

@SpringBootTest
class BoostApplicationTests {

	@Value("${internal.datasource.serviceKey-base64}")
	private String base64String;

	//Проверка декодирования из base64
	@Test
	void testBase64Decode() {
		String decodedString = Base64Service.decodeBase64String(base64String);
		assert(decodedString).equals("internal-service-key");
	}

}
