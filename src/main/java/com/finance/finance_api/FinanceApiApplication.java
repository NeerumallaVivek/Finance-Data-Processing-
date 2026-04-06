package com.finance.finance_api;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class FinanceApiApplication {

	public static void main(String[] args) {
		// Load configuration from .env file
		try {
			// First try to load from the current directory
			Dotenv dotenv = Dotenv.configure()
					.ignoreIfMissing()
					.load();

			// If the key is missing, check the sub-directory
			if (dotenv.get("SPRING_DATASOURCE_URL") == null) {
				dotenv = Dotenv.configure()
						.directory("./finance-api/finance-api")
						.ignoreIfMissing()
						.load();
			}

			// Map variables to System Properties for Spring Boot to see
			dotenv.entries().forEach(entry -> {
				if (entry.getValue() != null && !entry.getValue().isEmpty()) {
					System.setProperty(entry.getKey(), entry.getValue());
				}
			});

			if (System.getProperty("SPRING_DATASOURCE_URL") != null) {
				System.out.println("✅ Configuration loaded from .env");
			} else {
				System.out.println("ℹ️ No .env found, using system environment variables.");
			}
		} catch (Exception e) {
			System.err.println("⚠️ Warning during .env loading: " + e.getMessage());
		}

		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		SpringApplication.run(FinanceApiApplication.class, args);
	}
}
