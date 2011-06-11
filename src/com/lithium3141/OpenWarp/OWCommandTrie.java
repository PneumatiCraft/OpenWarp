package com.lithium3141.OpenWarp;

public class OWCommandTrie {
	protected OWCommandTrieNode root;
	
	public OWCommandTrie() {
		this.root = new OWCommandTrieNode(null);
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
	public OWCommand getDeepestMatch(String[] keys) {
		OWCommandTrieNode current = this.root;
		OWCommand[] commands = new OWCommand[keys.length];
		
		for(int i = 0; i < keys.length; i++) {
			current = current.getChild(keys[i]);
			if(current == null) {
				break;
			}
			commands[i] = current.getCommand();
		}
		
		// Return last located command
		for(int i = keys.length - 1; i >= 0; i--) {
			if(commands[i] != null) {
				return commands[i];
			}
		}
		return null;
	}
}
