package jforgame.commons.trie;


import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 节点容器（包含所有孩子节点）
 * @since 2.4.0
 */
interface NodeContainer {

    /**
     * 添加子节点
     *
     * @param node
     */
    void add(TrieNode node);

    TrieNode get(Character character);

    int size();

    Collection<TrieNode> getAll();

    /**
     * 转换为另一种容器类型
     *
     * @return
     */
    NodeContainer transform();
}

/**
 * 基于Map的容器
 */
class MapNodeContainer implements NodeContainer {

    private Map<Character, TrieNode> container = new HashMap<>();

    @Override
    public void add(TrieNode node) {
        container.put(node.val, node);
    }

    public ListNodeContainer transform() {
        ListNodeContainer listNodeContainer = new ListNodeContainer();
        container.values().forEach(listNodeContainer::add);
        return listNodeContainer;
    }

    @Override
    public TrieNode get(Character character) {
        return container.get(character);
    }

    @Override
    public int size() {
        return container.size();
    }

    @Override
    public Collection<TrieNode> getAll() {
        return container.values();
    }

}

/**
 * 基于List的容器
 */
class ListNodeContainer implements NodeContainer {

    private List<TrieNode> container = new LinkedList<>();

    @Override
    public void add(TrieNode node) {
        container.add(node);
    }

    public MapNodeContainer transform() {
        MapNodeContainer mapNodeContainer = new MapNodeContainer();
        container.forEach(mapNodeContainer::add);
        return mapNodeContainer;
    }

    @Override
    public TrieNode get(Character character) {
        for (TrieNode node : container) {
            if (character == node.val) {
                return node;
            }
        }
        return null;
    }

    @Override
    public int size() {
        return container.size();
    }

    @Override
    public Collection<TrieNode> getAll() {
        return container;
    }
}
