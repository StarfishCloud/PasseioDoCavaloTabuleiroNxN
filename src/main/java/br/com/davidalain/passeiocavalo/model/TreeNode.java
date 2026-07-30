package br.com.davidalain.passeiocavalo.model;

import java.io.Serializable;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TreeNode<T extends Comparable<T>> implements Comparator<TreeNode<T>> {

    public final T value;
    public final int depth;
    public final TreeNode<T> parentNode;
    public final List<TreeNode<T>> childNodes;

    public TreeNode(T value, int depth, TreeNode<T> parentNode) {
        this.value = value;
        this.depth = depth;
        this.parentNode = parentNode;
        this.childNodes = new LinkedList<>();
    }

    public void addChild(TreeNode<T> childNode) {
        this.childNodes.add(childNode);
    }

    public TreeNode<T> addChild(T childNodeValue) {
        final TreeNode<T> childNode = new TreeNode<>(childNodeValue, depth + 1, this);
        this.childNodes.add(childNode);
        return childNode;
    }

    public boolean deleteChild(T childNodeValue) {
        return this.childNodes.removeIf(child -> child.value.equals(childNodeValue));
    }

    public void deleteChild(TreeNode<T> childNode) {
        this.childNodes.remove(childNode);
    }

    public T getValue() {
        return value;
    }

    public List<TreeNode<T>> getChildNodes() {
        return childNodes;
    }

    @Override
    public int compare(TreeNode<T> o1, TreeNode<T> o2) {
        return o1.value.compareTo(o2.value);
    }

    public static <K extends Comparable<K>> Comparator<TreeNode<K>> comparingByValue(){
        return (Comparator<TreeNode<K>> & Serializable)
                (t1, t2) -> t1.value.compareTo(t2.value);
    }

    @Override
    public String toString() {
        final String spacing = IntStream.range(0, depth).mapToObj(i -> "  ").collect(Collectors.joining());
        return "\r\n" + spacing + "TreeNode{" +
                "value=" + value +
                ", depth=" + depth +
                ", childNodes=" +
                (childNodes.isEmpty() ?
                        "[EMPTY]" :
                        childNodes.stream().map(Object::toString).collect(Collectors.joining(",")) + spacing) +
                "}\r\n";
    }
}

