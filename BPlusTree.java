package assignment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BPlusTree {

  private final int order;
  private Node root;

  public BPlusTree(int order) {
    if (order < 3) {
      throw new IllegalArgumentException("Order must be at least 3");
    }
    this.order = order;
    this.root = new LeafNode();
  }

  public WordDefinition search(String word) {
    LeafNode leaf = findLeafNode(word);
    for (int i = 0; i < leaf.keys.size(); i++) {
      if (leaf.keys.get(i).compareTo(word) == 0) {
        return leaf.values.get(i);
      }
    }
    return null;
  }

  public void insert(String word, WordDefinition definition) {
    LeafNode leaf = findLeafNode(word);
    leaf.insert(word, definition);

    if (leaf.isFull()) {
      splitLeafNode(leaf);
    }
  }

  private LeafNode findLeafNode(String word) {
    Node currentNode = root;
    while (currentNode instanceof InternalNode) {
      InternalNode internalNode = (InternalNode) currentNode;
      currentNode = internalNode.getChild(word);
    }
    return (LeafNode) currentNode;
  }

  private void splitLeafNode(LeafNode leaf) {
    int midIndex = leaf.keys.size() / 2;
    String midKey = leaf.keys.get(midIndex);

    LeafNode newLeaf = new LeafNode();
    newLeaf.keys.addAll(leaf.keys.subList(midIndex, leaf.keys.size()));
    newLeaf.values.addAll(leaf.values.subList(midIndex, leaf.values.size()));

    leaf.keys = new ArrayList<>(leaf.keys.subList(0, midIndex));
    leaf.values = new ArrayList<>(leaf.values.subList(0, midIndex));

    newLeaf.next = leaf.next;
    leaf.next = newLeaf;

    insertIntoParent(leaf, midKey, newLeaf);
  }

  private void splitInternalNode(InternalNode node) {
    int midIndex = node.keys.size() / 2;
    String midKey = node.keys.get(midIndex);

    InternalNode newInternal = new InternalNode();
    newInternal.keys.addAll(node.keys.subList(midIndex + 1, node.keys.size()));
    newInternal.children.addAll(node.children.subList(midIndex + 1, node.children.size()));

    for (Node child : newInternal.children) {
      child.parent = newInternal;
    }

    node.keys = new ArrayList<>(node.keys.subList(0, midIndex));
    node.children = new ArrayList<>(node.children.subList(0, midIndex + 1));

    insertIntoParent(node, midKey, newInternal);
  }

  private void insertIntoParent(Node leftChild, String key, Node rightChild) {
    if (leftChild == root) {
      InternalNode newRoot = new InternalNode();
      newRoot.keys.add(key);
      newRoot.children.add(leftChild);
      newRoot.children.add(rightChild);
      root = newRoot;
      leftChild.parent = newRoot;
      rightChild.parent = newRoot;
      return;
    }

    InternalNode parent = (InternalNode) leftChild.parent;
    int insertIndex = Collections.binarySearch(parent.keys, key);
    if (insertIndex < 0) {
      insertIndex = -insertIndex - 1;
    }
    parent.keys.add(insertIndex, key);
    parent.children.add(insertIndex + 1, rightChild);
    rightChild.parent = parent;

    if (parent.isFull()) {
      splitInternalNode(parent);
    }
  }

  private abstract class Node {
    List<String> keys;
    InternalNode parent;

    Node() {
      this.keys = new ArrayList<>();
    }

    boolean isFull() {
      return keys.size() == order - 1;
    }
  }

  private class InternalNode extends Node {
    List<Node> children;

    InternalNode() {
      super();
      this.children = new ArrayList<>();
    }

    Node getChild(String key) {
      int loc = Collections.binarySearch(keys, key);
      int childIndex = (loc >= 0) ? loc + 1 : -loc - 1;
      return children.get(childIndex);
    }
  }

  private class LeafNode extends Node {
    List<WordDefinition> values;
    LeafNode next;

    LeafNode() {
      super();
      this.values = new ArrayList<>();
    }

    void insert(String key, WordDefinition value) {
      int loc = Collections.binarySearch(keys, key);
      int insertIndex = (loc >= 0) ? loc : -loc - 1;

      if (loc >= 0) {
        System.out.println("Warning: Duplicate word '" + key + "' ignored.");
        return;
      }

      keys.add(insertIndex, key);
      values.add(insertIndex, value);
    }
  }
}