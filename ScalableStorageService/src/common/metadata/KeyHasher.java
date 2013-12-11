package common.metadata;

/**
 * All classes which provide hashes of keys need to implement this interface. 
 *
 */
public abstract interface KeyHasher {

	/**
	 * Hashes the given key. The returned string is the position of the key in the
	 * hash circle.
	 * @param key The key to be placed in the circle based on its hash
	 * @return A hash of the given key
	 */
	public String getKeyHash(String key);
}
