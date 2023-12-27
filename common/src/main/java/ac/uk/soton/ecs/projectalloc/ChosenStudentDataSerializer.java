package ac.uk.soton.ecs.projectalloc;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class ChosenStudentDataSerializer extends StdSerializer<ChosenStudentData> {

    public ChosenStudentDataSerializer() {
        this(null);
    }

    protected ChosenStudentDataSerializer(Class<ChosenStudentData> t) {
        super(t);
    }

    @Override
    public void serialize(ChosenStudentData chosenStu, JsonGenerator jsonGen, SerializerProvider serializerProvider) throws IOException {
        String email = chosenStu.getUsername();
        Integer grade = chosenStu.getGrade();
        Node tree = chosenStu.getInterestTree();
        String tutorEmail = chosenStu.getTutor().getUsername();
        String specialism = chosenStu.getSpecialism();


        jsonGen.writeStartObject(); //Creates json object
        jsonGen.writeStringField("email",email);
        jsonGen.writeNumberField("grade",grade);
        jsonGen.writeStringField("specialism",specialism);
        jsonGen.writeStringField("tutor",tutorEmail);
        jsonGen.writeObjectField("interest_tree", tree);

        jsonGen.writeEndObject();
    }
}
