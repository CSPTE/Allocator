package ac.uk.soton.ecs.projectalloc;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.util.List;

public class NodeSerializer extends StdSerializer<Node> {

    NodeSerializer() {
        this(null);
    }

    protected NodeSerializer(Class<Node> t) {
        super(t);
    }

    @Override
    public void serialize(Node node, JsonGenerator jsonGen, SerializerProvider serializerProvider) throws IOException {
        String interest = node.getInterest();
        List<Node> children = node.getChildren();
        Integer value = node.getValue();

        jsonGen.writeStartObject();
        jsonGen.writeNumberField("value",value);
        jsonGen.writeStringField("interest",interest);

        jsonGen.writeFieldName("children");
        jsonGen.writeStartArray();
        for(Node child: children) {
            jsonGen.writeObject(child);
        }

        jsonGen.writeEndArray();
        jsonGen.writeEndObject();
    }
}
