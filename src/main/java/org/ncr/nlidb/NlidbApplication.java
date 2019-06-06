package org.ncr.nlidb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
/*@ComponentScan({"org.ncr.nlp.service"})
@EntityScan("org.ncr.nlp.model")
@EnableJpaRepositories("org.ncr.nlp.repository")
*/
public class NlidbApplication {

	public static void main(String[] args) {
		SpringApplication.run(NlidbApplication.class, args);
	}

}
