package io.pivotal.datatx.twoclusterclient;

import java.util.concurrent.TimeUnit;

import org.apache.geode.cache.execute.FunctionException;
import org.apache.geode.cache.execute.ResultCollector;
import org.apache.geode.distributed.DistributedMember;

public class AnyResultCollector<T> implements ResultCollector<T,T> {

	private T result;

	@Override
	public T getResult() throws FunctionException {
		return result;
	}

	@Override
	public T getResult(long timeout, TimeUnit unit) throws FunctionException, InterruptedException {
		return result;
	}

	@Override
	public void addResult(DistributedMember memberID, T resultOfSingleExecution) {
		result = resultOfSingleExecution;
	}

	@Override
	public void endResults() {
	}

	@Override
	public void clearResults() {
		result = null;
	}
	

}
