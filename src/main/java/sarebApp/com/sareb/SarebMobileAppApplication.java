package sarebApp.com.sareb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication(scanBasePackages = {"sarebApp.com.sareb"})
@EnableSwagger2
public class SarebMobileAppApplication {
//ATBB3ac3wwxZHrcmD3VtY2zwTvRS83B9ADE4
	public static void main(String[] args) {
		SpringApplication.run(SarebMobileAppApplication.class, args);
	}

//	@Bean
//	BCryptPasswordEncoder passwordEncoder(){
//		return new BCryptPasswordEncoder();
//	}

}
