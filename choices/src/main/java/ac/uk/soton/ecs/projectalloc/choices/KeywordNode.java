package ac.uk.soton.ecs.projectalloc.choices;

import ac.uk.soton.ecs.projectalloc.Node;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.List;
import java.util.Objects;

@Entity
@JsonDeserialize(converter= KeywordNodeSanitizer.class)
public class KeywordNode {

    @Id
    @GeneratedValue
    private Long id;
    private String value;
    @ManyToOne(cascade = CascadeType.ALL)
    @JsonIgnore
    private KeywordNode parent;
    @OneToMany(cascade = CascadeType.ALL)
    private List<KeywordNode> children;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public KeywordNode getParent() {
        return parent;
    }

    public void setParent(KeywordNode parent) {
        this.parent = parent;
    }

    public List<KeywordNode> getChildren() {
        return children;
    }

    public void setChildren(List<KeywordNode> children) {
        this.children = children;
    }

    public boolean equals(Node node) {
        if(!node.getInterest().equals(value)) {
            return false;
        }

        int matching = 0;

        outer: for(Node childNode : node.getChildren()) {
            for(KeywordNode childKeynode : children) {
                if(!childKeynode.equals(childNode)) {
                    continue;
                }

                matching++;
                continue outer;
            }
        }

        return matching == children.size() &&
            children.size() == node.getChildren().size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null) {
            return false;
        }

        if(o instanceof Node node) {
            return equals(node);
        }

        if(getClass() != o.getClass()) {
            return false;
        }

        KeywordNode that = (KeywordNode) o;

        return Objects.equals(id, that.id) &&
            Objects.equals(value, that.value) &&
            ((parent == null && that.parent == null) ||
                (parent != null && that.parent != null && Objects.equals(parent.id, that.parent.id))) &&
            Objects.equals(children, that.children);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, value, children);
    }
}
