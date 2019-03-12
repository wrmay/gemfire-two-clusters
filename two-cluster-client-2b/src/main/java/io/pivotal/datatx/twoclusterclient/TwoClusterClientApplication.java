package io.pivotal.datatx.twoclusterclient;

import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.Pool;
import org.apache.geode.cache.execute.Execution;
import org.apache.geode.cache.execute.FunctionService;
import org.apache.geode.cache.execute.ResultCollector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Component;

import io.pivotal.pde.dataserializable.sample.DataSerializablePerson;
import io.pivotal.pde.sample.Person;

@SpringBootApplication
@Component
@ImportResource({"classpath*:gemfire-context.xml"})
public class TwoClusterClientApplication {
	private static Logger log = LogManager.getLogger(TwoClusterClientApplication.class);

	@Autowired
	@Qualifier("personRegion")
	Region<Integer,DataSerializablePerson> personRegion;
	
	@Autowired
	@Qualifier("pool2")
	Pool pool;
	
	public static void main(String[] args) {
		SpringApplication.run(TwoClusterClientApplication.class, args);
	}

	
	
	@PostConstruct
	void init(){
		Set<Integer> keys = personRegion.keySetOnServer(); 
		for(Integer k: keys){
			DataSerializablePerson p = personRegion.get(k);
			
			Object []args = new Object []{"customer",k, p};
			Execution<Object[],DataSerializablePerson,DataSerializablePerson> exec = 
					FunctionService.onServer(pool).setArguments(args).withCollector(new AnyResultCollector<DataSerializablePerson>());
			ResultCollector<DataSerializablePerson,DataSerializablePerson> rc = exec.execute("io.pivotal.pde.sample.PutFunction");
			Person person = rc.getResult();
			log.info("put {}",person);
		}
	}
}
