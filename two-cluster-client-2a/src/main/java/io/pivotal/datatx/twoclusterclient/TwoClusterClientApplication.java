package io.pivotal.datatx.twoclusterclient;

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
	@Qualifier("pool2")
	Pool pool;
	
	public static void main(String[] args) {
		SpringApplication.run(TwoClusterClientApplication.class, args);
	}

	
	private Integer nextId(){
		Object []args = new String[]{"person"};
		Execution<Object[],Integer,Integer> exec = FunctionService.onServer(pool)
				.setArguments(args)
				.withCollector(new AnyResultCollector<Integer>());
		
		ResultCollector<Integer,Integer> rc = exec.execute("io.pivotal.pde.sample.NextIdFunction");
		return rc.getResult();
	}
	
	@PostConstruct
	void init(){
		
		Person p = new Person();		
		
		for(int i=0;i<100; ++i){
			p = new Person();
			p.fake();
			p.setId(this.nextId());
			personRegion.put(p.getId(), p);
		}
	}
}
