package com.lithium3141.OpenWarp;

import java.util.HashMap;
import java.util.Map;

public class TrieNode<E> {
	protected Map<String, TrieNode<E>> children;
	protected E value;
	
	public TrieNode(E value) {
		this.children = new HashMap<String, TrieNode<E>>();
		this.value = value;
	}
	
	/**
	 * Get the value associated with this node.
	 * 
	 * @return The OWCommand for this node 
	 */
	public E getValue() {
		return this.value;
	}
	
	/**
	 * Set a new value for this node.
	 * 
	 * @param value The new value
	 */
	public void setValue(E value) {
		this.value = value;
	}
	
	/**
	 * Add a new child node to this node for the given key.
	 * 
	 * @param key The key for the new child
	 * @param child The new OWCommandTrieNode to add as a child
	 */
	public void setChild(String key, TrieNode<E> child) {
		this.children.put(key, child);
	}
	
	/**
	 * Add a new child node to this node for the given key.
	 * 
	 * @param key The key for the new child
	 * @param command The new OWCommand to add as a child. Autoboxed
	 *                in an OWCommandTrieNode before adding
	 */
	public void setChild(String key, E value) {
		this.children.put(key, new TrieNode<E>(value));
	}
	
	/**
	 * Get the child for the given key.
	 * 
	 * @param key The search key for this node's children
	 * @return The matching OWCommandTrieNode for the given key, or
	 *         null if no such key exists
	 */
	public TrieNode<E> getChild(String key) {
		return this.children.get(key);
	}
}
