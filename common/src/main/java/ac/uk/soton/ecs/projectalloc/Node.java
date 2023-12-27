package ac.uk.soton.ecs.projectalloc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * An individual node of a tree data structure representing a partipants interests.
 * Node has an id, interest and list of children for:
 * Id is used as a unique identifier for the node
 * Interest stores the name of the interest e.g. machine learning
 * List of children: child interests e.g. machine learning is a child interest of ai
 */
@JsonSerialize(using = NodeSerializer.class)
@JsonDeserialize(using = NodeDeserializer.class)
public class Node implements Comparable<Node> {
    Integer value; // Id for the node
    String interest; // Interest
    List<Node> children; // Children of Node

    public Node(Integer value) {
        this.value = value;
        this.children = new ArrayList<Node>();
    }

    public Node(Integer value, String interest) {
        this.value = value;
        this.interest = interest;
        this.children = new ArrayList<Node>();
    }

    public Node deepClone() {
        Node copy = new Node(this.getValue());
        copy.setInterest(this.getInterest());

        if (this.children != null) {
            // Recursively deep copy each child and add it to the copied children list
            for (Node child : this.children) {
                copy.addChild(child.deepClone());
            }
        }

        return copy;
    }

    /**
     * Setter method
     * @param value id of node
     */
    public void setValue(Integer value) {
        this.value = value;
    }

    /**
     * Getter method
     * @return id of node
     */
    public Integer getValue() {
        return this.value;
    }

    /**
     * Setter method
     * @param interest Area student/supervisor is interested in
     */
    public void setInterest(String interest) {
        this.interest = interest;
    }

    /**
     * Getter method
     * @return interest
     */
    public String getInterest() {
        return this.interest;
    }

    /**
     * Adds node to list of child nodes
     * @param child node being added
     */
    public void addChild(Node child) {
        if(!children.contains(child))
            children.add(child);
    }

    /**
     * Adds nodes to list of child nodes
     * @param children list of nodes being added
     */
    public void addChildren(List<Node> children) {
        for(Node child: children) {
            addChild(child);
        }
    }

    /**
     * Removes node from the immediate children of the node
     * @param child node being removed
     */
    public void removeChild(Node child) {
        children.remove(child);
    }

    /**
     * Removes nodes from the immediate children of the node
     * @param children list of nodes being removed
     */
    public void removeChildren(List<Node> children) {
        this.children.removeAll(children);
    }

    /**
     * Removes all immediate children of the node
     */
    public void clearChildren() {
        children.clear();
    }

    /**
     * Getter method
     * @return list of children
     */
    public List<Node> getChildren() {
        return Collections.unmodifiableList(children);
    }

    /**
     * Checks if child exists
     * @param child node being checked
     * @return if node is a child
     */
    public Boolean hasChild(Node child) {
        return children.contains(child);
    }

    public Integer getNumChildren() {
        return children.size();
    }

    public List<Node> getAllNodes() {
        List<Node> nodes = new ArrayList<>();
        walkNodes(this, nodes);
        return nodes;
    }

    public List<String> getAllInterests() {
        List<Node> nodes = new ArrayList<>();
        walkNodes(this, nodes);
        return nodes.stream().map(Node::getInterest).collect(Collectors.toList());
    }

    private void walkNodes (Node currentNode, List<Node> nodes) {
        nodes.add(currentNode);
        currentNode.getChildren().forEach(node -> walkNodes(node, nodes));
    }

    @Override
    public int compareTo(Node o) {
        return Integer.compare(this.value,o.getValue());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node node = (Node) o;

        if (!Objects.equals(value, node.value)) return false;
        if (!Objects.equals(interest, node.interest)) return false;
        return Objects.equals(children, node.children);
    }

    @Override
    public int hashCode() {
        int result = value != null ? value.hashCode() : 0;
        result = 31 * result + (interest != null ? interest.hashCode() : 0);
        result = 31 * result + (children != null ? children.hashCode() : 0);
        return result;
    }
}
