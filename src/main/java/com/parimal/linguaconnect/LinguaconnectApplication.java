package com.parimal.linguaconnect;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.parimal.linguaconnect.entity.Role;
import com.parimal.linguaconnect.repository.RoleRepository;

@SpringBootApplication
public class LinguaconnectApplication implements CommandLineRunner{
	@Autowired
    private RoleRepository roleRepository;

	public static void main(String[] args) {
		SpringApplication.run(LinguaconnectApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
			Role roleAdmin = new Role(1, "ROLE_ADMIN", null);
			Role roleUser = new Role(2, "ROLE_USER", null);

			roleRepository.saveAll(List.of(roleAdmin, roleUser));
	}

}
