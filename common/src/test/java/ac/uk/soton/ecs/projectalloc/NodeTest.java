package ac.uk.soton.ecs.projectalloc;

import ac.uk.soton.ecs.projectalloc.Node;
import com.sun.source.util.DocTreeFactory;
import junit.framework.TestCase;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.List;

public class NodeTest extends TestCase {
    public void testSetValue() {
        Node node = new Node(0);
        node.setValue(1);
        assertEquals((int) node.value,1);
    }

    public void testDeepCloneOneNodeDifferentRegister() {
        Node node = new Node(1);
        Node clonedNode = node.deepClone();
        Assert.assertTrue(node != clonedNode);
    }

    public void testDeepCloneOneNodeSameObject() {
        Node node = new Node(1);
        Node clonedNode = node.deepClone();
        Assert.assertTrue(node.equals(clonedNode));
    }

    public void testDeepCloneDifferentRegister() {
        Node node = new Node(1);
        Node child1 = new Node(2);
        Node child2 = new Node(3);
        Node child3 = new Node(4);
        node.addChildren(List.of(child1,child2));
        child2.addChild(child3);

        Assert.assertTrue(node != node.deepClone());
    }

    public void testDeepCloneSameObject() {
        Node node = new Node(1);
        Node child1 = new Node(2);
        Node child2 = new Node(3);
        Node child3 = new Node(4);
        node.addChildren(List.of(child1,child2));
        child2.addChild(child3);

        Assert.assertTrue(node.equals(node.deepClone()));
    }

    public void testGetValue() {
        Node node = new Node(0);
        assertEquals((int) node.getValue(),0);
    }

    public void testSetInterest() {
        Node node = new Node(0);
        node.setInterest("ML");
        assertEquals(node.interest, "ML");
    }

    public void testGetInterest() {
        Node node = new Node(0);
        node.setInterest("ML");
        assertEquals(node.getInterest(), "ML");
    }

    public void testAddChild() {
        Node node = new Node(0);
        Node child1 = new Node(1);
        node.addChild(child1);
        assertEquals(node.getChildren().get(0),child1);
    }

    public void testRemoveChild() {
        Node node = new Node(0);
        Node child1 = new Node(1);
        Node child2 = new Node(2);
        Node child3 = new Node(3);
        ArrayList<Node> children = new ArrayList<>();
        children.add(child1);
        children.add(child2);
        children.add(child3);
        node.addChildren(children);
        node.removeChild(child3);
        children.remove(child3);
        assertEquals(node.getChildren(),children);
    }

    public void testAddChildren() {
        Node node = new Node(0);
        Node child1 = new Node(1);
        Node child2 = new Node(2);
        Node child3 = new Node(3);
        List<Node> children = List.of(child1, child2, child3);
        node.addChildren(children);
        assertEquals(node.getChildren(),children);
    }

    public void testRemoveChildren() {
        Node node = new Node(0);
        Node child1 = new Node(1);
        Node child2 = new Node(2);
        Node child3 = new Node(3);
        List<Node> children = List.of(child1, child2,child3);
        node.addChildren(children);
        node.removeChild(child3);
        assertEquals(node.getChildren(),List.of(child1, child2));
    }

    public void testClearChildren() {
        Node node = new Node(0);
        Node child1 = new Node(1);
        Node child2 = new Node(2);
        Node child3 = new Node(3);
        List<Node> children = List.of(child1, child2, child3);
        node.clearChildren();
        assertEquals(node.getChildren().isEmpty(), true);
    }

    public void testHasChild() {
        Node node = new Node(0);
        Node child1 = new Node(1);
        node.addChild(child1);
        assertEquals(node.hasChild(child1).booleanValue(),true);
    }

    public void testAddDuplicateChild() {
        Node node = new Node(0);
        Node child1 = new Node(1);
        node.addChild(child1);
        node.addChild(child1);
        assertEquals(1,(int) node.getChildren().size());
    }

    public void testRemoveChildNotPresent() {
        Node node = new Node(0);
        Node child1 = new Node(1);
        Node child2 = new Node(2);
        node.addChild(child1);
        node.removeChild(child2);
    }

    public void testHasChildNotPresent() {
        Node node = new Node(0);
        Node child1 = new Node(1);
        Node child2 = new Node(2);
        node.addChild(child1);
        node.hasChild(child2);
    }

    public void testGetInterestsSize() {
        Node node = new Node(0);
        Node child1 = new Node(1);
        Node child2 = new Node(2);
        node.addChild(child1);
        node.addChild(child2);

        Assert.assertEquals(3,node.getAllInterests().size());
    }

    public void testGetInterests() {
        Node node = new Node(0);
        Node child1 = new Node(1);
        Node child2 = new Node(2);

        node.setInterest("a");
        child1.setInterest("a");
        child2.setInterest("a");
        node.addChild(child1);
        node.addChild(child2);

        Assert.assertEquals(List.of("a","a","a"),node.getAllInterests());
    }

    public void testGetNodesSize() {
        Node node = new Node(0);
        Node child1 = new Node(1);
        Node child2 = new Node(2);
        node.addChild(child1);
        node.addChild(child2);

        Assert.assertEquals(3,node.getAllNodes().size());
    }

    public void testGetNodes() {
        Node node = new Node(0);
        Node child1 = new Node(1);
        Node child2 = new Node(2);
        node.addChild(child1);
        node.addChild(child2);

        Assert.assertEquals(List.of(node,child1,child2),node.getAllNodes());
    }

    public void testGetNodesRootOnly() {
        Node node = new Node(0);
        Assert.assertEquals(List.of(node),node.getAllNodes());
    }
}
