package ac.uk.soton.ecs.projectalloc.choices;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
public class SelectedKeywordNode {

    @Id
    @GeneratedValue
    private Long id;

    private Long keywordNodeId;

    @ManyToOne(cascade = CascadeType.ALL)
    @JsonIgnore
    private SelectedKeywordNode parent;

    @OneToMany(cascade = CascadeType.ALL)
    private Set<SelectedKeywordNode> children = new HashSet<>();

    public Long getId() {
        return id;
    }

    private void setId(Long id) {
        this.id = id;
    }

    public Long getKeywordNodeId() {
        return keywordNodeId;
    }

    public void setKeywordNodeId(Long keywordNodeId) {
        this.keywordNodeId = keywordNodeId;
    }

    public SelectedKeywordNode getParent() {
        return parent;
    }

    public void setParent(SelectedKeywordNode parent) {
        this.parent = parent;
    }

    public Set<SelectedKeywordNode> getChildren() {
        return children;
    }

    private void setChildren(Set<SelectedKeywordNode> children) {
        this.children = children;
    }

    public void addChild(SelectedKeywordNode child) {
        getChildren().add(child);
        child.setParent(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SelectedKeywordNode that = (SelectedKeywordNode) o;

        return Objects.equals(id, that.id) &&
            Objects.equals(keywordNodeId, that.keywordNodeId) &&
            ((parent == null && that.parent == null) ||
                (parent != null && that.parent != null && Objects.equals(parent.id, that.parent.id))) &&
            Objects.equals(children, that.children);
    }

}
