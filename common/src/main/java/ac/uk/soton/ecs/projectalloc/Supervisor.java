package ac.uk.soton.ecs.projectalloc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Supervisor extends Participant {

    @JsonCreator
    public Supervisor(@JsonProperty("username") String username) {
        super(username);
    }

}