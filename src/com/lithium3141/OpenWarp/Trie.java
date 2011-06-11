package com.lithium3141.OpenWarp;

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
	 * @return The OWCommand associated with the longest valid prefix
	 *         of the given key path, or null if no valid OWCommand
	 *         for the given path was found
	 */
	public E getDeepestMatch(String[] keys) {
		TrieNode<E> current = this.root;
		List<E> values = new ArrayList<E>();
		
		for(int i = 0; i < keys.length; i++) {
			current = current.getChild(keys[i]);
			if(current == null) {
				break;
			}
			values.add(current.getValue());
		}
		
		// Return last located command
		for(int i = values.size() - 1; i >= 0; i--) {
			if(values.get(i) != null) {
				return values.get(i);
			}
		}
		return null;
	}
}
