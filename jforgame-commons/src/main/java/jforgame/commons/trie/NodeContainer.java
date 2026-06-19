package jforgame.commons.trie;


import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Node container (contains all child nodes)
 * @since 2.4.0
 */
interface NodeContainer {

    /**
     * Adds a child node
     *
     * @param node the child node to add
     */
    void add(TrieNode node);

    /**
     * Removes a child node
     *
     * @param character the character to remove
     * @return the removed node, returns null if it doesn't exist
     * @since 2.5.0
     */
    TrieNode remove(Character character);

    TrieNode get(Character character);

    int size();

    Collection<TrieNode> getAll();

    /**
     * Converts to another container type
     *
     * @return the converted container
     */
    NodeContainer transform();
}

/**
 * Map-based container
 */
class MapNodeContainer implements NodeContainer {

    private Map<Character, TrieNode> container = new HashMap<>();

    @Override
    public void add(TrieNode node) {
        container.put(node.val, node);
    }

    @Override
    public TrieNode remove(Character character) {
        return container.remove(character);
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
 * List-based container
 */
class ListNodeContainer implements NodeContainer {

    private List<TrieNode> container = new LinkedList<>();

    @Override
    public void add(TrieNode node) {
        container.add(node);
    }

    @Override
    public TrieNode remove(Character character) {
        for (int i = 0; i < container.size(); i++) {
            TrieNode node = container.get(i);
            if (character == node.val) {
                return container.remove(i);
            }
        }
        return null;
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
