package br.edu.senac.recursos;

import java.time.Clock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class RecursosApplication {
    public static void main(String[] args) { SpringApplication.run(RecursosApplication.class, args); }
    @Bean Clock clock() { return Clock.systemUTC(); }
}
