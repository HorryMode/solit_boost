package slcd.boost.boost;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;
import slcd.boost.boost.SecurityConfig.SecurityConfig;

import java.io.IOException;

@SpringBootApplication
@Import(SecurityConfig.class)
@EnableScheduling
public class BoostApplication {

	public static void main(String[] args) throws IOException {
		SpringApplication.run(BoostApplication.class, args);
	}

}
