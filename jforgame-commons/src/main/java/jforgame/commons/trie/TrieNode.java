package jforgame.commons.trie;


import java.util.Collection;

/**
 * Trie tree node
 * Each node represents a character, and the node contains multiple child nodes
 * @since 2.4.0
 */
public class TrieNode {

    /**
     * Character value of the current node
     */
    char val;
    /**
     * All child nodes, if the number of child nodes of each node is relatively small, this will cause memory waste, can be degraded to array
     */
    NodeContainer children = new MapNodeContainer();

    /**
     * Whether it is a leaf node, i.e., whether it is the last character of a dirty word
     */
    boolean isLeaf;

    public TrieNode(char val) {
        this.val = val;
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public void setLeaf(boolean leaf) {
        isLeaf = leaf;
    }

    public void addChild(CharSequence cs, int index) {
        if (index >= cs.length()) {
            setLeaf(true);
            return;
        }
        char val = cs.charAt(index);
        TrieNode child = children.get(val);
        if (child == null) {
            child = new TrieNode(val);
            children.add(child);
        }
        child.addChild(cs, index + 1);
    }

    /**
     * Removes a child node
     * @param character the character to remove
     * @return whether the removed node exists
     * @since 2.5.0
     */
    public boolean removeChild(char character) {
        return children.remove(character) != null;
    }

    public int hasPrefix(CharSequence cs, int idx) {
        if (idx >= cs.length()) {
            if (isLeaf()) {
                return idx;
            } else {
                return -1;
            }
        }
        char val = cs.charAt(idx);
        TrieNode child = children.get(val);
        int findIndex = -1;
        if (child != null) {
            findIndex = child.hasPrefix(cs, idx + 1);
        }
        if (findIndex != -1) {
            return findIndex;
        } else if (isLeaf()) {
            return idx;
        } else {
            return -1;
        }
    }

    /**
     * Checks if it exactly matches a word
     * @param cs the string to check
     * @param idx the current character index being processed
     * @return whether it exactly matches
     * @since 2.5.0
     */
    public boolean hasExactWord(CharSequence cs, int idx) {
        if (idx >= cs.length()) {
            return isLeaf();
        }
        char val = cs.charAt(idx);
        TrieNode child = children.get(val);
        if (child == null) {
            return false;
        }
        return child.hasExactWord(cs, idx + 1);
    }

    public TrieNode getChild(char c) {
        return children.get(c);
    }

    public Collection<TrieNode> getChildren() {
        return children.getAll();
    }

    public char getValue() {
        return val;
    }


}