package ac.uk.soton.ecs.projectalloc;
import ac.uk.soton.ecs.projectalloc.Node;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NodeDeserializer extends StdDeserializer<Node> {
    public NodeDeserializer() {
        super(Node.class);
    }

    @Override
    public Node deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode treeNodeNode = parser.readValueAsTree();
        int value = treeNodeNode.get("value").asInt();
        String interest = treeNodeNode.get("interest").asText();
        List<Node> children = new ArrayList<>();

        if (interest.equals("null")) {
            interest = null;
        }

        if (treeNodeNode.has("children")) {
            ArrayNode childrenNode = (ArrayNode) treeNodeNode.get("children");
            for (JsonNode childNode : childrenNode) {
                children.add(context.readValue(childNode.traverse(parser.getCodec()), Node.class));
            }
        }

        Node node = new Node(value);
        node.setInterest(interest);
        node.addChildren((ArrayList) children);

        return node;
    }
}

