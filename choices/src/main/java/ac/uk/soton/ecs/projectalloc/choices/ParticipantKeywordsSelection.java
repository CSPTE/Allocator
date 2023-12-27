package ac.uk.soton.ecs.projectalloc.choices;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import java.util.Objects;

@Entity
public class ParticipantKeywordsSelection {

    @Id
    private String participantId;

    private boolean supervisor = false;

    @OneToOne
    private SelectedKeywordNode selectedKeywordTree;

    public String getId() {
        return participantId;
    }

    public void setId(String id) {
        this.participantId = id;
    }

    public boolean isSupervisor() {
        return supervisor;
    }

    public void setSupervisor(boolean supervisor) {
        this.supervisor = supervisor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ParticipantKeywordsSelection that = (ParticipantKeywordsSelection) o;

        return supervisor == that.supervisor &&
            Objects.equals(participantId, that.participantId) &&
            Objects.equals(selectedKeywordTree, that.selectedKeywordTree);
    }

    @Override
    public int hashCode() {
        return Objects.hash(participantId, supervisor, selectedKeywordTree);
    }

    public SelectedKeywordNode getSelectedKeywordTree() {
        return selectedKeywordTree;
    }

    public void setSelectedKeywordTree(SelectedKeywordNode selectedKeywordTree) {
        this.selectedKeywordTree = selectedKeywordTree;
    }
}
