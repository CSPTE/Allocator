package ac.uk.soton.ecs.projectalloc;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;

public class ChosenSupervisorDataDeserializer extends StdDeserializer<ChosenSupervisorData> {

    public ChosenSupervisorDataDeserializer() {
        super(ChosenSupervisorData.class);
    }

    @Override
    public ChosenSupervisorData deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        JsonNode node = jsonParser.readValueAsTree();

        Supervisor student = new Supervisor(node.get("email").asText());

        ChosenSupervisorData chosenSupervisorData = new ChosenSupervisorData(student, node.get("max_allocations").asInt());
        chosenSupervisorData.setSpecialism(node.get("specialism").asText());
        chosenSupervisorData.setInterestTree(node.get("interest_tree").traverse(jsonParser.getCodec()).readValueAs(Node.class));

        return chosenSupervisorData;
    }

}
