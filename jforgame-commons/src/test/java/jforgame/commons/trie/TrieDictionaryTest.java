package jforgame.commons.trie;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link TrieDictionary}
 */
public class TrieDictionaryTest {

    private TrieDictionary dictionary;

    @Before
    public void setUp() {
        dictionary = new TrieDictionary();
    }

    @Test
    public void testAddNode() {
        dictionary.addNode("hello");
        assertTrue(dictionary.containsWords("hello world"));
    }

    @Test
    public void testAddEmptyWord() {
        dictionary.addNode("");
        assertFalse(dictionary.containsWords("test"));
    }

    @Test
    public void testAddNull() {
        // Null handling - normalize returns empty string for null
        dictionary.addNode(null);
        assertFalse(dictionary.containsWords("test"));
    }

    @Test
    public void testAddAllNode() {
        List<String> words = Arrays.asList("hello", "world", "test");
        dictionary.addAllNode(words);
        assertTrue(dictionary.containsWords("say hello"));
        assertTrue(dictionary.containsWords("big world"));
        assertTrue(dictionary.containsWords("this is a test"));
    }

    @Test
    public void testAddAllNodeEmpty() {
        dictionary.addAllNode(Collections.emptyList());
        // Should not throw
    }

    @Test
    public void testAddAllNodeNull() {
        dictionary.addAllNode(null);
        // Should not throw
    }

    @Test
    public void testContainsWords() {
        dictionary.addNode("bad");
        assertTrue(dictionary.containsWords("this is bad"));
        assertFalse(dictionary.containsWords("this is good"));
    }

    @Test
    public void testContainsWordsWithChinese() {
        dictionary.addNode("垃圾");
        assertTrue(dictionary.containsWords("这是垃圾内容"));
        assertFalse(dictionary.containsWords("这是正常内容"));
    }

    @Test
    public void testContainsWordsCaseInsensitive() {
        dictionary.addNode("HELLO");
        assertTrue(dictionary.containsWords("say hello"));
        assertTrue(dictionary.containsWords("say HELLO"));
    }

    @Test
    public void testContainsWordsNull() {
        assertFalse(dictionary.containsWords(null));
    }

    @Test
    public void testContainsExactWord() {
        dictionary.addNode("bad");
        assertTrue(dictionary.containsExactWord("bad"));
        // "badness" is not exactly "bad"
        assertFalse(dictionary.containsExactWord("badness"));
    }

    @Test
    public void testContainsExactWordEmpty() {
        dictionary.addNode("bad");
        assertFalse(dictionary.containsExactWord(""));
        assertFalse(dictionary.containsExactWord(null));
    }

    @Test
    public void testReplaceWords() {
        dictionary.addNode("bad");
        dictionary.addNode("ugly");
        // Note: normalize removes spaces, so input "this is bad and ugly" becomes "thisisbadandugly"
        // Replace works on the normalized string
        String result = dictionary.replaceWords("bad and ugly");
        assertNotNull(result);
        assertTrue(result.contains("***"));
        assertTrue(result.contains("****"));
    }

    @Test
    public void testReplaceWordsNoMatch() {
        dictionary.addNode("bad");
        String result = dictionary.replaceWords("this is good");
        assertEquals("this is good", result);
    }

    @Test
    public void testReplaceWordsNull() {
        String result = dictionary.replaceWords(null);
        assertEquals(null, result);
    }

    @Test
    public void testDeleteNode() {
        dictionary.addNode("bad");
        assertTrue(dictionary.containsWords("this is bad"));

        assertTrue(dictionary.deleteNode("bad"));
        assertFalse(dictionary.containsWords("this is bad"));
    }

    @Test
    public void testDeleteNonExistentNode() {
        assertFalse(dictionary.deleteNode("nonexistent"));
    }

    @Test
    public void testDeleteEmptyNode() {
        assertFalse(dictionary.deleteNode(""));
    }

    @Test
    public void testRebuild() {
        // Add enough words to trigger map container, then rebuild
        List<String> words = Arrays.asList("apple", "banana", "cherry", "date", "elderberry");
        dictionary.addAllNode(words);
        dictionary.rebuild();
        // After rebuild, should still work correctly
        assertTrue(dictionary.containsWords("I like apples"));
    }

    @Test
    public void testGetRoot() {
        TrieNode root = dictionary.getRoot();
        assertNotNull(root);
        assertEquals((char) 0, root.getValue());
    }

    @Test
    public void testMultipleWords() {
        dictionary.addNode("bad");
        dictionary.addNode("worse");
        dictionary.addNode("worst");

        assertTrue(dictionary.containsWords("this is bad"));
        assertTrue(dictionary.containsWords("this is worse"));
        assertTrue(dictionary.containsWords("this is worst"));
        assertFalse(dictionary.containsWords("this is good"));
    }

    @Test
    public void testWordWithSpecialChars() {
        // Special characters should be removed by normalize
        dictionary.addNode("hello!");
        assertTrue(dictionary.containsWords("say hello"));
    }
}
