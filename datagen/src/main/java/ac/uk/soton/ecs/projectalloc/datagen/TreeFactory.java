package ac.uk.soton.ecs.projectalloc.datagen;

import ac.uk.soton.ecs.projectalloc.Node;
import ac.uk.soton.ecs.projectalloc.NodeDeserializer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Generates trees
 */
public class TreeFactory {
    /**
     * Prints tree to console
     * @param root of tree being printed
     */
    public void prettyPrintTree(Node root) {
        prettyPrintNode(root);

        if (root.getChildren().isEmpty()) {
            return;
        }

        for(Node child: root.getChildren()) {
            prettyPrintTree(child);
        }
    }

    public Integer getNumNodes(Node node) {
        return node.getAllNodes().size();
    }

    public Integer getDepth(Node node) {
        Integer maxDepth = 0;

        if(node.getChildren().isEmpty()) {
            return 0;
        }

        for(Node child: node.getChildren()) {
            maxDepth = Math.max(maxDepth,getDepth(child));
        }

        return 1 + maxDepth;
    }

    public Node getSubTree(Node superTree, Integer numNodes) {
        ArrayList<Node> selectedNodes = new ArrayList<>();
        ArrayList<Node> children = new ArrayList<>();
        Node newNode;
        Integer newNodeIndex;

        if(superTree.getChildren().isEmpty()) {
            return superTree;
        }

        if(isTreeCyclic(superTree)) {
            throw new IllegalArgumentException("Super Tree is cyclic and therefore not a tree");
        }

        if(numNodes > superTree.getAllInterests().size()) {
            throw new IllegalArgumentException("Number of nodes to select is greater than number of nodes in super tree");
        }

        children.addAll(superTree.getChildren()); //Add root's children to children list
        selectedNodes.add(superTree); //Add root to selected

        for(int i = 1; i < numNodes; i++) {
            newNodeIndex = (int) (Math.random() * (children.size() - 1));
            newNode = children.get(newNodeIndex); //Select a node

            if(selectedNodes.size() < numNodes) {
                selectedNodes.add(newNode);
                children.addAll(newNode.getChildren()); //Add children of selected node
                children.remove(newNode); //Remove selected node from children
            }
        }

        //Dfs removes any nodes not in selected
        return skinTree(superTree, selectedNodes);
    }

    public Node skinTree(Node node, List<Node> selectedNodes) {
        if(!selectedNodes.contains(node)) {
            throw new IllegalArgumentException("Root node is not in selected nodes");
        }

        Node skinnedTree = skinTreeInner(node.deepClone(),selectedNodes);

        return skinnedTree;
    }


    public Node skinTreeInner(Node node, List<Node> selectedNodes) {
        if(node.getNumChildren() == 0) {
            return node;
        }

        Queue<Node> queue = new PriorityQueue<>(node.getNumChildren());
        Integer numNodesProcessed = 0;
        ArrayList<Node> childrenToAdd = new ArrayList<>();
        Node child;
        Node newChild;
        boolean contains;

        queue.addAll(node.getChildren());

        while(queue.peek() != null) {
            child = queue.poll();


            if(!selectedNodes.contains(child)) {
                node.removeChild(child);
            } else {
                newChild = skinTreeInner(child,selectedNodes);
                childrenToAdd.add(newChild);
            }
            numNodesProcessed++;
        }

        node.clearChildren();
        node.addChildren(childrenToAdd);


        return node;
    }


    /**
     * Exports Tree as Node Object to json file
     * @param root root node of the tree
     * @param filePath the directory the file is saved in
     * @param fileName name of the file
     */
    public void exportTree(Node root, String filePath,String fileName) {
        String dir = filePath + File.separator + fileName + ".json";
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.writeValue(new File(dir),root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String treeToJSONString(Node root) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(root);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Imports tree json file as Node object
     * @param filePath directory json f ile is saved in
     * @param fileName name of json file
     * @return the root of the tree
     */
    public Node importTree(String filePath,String fileName) {
        String dir = filePath + File.separator + fileName + ".json";
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Node.class, new NodeDeserializer()); //Node deserializer reads tree json file
        objectMapper.registerModule(module);

        try {
            return objectMapper.readValue(new File(dir),Node.class);
        } catch (IOException e) {
            System.out.println("File not found");
            return null;
        }
    }

    public boolean isTreeCyclic(Node root) {
        try {
            root.getAllNodes();
        } catch (StackOverflowError e) {
            return true;
        }

        return false;
    }

    /**
     * Pretty prints individual node and children
     * @param node being printed
     */
    public void prettyPrintNode(Node node) {
        System.out.println("Current Node: " + node.getValue());
        System.out.print("Nodes children: ");
        node.getChildren().forEach(x -> System.out.print(x.getValue() + " "));
        System.out.println("\n------------------------------------------------");
    }

    /**
     * Generates a tree using bfs
     * @param maxNodes number of nodes in tree
     * @param minChildren min number of children for a node
     * @param maxChildren max number of children for a node
     * @return tree generated
     */
    public Node generateTree(Integer maxNodes, Integer minChildren, Integer maxChildren) {
        Node root = new Node(0);
        Integer numNodes = 1;
        Queue<Node> queue = new PriorityQueue<>(numNodes); //Queue of nodes yet to have children
        Node currentNode;
        Integer numChildren;
        Node newNode;

        root.setInterest("ROOT");

        queue.add(root);

        if(maxNodes == 0) {
            return null;
        } else if (maxNodes < 0) {
            throw new IllegalArgumentException("Must be a positive number of nodes");
        } else if (minChildren < 0) {
            throw new IllegalArgumentException("Must be a positive number of minChildren");
        } else if (maxChildren < 0) {
            throw new IllegalArgumentException("Must be a positive number of maxChildren");
        } else if (minChildren > maxNodes) {
            throw new IllegalArgumentException("MinChildren must be less than maxNodes");
        } else if (maxChildren > maxNodes) {
            throw new IllegalArgumentException("MaxChildren must be less than maxNodes");
        } else if (maxNodes==1 | maxChildren == 0) {
            return root;
        } else if (maxChildren < minChildren) {
            throw new IllegalArgumentException("Max children must be greater than or equal to min children");
        }


        while(maxNodes > numNodes) {
            numChildren = (int) (Math.random() * (maxChildren - minChildren)) + minChildren;

            if(numChildren > maxChildren | numChildren < minChildren) {
                numChildren = minChildren;
            }

            //Ensures exactly tree has maxNodes nodes
            if(numNodes + numChildren > maxNodes) {
                numChildren = maxNodes - numNodes;
            }

            currentNode = queue.poll(); //Node yet to have children (minimum depth out of all other nodes without children)

            for(int i = 0; i < numChildren; i++) { //Generate children
                newNode = new Node(numNodes);
                newNode.setInterest("Node" + numNodes);
                currentNode.addChild(newNode);
                queue.add(newNode);
                numNodes++;
            }
        }

        if(isTreeCyclic(root)) {
            return generateTree(maxNodes,minChildren,maxChildren);
        }

        return root;
    }
}
