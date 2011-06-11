package com.lithium3141.OpenWarp;

import java.util.HashMap;
import java.util.Map;

public class OWCommandTrieNode {
	protected Map<String, OWCommandTrieNode> children;
	protected OWCommand command;
	
	public OWCommandTrieNode(OWCommand command) {
		this.children = new HashMap<String, OWCommandTrieNode>();
		this.command = command;
	}
	
	/**
	 * Get the command associated with this node.
	 * 
	 * @return The OWCommand for this node 
	 */
	public OWCommand getCommand() {
		return this.command;
	}
	
	/**
	 * Add a new child node to this node for the given key.
	 * 
	 * @param key The key for the new child
	 * @param child The new OWCommandTrieNode to add as a child
	 */
	public void addChild(String key, OWCommandTrieNode child) {
		this.children.put(key, child);
	}
	
	/**
	 * Add a new child node to this node for the given key.
	 * 
	 * @param key The key for the new child
	 * @param command The new OWCommand to add as a child. Autoboxed
	 *                in an OWCommandTrieNode before adding
	 */
	public void addChild(String key, OWCommand command) {
		this.children.put(key, new OWCommandTrieNode(command));
	}
	
	/**
	 * Get the child for the given key.
	 * 
	 * @param key The search key for this node's children
	 * @return The matching OWCommandTrieNode for the given key, or
	 *         null if no such key exists
	 */
	public OWCommandTrieNode getChild(String key) {
		return this.children.get(key);
	}
}
