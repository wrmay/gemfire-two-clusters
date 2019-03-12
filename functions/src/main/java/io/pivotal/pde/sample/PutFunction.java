package io.pivotal.pde.sample;

import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.CacheTransactionManager;
import org.apache.geode.cache.CommitConflictException;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PutFunction implements Function {
	private static Logger log = LogManager.getLogger(PutFunction.class);
	
	/**
	 * Input: Object[] with 3 entries 
	 * 
	 * input[0] is the name of the region
	 * input[1] is the key
	 * input[2] is the value
	 * 
	 * Returns a single Integer indicating the next id in the sequence
	 */
	
	@Override
	public void execute(FunctionContext context) {		
		Object []args=(Object [])context.getArguments();
		if (args.length != 3) {
			String msg = "Put Function requires exactly 3 arguments but " + args.length + " were provided.";
			log.warn(msg);
			throw new RuntimeException(msg);
		}
		
		Region region = CacheFactory.getAnyInstance().getRegion((String) args[0]);
		if (region == null){
			String msg = String.format("The \"%s\" region does not exist.", args[0]);
		}
		
		Object result = region.put(args[1], args[2]);
		
		context.getResultSender().lastResult(result);
	}

	@Override
	public boolean hasResult() {
		return true;
	}

	@Override
	public String getId() {
		return PutFunction.class.getName();
	}

	@Override
	public boolean isHA() {
		return true;
	}


}
