package org.directmemory.cache;



import org.directmemory.store.HeapStore;
import org.directmemory.store.AbstractQueuedStore;
import org.directmemory.store.OffHeapStore;

public class CacheManager2  {
	private HeapStore entries;
	
	public CacheManager2() {
		entries = new HeapStore();
		entries.limit = 1000;
		AbstractQueuedStore other = new OffHeapStore(); 
		entries.nextStore = other;
	}

	public CacheManager2(AbstractQueuedStore secondLevel) {
		entries = new HeapStore();
		entries.limit = 1000;
		entries.nextStore = secondLevel;
		secondLevel.topStore = entries;
	}

	public void limit(int limit) {
		entries.limit = limit;
	}
	
	public synchronized CacheEntry getEntry(String key) {
		return entries.get(key);
	}
	public synchronized Object get(String key) {
		CacheEntry entry = getEntry(key);
		if (entry != null) {
			return entry.object;
		}
		return null;
	}
	public synchronized CacheEntry remove(String key) {
		return entries.remove(key);
	}
	public synchronized CacheEntry remove(CacheEntry entry) {
		return entries.remove(entry.key);
	}
	public synchronized CacheEntry put(String key, Object object) {
		CacheEntry entry = new CacheEntry();
		entry.key = key;
		entry.object = object;
		return put(entry);
	}
	public synchronized CacheEntry put(CacheEntry entry) {
		return entries.put(entry.key, entry);
	}
}
