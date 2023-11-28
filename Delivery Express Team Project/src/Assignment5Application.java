package com.group11.assignment5;

import com.group11.assignment5.service.CommandService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
@SpringBootApplication
public class Assignment5Application implements CommandLineRunner{
	private CommandService commandService;
	@Override
	public void run(String... args){

		commandService.commandLoop();

	}
	public static void main(String[] args) {
		SpringApplication.run(Assignment5Application.class, args);
	}

}
