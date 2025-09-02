package jforgame.commons.trie;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * TrieDictionary删除功能测试
 */
public class TestTrieDictionary {

    @Test
    public void testSimpleDelete() {
        TrieDictionary trie = new TrieDictionary();
        
        // 只添加一个单词
        trie.addNode("张无忌");
        
        // 验证单词存在
        assertTrue(trie.containsWords("张无忌"));
        
        // 删除单词
        boolean deleted = trie.deleteNode("张无忌");
        System.out.println("删除结果: " + deleted);
        
        // 验证单词不存在
        boolean contains = trie.containsWords("张无忌");
        System.out.println("删除后是否包含: " + contains);
        assertFalse(contains);
    }

    @Test
    public void testDeleteNode() {
        TrieDictionary trie = new TrieDictionary();
        
        // 添加一些测试单词
        trie.addNode("张无忌");
        trie.addNode("张无敌");
        trie.addNode("张有忌");
        trie.addNode("张天忌");
        
        // 验证单词存在
        assertTrue(trie.containsWords("张无忌"));
        assertTrue(trie.containsWords("张无敌"));
        assertTrue(trie.containsWords("张有忌"));
        assertTrue(trie.containsWords("张天忌"));

        // 删除单词
        assertTrue(trie.deleteNode("张无忌"));
        assertFalse(trie.containsWords("张无忌"));
        
        // 删除不存在的单词
        assertFalse(trie.deleteNode("nonexistent"));
        
        // 删除部分单词，验证其他单词仍然存在
        assertTrue(trie.deleteNode("张有忌"));
        assertFalse(trie.containsWords("张有忌"));
        assertTrue(trie.containsWords("张天忌"));
        
        // 删除所有单词
        assertTrue(trie.deleteNode("张无敌"));
        assertTrue(trie.deleteNode("张天忌"));

        assertFalse(trie.containsWords("张无敌"));
        assertFalse(trie.containsWords("张天忌"));
    }

    @Test
    public void testDeleteNodeWithEmptyString() {
        TrieDictionary trie = new TrieDictionary();
        trie.addNode("test");
        
        // 删除空字符串应该返回false
        assertFalse(trie.deleteNode(""));
        assertFalse(trie.deleteNode(null));
        
        // 原单词应该仍然存在
        assertTrue(trie.containsWords("test"));
    }

    @Test
    public void testDeleteNodeCaseInsensitive() {
        TrieDictionary trie = new TrieDictionary();
        trie.addNode("Hello");
        
        // 删除时应该忽略大小写
        assertTrue(trie.deleteNode("hello"));
        assertFalse(trie.containsWords("Hello"));
    }

    @Test
    public void testMultipleWordsDelete() {
        TrieDictionary trie = new TrieDictionary();
        
        // 添加测试单词
        trie.addNode("hello");
        trie.addNode("hell");
        
        System.out.println("添加后:");
        System.out.println("containsExactWord hello: " + trie.containsExactWord("hello"));
        System.out.println("containsExactWord hell: " + trie.containsExactWord("hell"));
        
        // 删除 hello
        boolean deleted = trie.deleteNode("hello");
        System.out.println("删除hello结果: " + deleted);
        
        System.out.println("删除hello后:");
        System.out.println("containsExactWord hello: " + trie.containsExactWord("hello"));
        System.out.println("containsExactWord hell: " + trie.containsExactWord("hell"));
        
        // 验证结果
        assertFalse(trie.containsExactWord("hello"));
        assertTrue(trie.containsExactWord("hell"));
    }

    @Test
    public void testHasPrefixBehavior() {
        TrieDictionary trie = new TrieDictionary();
        
        // 添加 "hell" 和 "hello"
        trie.addNode("hell");
        trie.addNode("hello");
        
        // 检查 "hello" 是否包含敏感词
        boolean containsHello = trie.containsWords("hello");
        System.out.println("contains hello: " + containsHello);
        
        // 删除 "hello"
        trie.deleteNode("hello");
        
        // 再次检查 "hello" 是否包含敏感词
        boolean containsHelloAfterDelete = trie.containsWords("hello");
        System.out.println("contains hello after delete: " + containsHelloAfterDelete);
        
        // 检查 "hell" 是否仍然存在
        boolean containsHell = trie.containsWords("hell");
        System.out.println("contains hell: " + containsHell);
    }

    @Test
    public void testContainsWordsBehavior() {
        TrieDictionary trie = new TrieDictionary();
        
        // 只添加 "hell"
        trie.addNode("hell");
        
        // 检查 "hello" 是否包含敏感词
        boolean containsHello = trie.containsWords("hello");
        System.out.println("contains hello (only hell exists): " + containsHello);
        
        // 添加 "hello"
        trie.addNode("hello");
        
        // 再次检查 "hello" 是否包含敏感词
        boolean containsHelloAfterAdd = trie.containsWords("hello");
        System.out.println("contains hello (both hell and hello exist): " + containsHelloAfterAdd);
        
        // 删除 "hello"
        trie.deleteNode("hello");
        
        // 再次检查 "hello" 是否包含敏感词
        boolean containsHelloAfterDelete = trie.containsWords("hello");
        System.out.println("contains hello (only hell exists again): " + containsHelloAfterDelete);
    }
}
