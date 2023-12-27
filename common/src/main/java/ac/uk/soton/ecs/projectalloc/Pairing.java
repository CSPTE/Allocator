package ac.uk.soton.ecs.projectalloc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Pairing {

    private final Student student;
    private final Supervisor supervisor;

    @JsonCreator
    public Pairing(@JsonProperty("student") Student student,
        @JsonProperty("supervisor") Supervisor supervisor){
        this.student = student;
        this.supervisor = supervisor;
    }

    public Student getStudent() {
        return student;
    }

    public Supervisor getSupervisor() {
        return supervisor;
    }

}
