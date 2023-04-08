package slcd.boost.boost;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import slcd.boost.boost.Configs.SecurityConfig;

@SpringBootApplication
@Import(SecurityConfig.class)
public class BoostApplication {

	public static void main(String[] args) {
		SpringApplication.run(BoostApplication.class, args);
	}

}
