package ac.uk.soton.ecs.projectalloc;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChosenStudentDataDeserializer extends StdDeserializer<ChosenStudentData> {

    public ChosenStudentDataDeserializer() {
        super(ChosenStudentData.class);
    }

    @Override
    public ChosenStudentData deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        JsonNode node = jsonParser.readValueAsTree();

        Student student = new Student(node.get("email").asText());

        ChosenStudentData chosenStudentData = new ChosenStudentData(student, node.get("grade").asInt());
        chosenStudentData.setTutor(new Supervisor(node.get("tutor").asText()));
        chosenStudentData.setSpecialism(node.get("specialism").asText());
        chosenStudentData.setInterestTree(node.get("interest_tree").traverse(jsonParser.getCodec()).readValueAs(Node.class));

        return chosenStudentData;
    }

}
