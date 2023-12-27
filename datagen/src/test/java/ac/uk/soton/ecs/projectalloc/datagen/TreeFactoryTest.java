package ac.uk.soton.ecs.projectalloc.datagen;

import ac.uk.soton.ecs.projectalloc.Node;
import com.sun.source.tree.Tree;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

public class TreeFactoryTest extends TestCase {

    private final PrintStream standardOut = System.out;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    public void testJSONConversionTreeBigTree() {
        Node root = new Node(0);
        Node child11 = new Node(1);
        Node child12 = new Node(2);
        Node child21 = new Node(3);
        Node child22 = new Node(4);
        Node child23 = new Node(5);
        Node child24 = new Node(6);

        root.addChild(child11);
        root.addChild(child12);
        child11.addChild(child21);
        child11.addChild(child22);
        child12.addChild(child23);
        child12.addChild(child24);

        TreeFactory tf = new TreeFactory();
        Node importedTree = tf.importTree("src/test/resources/","test");
        String expectedJSON = tf.treeToJSONString(importedTree);
        String actualJSON = tf.treeToJSONString(root);

        assertEquals(expectedJSON, actualJSON);
    }

    public void testJSONConversionTreeIndividualNode() {
        Node root = new Node(0);
        TreeFactory tf = new TreeFactory();
        Node importedTree = tf.importTree("src/test/resources/","test2");
        String expectedJSON = tf.treeToJSONString(importedTree);
        String actualJSON = tf.treeToJSONString(root);

        assertEquals(expectedJSON, actualJSON);
    }


    public void testImportTreeNoFileExceptionThrown() {
        TreeFactory tf = new TreeFactory();
        tf.importTree(null, null);
        Assert.assertEquals("File not found", outputStreamCaptor.toString()
                .trim());
    }

    public void testCheckForCyclicTree1Nodes() {
        TreeFactory tf = new TreeFactory();
        Node node1 = new Node(0);
        node1.addChild(node1);

        Assert.assertTrue(tf.isTreeCyclic(node1));
    }

    public void testCheckForCyclicTree2Nodes() {
        TreeFactory tf = new TreeFactory();
        Node node1 = new Node(0);
        Node node2 = new Node(1);
        node1.addChild(node2);
        node2.addChild(node1);

        Assert.assertTrue(tf.isTreeCyclic(node1));
    }

    public void testCheckForCyclicTree3Nodes() {
        TreeFactory tf = new TreeFactory();
        Node node1 = new Node(0);
        Node node2 = new Node(1);
        Node node3 = new Node(2);
        node1.addChild(node2);
        node2.addChild(node3);
        node3.addChild(node1);

        Assert.assertTrue(tf.isTreeCyclic(node1));
    }

    public void testCheckForCyclicTree4Nodes() {
        TreeFactory tf = new TreeFactory();
        Node node1 = new Node(0);
        Node node2 = new Node(1);
        Node node3 = new Node(2);
        Node node4 = new Node(3);
        node1.addChild(node2);
        node2.addChild(node3);
        node3.addChild(node4);
        node4.addChild(node1);

        Assert.assertTrue(tf.isTreeCyclic(node1));
    }

    public void testCheckForCyclicTreeNotCyclic() {
        TreeFactory tf = new TreeFactory();
        Node root = new Node(0);
        Node child11 = new Node(1);
        Node child12 = new Node(2);
        Node child21 = new Node(3);
        Node child22 = new Node(4);
        Node child23 = new Node(5);
        Node child24 = new Node(6);

        root.addChild(child11);
        root.addChild(child12);
        child11.addChild(child21);
        child11.addChild(child22);
        child12.addChild(child23);
        child12.addChild(child24);

        Assert.assertFalse(tf.isTreeCyclic(root));
    }

    public void testSubTreeCyclicSuperTree() throws IllegalArgumentException {
        TreeFactory tf = new TreeFactory();
        Node node1 = new Node(0);
        Node node2 = new Node(1);
        Node node3 = new Node(2);
        Node node4 = new Node(3);
        node1.addChild(node2);
        node2.addChild(node3);
        node3.addChild(node4);
        node3.addChild(node1);

        Assert.assertThrows(IllegalArgumentException.class, () -> {
            tf.getSubTree(node1, 2);
        });
    }

    public void testTreeGenMaxNodesIs0() {
        TreeFactory tf = new TreeFactory();
        Assert.assertNull(tf.generateTree(0,0,0));
    }

    public void testTreeGenMaxNodesIs1() {
        TreeFactory tf = new TreeFactory();
        Node root = tf.generateTree(1,1,1);
        Assert.assertSame(1,tf.getNumNodes(root));
    }

    public void testTreeGenMaxNodesIs10Nodes() {
        TreeFactory tf = new TreeFactory();
        Node root = tf.generateTree(10,1,1);
        Assert.assertSame(10,tf.getNumNodes(root));
    }

    public void testTreeGenNumChildrenInsideRange1Depth() {
       TreeFactory tf = new TreeFactory();
       Node root = tf.generateTree(4,3,4);

       Assert.assertSame(3,root.getChildren().size());
    }

    public void testTreeGenMinChildrenGreaterThanMaxChildren() {
        TreeFactory tf = new TreeFactory();

        Assert.assertThrows(IllegalArgumentException.class, () -> {
            tf.generateTree(5,4,2);
        });
    }

    public void testTreeGenNegativeMaxNodes() {
        TreeFactory tf = new TreeFactory();

        Assert.assertThrows(IllegalArgumentException.class, () -> {
            tf.generateTree(-1,2,5);
        });
    }

    public void testTreeGenNegativeMaxChildren() {
        TreeFactory tf = new TreeFactory();

        Assert.assertThrows(IllegalArgumentException.class, () -> {
            tf.generateTree(5,2,-5);
        });
    }

    public void testTreeGenNegativeMinChildren() {
        TreeFactory tf = new TreeFactory();

        Assert.assertThrows(IllegalArgumentException.class, () -> {
            tf.generateTree(5,-6,5);
        });
    }

    public void testTreeGenMinChildrenGreaterThanMaxNodes() {
        TreeFactory tf = new TreeFactory();

        Assert.assertThrows(IllegalArgumentException.class, () -> {
            tf.generateTree(5,6,5);
        });
    }

    public void testTreeGenMaxChildrenGreaterThanMaxNodes() {
        TreeFactory tf = new TreeFactory();

        Assert.assertThrows(IllegalArgumentException.class, () -> {
            tf.generateTree(5,2,6);
        });
    }

    public void testGetDepthRootNode() {
        Node node = new Node(0);
        TreeFactory tf = new TreeFactory();
        assertEquals(0,(int) tf.getDepth(node));
    }

    public void testGetDepth1() {
        Node root = new Node(0);
        Node child = new Node(1);
        TreeFactory tf = new TreeFactory();
        root.addChild(child);

        assertEquals(1, (int) tf.getDepth(root));
    }
    public void testGetDepthN() {
        Node root = new Node(0);
        int depth = (int) (Math.random() * 10);
        Node current = root;
        TreeFactory tf = new TreeFactory();

        for(int i = 0; i < depth; i++) {
            Node child = new Node(i+1);
            current.addChild(child);
            current = child;
        }

        assertEquals(depth, (int) tf.getDepth(root));
    }

    public void testGetSubTree() {
        Node root = new Node(0);
        Node child1 = new Node(1);
        Node child2 = new Node(2);
        Node child3 = new Node(3);
        Node child4 = new Node(4);
        Node child5 = new Node(5);
        Node child6 = new Node(6);
        TreeFactory tf = new TreeFactory();
        root.addChildren(List.of(child1,child2,child3,child4,child5,child6));
        Assert.assertEquals(4,tf.getSubTree(root,4).getAllInterests().size());
    }

    public void testSkinTreeRootNodeOnly() {
        TreeFactory tf = new TreeFactory();
        Node root = new Node(0);
        Node root2 = root.deepClone();
        Node child1 = new Node(1);
        Node child2 = new Node(2);
        Node child3 = new Node(3);
        root.addChildren(List.of(child1,child2,child3));
        Node prunedTree = tf.skinTree(root,List.of(root));

        Assert.assertTrue(prunedTree.equals(root2));
    }

    public void testSkinTreePartI() {
        TreeFactory tf = new TreeFactory();
        Node root = new Node(0);
        Node child1 = new Node(1);
        Node child2 = new Node(2);
        Node child3 = new Node(3);
        root.addChildren(List.of(child1,child2));

        Node expectedPrunedTree = root.deepClone();

        root.addChild(child3);
        Node prunedTree = tf.skinTree(root,List.of(root,child1,child2));

        Assert.assertTrue(prunedTree.equals(expectedPrunedTree));
    }

    public void testSkinTreePartII() {
        TreeFactory tf = new TreeFactory();
        Node root = new Node(0);
        Node child1 = new Node(1);
        Node child2 = new Node(2);
        Node child3 = new Node(3);
        Node child4 = new Node(4);
        Node child5 = new Node(5);
        Node child6 = new Node(6);
        Node child7 = new Node(7);
        Node child8 = new Node(8);

        root.addChildren(List.of(child1,child2));
        child1.addChild(child4);
        child4.addChild(child5);

        Node expectedPrunedTree = root.deepClone();

        root.addChild(child3);
        child5.addChildren(List.of(child6,child7,child8));

        Node prunedTree = tf.skinTree(root,List.of(root,child1,child2,child4,child5));

        Assert.assertTrue(prunedTree.equals(expectedPrunedTree));
    }

    public void testSkinTreeRootNodeExcluded() {
        TreeFactory tf = new TreeFactory();
        Node root = new Node(0);
        Node child = new Node(1);
        root.addChild(child);

        Assert.assertThrows(IllegalArgumentException.class, () -> {
            tf.skinTree(root, List.of(child));
        });
    }
}
