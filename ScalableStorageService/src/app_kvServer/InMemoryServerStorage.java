package app_kvServer;

import java.util.concurrent.ConcurrentHashMap;

/**
 * An implementation of the {@link ServerStorage} interface which stores the
 * given key/value pairs in the RAM, with absolutely no persistency guarantees.
 * 
 * The implementation is thread safe, meaning multiple threads may invoke the
 * same {@link InMemoryServerStorage} instance methods without causing
 * race conditions.
 *
 */
public class InMemoryServerStorage implements ServerStorage {

	private ConcurrentHashMap<String, String> storage = new ConcurrentHashMap<String, String>();
	
	@Override
	public void put(String key, String value) {
		storage.put(key, value);
	}

	@Override
	public String get(String key) {
		return storage.get(key);
	}

	@Override
	public boolean containsKey(String key) {
		return storage.containsKey(key);
	}

	@Override
	public void remove(String key) {
		storage.remove(key);
	}

}
