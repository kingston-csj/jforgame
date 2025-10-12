package jforgame.commons.trie;


import java.util.ArrayList;
import java.util.List;

/**
 * Trie树，又称字典树或前缀树，是一种在算法中常用的数据结构。它主要用于解决通过前缀来联想完整单词的问题
 * 可用于脏词检测，好友模糊查询等场景
 * @since 2.4.0
 */
public class TrieDictionary {

    /**
     * 阈值，当节点的孩子节点数量小于等于阈值时，将map容器转化为数组
     */
    private static final int THRESHOLD = 3;
    /**
     * 前缀根节点
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
     * 删除单词节点
     * @param word 要删除的单词
     * @return 是否成功删除
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
     * 递归删除单词节点
     * @param node 当前节点
     * @param word 要删除的单词
     * @param index 当前处理的字符索引
     * @return 是否成功删除
     */
    private boolean deleteNodeRecursive(TrieNode node, String word, int index) {
        // 如果已经处理完所有字符
        if (index >= word.length()) {
            // 如果当前节点是叶子节点，则删除叶子标记
            if (node.isLeaf()) {
                node.setLeaf(false);
                return true;
            }
            return false;
        }

        char currentChar = word.charAt(index);
        TrieNode childNode = node.getChild(currentChar);
        
        if (childNode == null) {
            // 单词不存在
            return false;
        }

        // 递归删除下一个字符
        boolean deleted = deleteNodeRecursive(childNode, word, index + 1);
        
        if (deleted) {
            // 如果子节点被删除且当前子节点没有其他子节点且不是叶子节点，则删除当前子节点
            if (!childNode.isLeaf() && childNode.getChildren().isEmpty()) {
                node.removeChild(currentChar);
            }
        }
        
        return deleted;
    }

    /**
     * 指定字符串是否包含敏感字
     * @param word 要检查的字符串
     * @return 是否包含敏感字
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
     * 检查字典是否精确匹配某个单词
     * 例如张无是敏感字，但是希望张无忌不是
     * @param word 要检查的单词
     * @return 是否精确匹配
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
     * 将敏感字替换成字符'*',如果有的话
     * @param content 要处理的字符串
     * @return 转换后的字符串
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
     * 字符串预处理，英文统一转小写（去掉特殊符合，只保留字母、数字、中文）
     *
     * @return 转换后的字符串
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
     * 整颗树构建成功后，对孩子节点运行重新构造
     * 如果某节点的孩子节点数量少于阈值，则将map容器转化为数组
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
        // 递归处理子节点
        node.children.getAll().forEach(this::rebuildChildren);
    }

}