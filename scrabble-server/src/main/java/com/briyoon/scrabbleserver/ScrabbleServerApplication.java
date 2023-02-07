package com.briyoon.scrabbleserver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.briyoon.scrabbleserver.dawg.Dawg;

@SpringBootApplication
public class ScrabbleServerApplication {
    @Autowired
    private Environment env;

	@Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry
					.addMapping("/api/**")
					.allowedOrigins(env.getProperty("client_origin"));
            }
        };
	}

	public static void main(String[] args) {
        // Create dawg if doesnt exist
        if (Files.notExists(Paths.get("scrabble-server/src/main/resources/dawgs/default.ser"))) {
            // Init default DAWG @TODO: support custom word lists
            Dawg dawg = new Dawg();
            try {
                FileOutputStream fout = new FileOutputStream("scrabble-server/src/main/resources/dawgs/default.ser", false);
                ObjectOutputStream oos = new ObjectOutputStream(fout);
                Scanner fileScanner = new Scanner(new File("scrabble-server/src/main/resources/wordlists/sowpods.txt"));

                while (fileScanner.hasNextLine()) {
                    dawg.insert(fileScanner.nextLine());
                }
                dawg.finish();

                oos.writeObject(dawg);

                fileScanner.close();
                oos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

		SpringApplication.run(ScrabbleServerApplication.class, args);
	}

}
