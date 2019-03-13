package io.pivotal.datatx.twoclusterclient;

import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.geode.cache.Region;
import org.apache.geode.pdx.PdxInstance;
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
@ImportResource({"classpath*:gem-context.xml"})
public class Dump {
	private static Logger log = LogManager.getLogger(Dump.class);

	@Autowired
	@Qualifier("customerRegion")
	Region<String,PdxInstance> customerRegion;
	
	public static void main(String[] args) {
		SpringApplication.run(Dump.class, args);
	}

	@PostConstruct
	void init(){
		
		Set<String> keys = customerRegion.keySetOnServer();
		for(String k: keys){
			Person p =  (Person) customerRegion.get(k).getObject();
			log.info("read {} {}",p.getFirstName(),p.getLastName());
		}
		
	}
}
