package io.pivotal.datatx.twoclusterclient;

import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.geode.cache.Region;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Component;

import io.pivotal.pde.sample.Person;

@SpringBootApplication
@Component
@ImportResource({"classpath*:gemfire-context.xml"})
public class Dump {
	private static Logger log = LogManager.getLogger(Dump.class);

	@Autowired
	@Qualifier("customerRegion")
	Region<Integer,Person> customerRegion;
	
	public static void main(String[] args) {
		SpringApplication.run(Dump.class, args);
	}

	@PostConstruct
	void init(){
		
		Set<Integer> keys = customerRegion.keySetOnServer();
		for(Integer k: keys){
			Person p = customerRegion.get(k);
			log.info("read {} {}",p.getFirstName(),p.getLastName());
		}
		
	}
}
