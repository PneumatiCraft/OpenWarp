package com.lithium3141.OpenWarp.util;

import java.util.ArrayList;
import java.util.List;

public class Trie<E> {
	protected TrieNode<E> root;
	
	public Trie() {
		this.root = new TrieNode<E>(null);
	}
	
	public TrieNode<E> getRoot() {
		return this.root;
	}
	
	/**
	 * Find the best match for the given key path through this trie.
	 * 
	 * @param keys The ordered sequence of keys to use when walking
	 *             the trie
	 * @return The longest matched sequence of keys existing in this
	 *         trie
	 */
	public String[] getDeepestMatch(String[] keys) {
		TrieNode<E> current = this.root;
		List<String> matched = new ArrayList<String>();
		
		for(int i = 0; i < keys.length; i++) {
			current = current.getChild(keys[i]);
			if(current == null) {
				break;
			}
			matched.add(keys[i]);
		}
		
		// Return last located command
		return matched.toArray(new String[matched.size()]);
	}
	
	/**
	 * Get the value stored at the given key path in this trie.
	 * 
	 * @param path The precise path to use when walking the trie
	 * @return The object associated with the given path in the trie
	 * @throws IndexOutOfBoundsException if no object exists for the given path
	 */
	public E get(String[] path) {
	    TrieNode<E> current = this.root;
	    for(int i = 0; i < path.length; i++) {
	        current = current.getChild(path[i]);
	        if(current == null) {
	            throw new IndexOutOfBoundsException("Key " + path[i] + " not in trie");
	        }
	    }
	    return current.getValue();
	}
	
	/**
	 * Locate the best matching value in this trie for the given key path.
	 * 
	 * @param keys The key path to use when walking the trie
	 * @return The value corresponding to the longest matched sequence in
	 *         the trie
	 */
	public E find(String[] keys) {
	    return this.get(this.getDeepestMatch(keys));
	}
}
