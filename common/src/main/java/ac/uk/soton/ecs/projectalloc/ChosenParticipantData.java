package ac.uk.soton.ecs.projectalloc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public abstract class ChosenParticipantData {

    protected final Participant participant;

    private Node interestTree;

    private String specialism;

    public ChosenParticipantData(Participant participant) {
        this.participant = participant;
    }

    public String getUsername() {
        return participant.getUsername();
    }

    public Node getInterestTree() {return this.interestTree;}

    public void setInterestTree(Node root) {this.interestTree = root;}

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof ChosenParticipantData that)) {
            return false;
        }

        return Objects.equals(participant, that.participant) && Objects.equals(interestTree.getAllInterests(), that.interestTree.getAllInterests());
    }

    @Override
    public int hashCode() {
        return Objects.hash(participant, interestTree.getAllNodes());
    }

    public String getSpecialism() {return specialism;}

    public void setSpecialism(String specialism) {this.specialism = specialism;}
}
