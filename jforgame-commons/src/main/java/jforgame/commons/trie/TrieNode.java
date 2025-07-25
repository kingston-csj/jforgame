package jforgame.commons.trie;


import java.util.Collection;

/**
 * tire树节点
 * 每一个节点代表一个字符，节点下面包含多个子节点
 * @since 2.4.0
 */
public class TrieNode {

    /**
     * 当前节点的字符值
     */
    char val;
    /**
     * 所有孩子子节点，如果每个节点的孩子节点数比较少，这里会造成内存浪费，可以退化为数组
     */
    NodeContainer children = new MapNodeContainer();

    /**
     * 是否是叶子节点，即是否是脏词的最后一个字符
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

    public int hasPrefix(CharSequence cs, int idx) {
        if (idx >= cs.length()) {
            if (val != 0 && isLeaf()) {
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
        } else if (val != 0 && isLeaf()) {
            return idx;
        } else {
            return -1;
        }
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