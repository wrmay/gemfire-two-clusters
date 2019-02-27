package io.pivotal.datatx.twoclusterclient;

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
public class TwoClusterClientApplication {
	private static Logger log = LogManager.getLogger(TwoClusterClientApplication.class);

	@Autowired
	@Qualifier("personRegion")
	Region<Integer,Person> personRegion;
	
	@Autowired
	@Qualifier("customerRegion")
	Region<Integer,Person> customerRegion;
	
	public static void main(String[] args) {
		SpringApplication.run(TwoClusterClientApplication.class, args);
	}

	@PostConstruct
	void init(){
		Person p = personRegion.get(1);
		log.info("got {} from person region in cluster 1", p);
		
		customerRegion.put(1, p);
		log.info("put {} into customer region in cluster 2", p);
	}
}
