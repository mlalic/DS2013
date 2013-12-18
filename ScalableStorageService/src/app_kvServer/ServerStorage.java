package app_kvServer;

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
}
