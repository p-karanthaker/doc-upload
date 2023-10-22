package me.karanthaker.scanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jms.annotation.EnableJms;

@EnableJms
@EntityScan("me.karanthaker.db.entity")
@EnableJpaRepositories("me.karanthaker.db.repository")
@ComponentScan(basePackages = {"me.karanthaker.s3", "me.karanthaker.scanner"})
@SpringBootApplication
public class ScannerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScannerApplication.class, args);
    }
}
