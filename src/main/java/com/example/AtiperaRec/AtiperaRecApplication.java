package com.example.AtiperaRec;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.w3c.dom.Document;

import javax.lang.model.util.Elements;

@EnableWebMvc
@SpringBootApplication
public class AtiperaRecApplication {

	public static void main(String[] args) {
		SpringApplication.run(AtiperaRecApplication.class, args);
	}

}
