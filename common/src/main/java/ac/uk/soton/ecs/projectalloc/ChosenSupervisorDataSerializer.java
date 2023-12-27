package ac.uk.soton.ecs.projectalloc;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class ChosenSupervisorDataSerializer extends StdSerializer<ChosenSupervisorData> {
    public ChosenSupervisorDataSerializer() {
        this(null);
    }

    protected ChosenSupervisorDataSerializer(Class<ChosenSupervisorData> t) {
        super(t);
    }

    @Override
    public void serialize(ChosenSupervisorData chosenSup, JsonGenerator jsonGen, SerializerProvider serializerProvider) throws IOException {
        String email = chosenSup.getUsername();
        String specialism = chosenSup.getSpecialism();
        Integer maxAllocations = chosenSup.getMaxSupervisees();
        Node tree = chosenSup.getInterestTree();

        jsonGen.writeStartObject();
        jsonGen.writeStringField("email",email);
        jsonGen.writeStringField("specialism",specialism);
        jsonGen.writeNumberField("max_allocations",maxAllocations);
        jsonGen.writeObjectField("interest_tree",tree);

        jsonGen.writeEndObject();
    }
}
