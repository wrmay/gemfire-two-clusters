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

@SpringBootApplication
@Component
@ImportResource({"classpath*:gemfire-context.xml"})
public class TwoClusterClientApplication {
	private static Logger log = LogManager.getLogger(TwoClusterClientApplication.class);

	@Autowired
	@Qualifier("personRegion")
	Region<Integer,PdxInstance> personRegion;
	
	@Autowired
	RESTClient restClient;
	
	public static void main(String[] args) {
		SpringApplication.run(TwoClusterClientApplication.class, args);
	}

	@PostConstruct
	void init(){
		
		Set<Integer> keys = personRegion.keySetOnServer();
		for(Integer k: keys){
			PdxInstance p = personRegion.get(k);
			restClient.put("customer", k, p);
			log.info("put person: " + k);
		}
		
	}
}
