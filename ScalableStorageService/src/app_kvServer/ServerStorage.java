package app_kvServer;

import java.util.Map;

/**
 * An interface that should be implemented to provide the server storage
 * functionality. 
 *
 */
public interface ServerStorage {
	public void put(String key, String value);
	public String get(String key);
	public void remove(String key);
	public boolean containsKey(String key);
	/**
	 * @return A representation of all the stored data as a {@link Map}
	 * This method's implementation may potentially consume a lot of memory.
	 */
	public Map<String, String> getAllData();
}
