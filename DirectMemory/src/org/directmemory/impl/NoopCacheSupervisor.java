package org.directmemory.impl;

import java.util.List;
import java.util.Vector;

import org.directmemory.ICacheEntry;
import org.directmemory.ICacheSupervisor;
import org.directmemory.ICacheStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NoopCacheSupervisor implements ICacheSupervisor {
	
	private static Logger logger=LoggerFactory.getLogger(NoopCacheSupervisor.class);

	private long fakeButFastCollectLRU(ICacheStore cache, long bytesToFree) {	

		// not really LRU - that will have to wait
		logger.debug("Attempting LRU collection for " + bytesToFree + " bytes");

		long freedBytes = 0;
		
		for (ICacheEntry entry : cache.entries().values()) {
			cache.delete(entry.getKey());
			freedBytes += entry.getSize();
			logger.debug("Collected LRU entry " + entry.getKey());
			if (freedBytes >= bytesToFree)
				return freedBytes;
		}
		
		logger.debug("No LRU entries to collect for " + bytesToFree + " bytes");
		return freedBytes;

	}
		
	/* (non-Javadoc)
	 * @see org.directcache.impl.ICacheSupervisor#signalLRUCollectionNeeded(org.directcache.impl.DirectCacheImpl, int)
	 */
	public long signalLRUCollectionNeeded(ICacheStore store, long bytesToFree) {
		return fakeButFastCollectLRU(store, bytesToFree);
	}


	/* (non-Javadoc)
	 * @see org.directcache.impl.ICacheSupervisor#signalWeDeleted(java.lang.String)
	 */
	public void signalWeDeleted(String key) {
//		entriesInInsertOrder.remove(key);
	}


	/* (non-Javadoc)
	 * @see org.directcache.impl.ICacheSupervisor#signalWeInserted(org.directcache.impl.CacheEntryImpl)
	 */
	public void signalWeInserted(ICacheEntry newEntry) {
//		entriesInInsertOrder.put(newEntry.getKey(), newEntry);
	}


	/* (non-Javadoc)
	 * @see org.directcache.impl.ICacheSupervisor#signalWeRetrevied(org.directcache.ICacheEntry)
	 */
	public void signalWeRetrieved(ICacheEntry entry) {
		entry.touch();
	}
	/* (non-Javadoc)
	 * @see org.directcache.impl.ICacheSupervisor#signalReset()
	 */
	public void signalReset(){
//		entriesInInsertOrder.reset();
	}

	public long signalCollectExpiredNeeded(ICacheStore store, long bytesToFree) {

		//		List<CacheEntry> expiredList = filter(
		//		having(on(CacheEntry.class).expired())
		//		, entries.values()
		//	);
		
		List<ICacheEntry> expiredList = new Vector<ICacheEntry>();
		
		for (ICacheEntry cacheEntry : store.entries().values()) {
			if (cacheEntry.expired())
				expiredList.add(cacheEntry);
		}

		logger.debug("Collecting " + expiredList.size() +  " expired entries");
		
		long bytesFreed = 0;
		
		for (ICacheEntry expired : expiredList) {
			store.delete(expired.getKey());
			bytesFreed += expired.getSize();
		}

		return bytesFreed;
	}
}
