package ru.maeasoftoworks.normativecontrol.api;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.config.BootstrapMode;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import ru.maeasoftoworks.normativecontrol.api.domain.Role;
import ru.maeasoftoworks.normativecontrol.api.entities.User;
import ru.maeasoftoworks.normativecontrol.api.repositories.UsersRepository;

import java.util.List;

@SpringBootApplication(exclude = org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class)
//@SpringBootApplication
@AllArgsConstructor
public class NormativeControlApiApplication {

	private static final Logger log = LoggerFactory.getLogger(NormativeControlApiApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(NormativeControlApiApplication.class, args);
	}

	private UsersRepository usersRepository;

	@PostConstruct
	private void initDatabase(){
		User user = new User("inspector@urfu.ru", "Кузнецов М.А.", "misha.kuznetsov", "inspector", List.of(Role.INSPECTOR), "UrFU");
		usersRepository.save(user);
		log.info("Inspector account created. Login: inspector@urfu.ru; Password: inspector;");
	}
}
