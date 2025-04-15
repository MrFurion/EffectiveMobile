package by.effective.mobile.eb;

import by.effective.mobile.eb.env.EnvLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
@Profile("dev")
public class EbApplication {
	public static void main(String[] args) {
		EnvLoader.loadEnv();
		SpringApplication.run(EbApplication.class, args);
	}

}
