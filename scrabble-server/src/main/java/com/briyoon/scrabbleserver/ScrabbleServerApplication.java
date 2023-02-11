package com.briyoon.scrabbleserver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.briyoon.scrabbleserver.dawg.Dawg;

@SpringBootApplication
public class ScrabbleServerApplication {
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