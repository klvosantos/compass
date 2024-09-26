package com.example.compass;


import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

//@Component
public class ScriptRunner {
    //@PostConstruct
    public void runScript() {
        try{
            ProcessBuilder processBuilder = new ProcessBuilder("bash", "/mnt/e/Marcelo/PROGRAMACAO/Projetos/compass/scripts/init-aws.sh");
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }

            int exitCode = process.waitFor();
            System.out.println("Script exited with code: " + exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}

