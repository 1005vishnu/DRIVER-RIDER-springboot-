package com.ridehailing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RidehailingApplication {
	public static void main(String[] args) {
		SpringApplication.run(RidehailingApplication.class, args);
		System.out.println("Ride-Hailing API is running on http://localhost:8080");
	}
}

