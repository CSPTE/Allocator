package ac.uk.soton.ecs.projectalloc;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = ChosenSupervisorDataSerializer.class)
@JsonDeserialize(using = ChosenSupervisorDataDeserializer.class)
public class ChosenSupervisorData extends ChosenParticipantData {

    private final int maxSupervisees;

    public ChosenSupervisorData(Supervisor supervisor, int supervises) {
        super(supervisor);

        this.maxSupervisees = supervises;
    }

    public Supervisor getSupervisor() {
        return (Supervisor) participant;
    }

    public int getMaxSupervisees() {
        return maxSupervisees;
    }
}
