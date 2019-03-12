package io.pivotal.pde.sample;

import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.CacheTransactionManager;
import org.apache.geode.cache.CommitConflictException;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;

public class NextIdFunction implements Function {

	/**
	 * Input: Object[] with a single entry 
	 * 
	 * input[0] is a String naming the sequence
	 * 
	 * Returns a single Integer indicating the next id in the sequence
	 */
	
	@Override
	public void execute(FunctionContext context) {
		Region<String,Integer> sequenceRegion = CacheFactory.getAnyInstance().getRegion("sequence");
		
		Object []args=(Object [])context.getArguments();
		String sequenceName = (String) args[0];
		
		int result = 0;
		CacheTransactionManager tm = CacheFactory.getAnyInstance().getCacheTransactionManager();
		try {
			for(int attempt=0;attempt < 10; ++attempt){
				try {
					tm.begin();
					result = 1 + sequenceRegion.get(sequenceName);
					sequenceRegion.put(sequenceName, result);
					tm.commit();
					break;
				} catch(CommitConflictException ccx){
					// no need to roll back
				}
			}
		} finally {
			if (tm.exists()) tm.rollback();
		}
		
		context.getResultSender().lastResult(Integer.valueOf(result));
	}

	@Override
	public boolean hasResult() {
		return true;
	}

	@Override
	public String getId() {
		return NextIdFunction.class.getName();
	}

	@Override
	public boolean isHA() {
		return false;
	}

}
