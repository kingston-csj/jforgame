package jforgame.commons.trie;

import org.junit.Before;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link TrieNode}
 */
public class TrieNodeTest {

    private TrieNode root;

    @Before
    public void setUp() {
        root = new TrieNode((char) 0);
    }

    @Test
    public void testGetValue() {
        TrieNode node = new TrieNode('a');
        assertEquals('a', node.getValue());
    }

    @Test
    public void testAddChild() {
        root.addChild("ab", 0);
        assertFalse(root.isLeaf());

        // After adding "ab", root->'a' should exist
        TrieNode aNode = root.getChild('a');
        assertNotNull(aNode);
        assertEquals('a', aNode.getValue());
        assertFalse(aNode.isLeaf());

        // aNode->'b' should exist and be leaf
        TrieNode bNode = aNode.getChild('b');
        assertNotNull(bNode);
        assertEquals('b', bNode.getValue());
        assertTrue(bNode.isLeaf());
    }

    @Test
    public void testAddChildEmpty() {
        root.addChild("", 0);
        assertTrue(root.isLeaf());
    }

    @Test
    public void testHasPrefix() {
        root.addChild("test", 0);
        // hasPrefix returns the end index when found
        int result = root.hasPrefix("this is a test", 10);
        assertTrue(result != -1);

        int notFound = root.hasPrefix("this is a fail", 10);
        assertEquals(-1, notFound);
    }

    @Test
    public void testHasPrefixWithLeaf() {
        root.addChild("te", 0);
        int result = root.hasPrefix("test", 0);
        // Should find "te" at index 2 (leaf node)
        assertEquals(2, result);
    }

    @Test
    public void testRemoveChild() {
        root.addChild("ab", 0);
        assertTrue(root.removeChild('a'));
        assertNull(root.getChild('a'));
    }

    @Test
    public void testRemoveNonExistentChild() {
        assertFalse(root.removeChild('x'));
    }

    @Test
    public void testGetChild() {
        root.addChild("a", 0);
        TrieNode child = root.getChild('a');
        assertNotNull(child);
        assertEquals('a', child.getValue());
    }

    @Test
    public void testGetChildNonExistent() {
        assertNull(root.getChild('z'));
    }

    @Test
    public void testGetChildren() {
        root.addChild("ab", 0);
        root.addChild("ac", 0);
        Collection<TrieNode> children = root.getChildren();
        // Root should have 'a' as single child (common prefix)
        assertEquals(1, children.size());
    }

    @Test
    public void testLeafStatus() {
        root.addChild("test", 0);
        TrieNode tNode = root.getChild('t');
        TrieNode eNode = tNode.getChild('e');
        TrieNode sNode = eNode.getChild('s');
        TrieNode finalNode = sNode.getChild('t');

        assertFalse(tNode.isLeaf());
        assertFalse(eNode.isLeaf());
        assertFalse(sNode.isLeaf());
        assertTrue(finalNode.isLeaf());

        finalNode.setLeaf(false);
        assertFalse(finalNode.isLeaf());
    }
}
