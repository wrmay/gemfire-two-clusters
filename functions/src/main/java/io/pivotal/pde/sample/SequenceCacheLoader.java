package io.pivotal.pde.sample;

import org.apache.geode.cache.CacheLoader;
import org.apache.geode.cache.CacheLoaderException;
import org.apache.geode.cache.LoaderHelper;

public class SequenceCacheLoader implements CacheLoader {

	@Override
	public Object load(LoaderHelper helper) throws CacheLoaderException {
		return Integer.valueOf(0);
	}

}
