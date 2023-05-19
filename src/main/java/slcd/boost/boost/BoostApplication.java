package slcd.boost.boost;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import slcd.boost.boost.General.ControllerScanner;
import slcd.boost.boost.SecurityConfig.SecurityConfig;

import java.io.IOException;

@SpringBootApplication
@Import(SecurityConfig.class)
public class BoostApplication {

	public static void main(String[] args) throws IOException {
		SpringApplication.run(BoostApplication.class, args);
	}

}
