package com.mikerusoft.euroleague;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class EuroleagueApplication {

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("IST"));
		SpringApplication.run(EuroleagueApplication.class, args);
	}

}
