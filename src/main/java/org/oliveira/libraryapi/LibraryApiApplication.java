package org.oliveira.libraryapi;

import org.modelmapper.ModelMapper;
import org.oliveira.libraryapi.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@SpringBootApplication
@EnableScheduling
public class LibraryApiApplication {

	@Bean
	public ModelMapper modelMapper(){
		return new ModelMapper();
	}

//	Parte para testar envio de email para mailtrap.io
//	@Autowired
//	private EmailService emailService;
//
//	@Bean
//	public CommandLineRunner runner(){
//		return args -> {
//			List<String> emails = Arrays.asList("0046131e35-6322ef@inbox.mailtrap.io");
//			emailService.sendMails(emails, "Testando serviço de email");
//			System.out.println("Emails enviados");
//		};
//	}

	public static void main(String[] args) {
		SpringApplication.run(LibraryApiApplication.class, args);

	}

}
