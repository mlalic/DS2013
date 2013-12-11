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
	/**
	 * @return The representation of the maximum possible hash value, e.g.
	 * "FFFF" for a hypothetical hasher which returns 2 byte hashes. 
	 */
	public String getMaxHash();

	/**
	 * @return The representation of the minimum possible hash value, e.g.
	 * "0000" for a hypothetical hasher which returns 2 byte hashes.
	 */
	public String getMinHash();
	
}
