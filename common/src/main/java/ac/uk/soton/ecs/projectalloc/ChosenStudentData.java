package ac.uk.soton.ecs.projectalloc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Objects;

@JsonSerialize(using = ChosenStudentDataSerializer.class)
@JsonDeserialize(using = ChosenStudentDataDeserializer.class)
public class ChosenStudentData extends ChosenParticipantData {

    private final int grade;

    private Supervisor tutor;

    public ChosenStudentData(Participant participant, int grade) {
        super(participant);
        this.grade = grade;
    }

    public Student getStudent() {
        return (Student) participant;
    }

    public int getGrade() {
        return grade;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof ChosenStudentData that)) {
            return false;
        }

        return super.equals(o) && grade == that.grade;
    }

    @Override
    public int hashCode() {
        return Objects.hash(grade);
    }

    public Supervisor getTutor() {
        return tutor;
    }

    public void setTutor(Supervisor tutor) {
        this.tutor = tutor;
    }

}
