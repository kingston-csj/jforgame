package jforgame.commons.trie;


import java.util.ArrayList;
import java.util.List;

/**
 * Trie tree, also known as prefix tree, is a common data structure in algorithms. It is mainly used to solve the problem of associating complete words through prefixes.
 * Can be used for dirty word detection, friend fuzzy query, etc.
 * @since 2.4.0
 */
public class TrieDictionary {

    /**
     * Threshold, when the number of child nodes is less than or equal to the threshold, convert map container to array
     */
    private static final int THRESHOLD = 3;
    /**
     * Prefix root node
     */
    private final TrieNode root;

    public TrieDictionary() {
        this.root = new TrieNode((char) 0);
    }

    public void addNode(String word) {
        word = normalize(word);
        if (word.isEmpty()) {
            return;
        }
        root.addChild(word, 0);
    }

    /**
     * Deletes a word node
     * @param word the word to delete
     * @return whether the deletion was successful
     * @since 2.5.0
     */
    public boolean deleteNode(String word) {
        word = normalize(word);
        if (word.isEmpty()) {
            return false;
        }
        return deleteNodeRecursive(root, word, 0);
    }

    /**
     * Recursively deletes a word node
     * @param node the current node
     * @param word the word to delete
     * @param index the current character index being processed
     * @return whether the deletion was successful
     */
    private boolean deleteNodeRecursive(TrieNode node, String word, int index) {
        // If all characters have been processed
        if (index >= word.length()) {
            // If the current node is a leaf node, remove the leaf marker
            if (node.isLeaf()) {
                node.setLeaf(false);
                return true;
            }
            return false;
        }

        char currentChar = word.charAt(index);
        TrieNode childNode = node.getChild(currentChar);
        
        if (childNode == null) {
            // Word does not exist
            return false;
        }

        // Recursively delete the next character
        boolean deleted = deleteNodeRecursive(childNode, word, index + 1);

        if (deleted) {
            // If the child node was deleted and the current child node has no other child nodes and is not a leaf node, delete the current child node
            if (!childNode.isLeaf() && childNode.getChildren().isEmpty()) {
                node.removeChild(currentChar);
            }
        }
        
        return deleted;
    }

    /**
     * Checks if the specified string contains sensitive words
     * @param word the string to check
     * @return whether it contains sensitive words
     */
    public boolean containsWords(String word) {
        word = normalize(word);
        for (int i = 0; i < word.length(); i++) {
            if (root.hasPrefix(word, i) != -1) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the dictionary exactly matches a word
     * For example, if "张无" is a sensitive word, but "张无忌" should not be
     * @param word the word to check
     * @return whether it exactly matches
     * @since 2.5.0
     */
    public boolean containsExactWord(String word) {
        word = normalize(word);
        if (word.isEmpty()) {
            return false;
        }
        return root.hasExactWord(word, 0);
    }

    /**
     * Replaces sensitive words with character '*', if any
     * @param content the string to process
     * @return the converted string
     */
    public String replaceWords(String content) {
        String normalizedString = normalize(content);
        List<int[]> indexList = new ArrayList<>();
        int end = -1, len = normalizedString.length();
        for (int i = 0; i < len; ) {
            if ((end = root.hasPrefix(normalizedString, i)) != -1) {
                indexList.add(new int[]{i, end});
                i = end;
            } else {
                i++;
            }
        }
        if (indexList.isEmpty()) {
            return content;
        } else {
            StringBuilder sb = new StringBuilder(normalizedString);
            for (int[] indexArray : indexList) {
                for (int i = indexArray[0]; i < indexArray[1]; i++) {
                    sb.setCharAt(i, '*');
                }
            }
            return sb.toString();
        }
    }

    /**
     * String preprocessing, convert English to lowercase (remove special symbols, keep only letters, numbers, Chinese)
     *
     * @return the converted string
     */
    private String normalize(String dirtyWord) {
        if (dirtyWord == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < dirtyWord.length(); i++) {
            char c = dirtyWord.charAt(i);
            if (Character.isLetterOrDigit(c) || isChineseCharacter(c)) {
                sb.append(Character.toLowerCase(c));
            }
        }
        return sb.toString();
    }

    private boolean isChineseCharacter(char c) {
        Character.UnicodeBlock block = Character.UnicodeBlock.of(c);
        return block == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || block == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || block == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS;
    }

    public TrieNode getRoot() {
        return root;
    }

    /**
     * After the entire tree is built, restructure the child nodes
     * If the number of child nodes of a node is less than the threshold, convert the map container to array
     */
    public void rebuild() {
        rebuildChildren(root);
    }

    private void rebuildChildren(TrieNode node) {
        if (node.children instanceof MapNodeContainer) {
            if (node.children.size() <= THRESHOLD) {
                node.children = node.children.transform();
            }
        }
        // Recursively process child nodes
        node.children.getAll().forEach(this::rebuildChildren);
    }

}