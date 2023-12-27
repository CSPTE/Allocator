package ac.uk.soton.ecs.projectalloc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Student extends Participant {

    @JsonCreator
    public Student(@JsonProperty("username") String username) {
        super(username);
    }

}
