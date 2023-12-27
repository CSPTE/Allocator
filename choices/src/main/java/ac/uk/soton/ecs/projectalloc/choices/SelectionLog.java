package ac.uk.soton.ecs.projectalloc.choices;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
public class SelectionLog {

    @Id
    @GeneratedValue
    private long id;

    private long time;

    private String userId;

    private boolean supervisor;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private final Set<SelectionLogKeywords> keywords = new HashSet<>();

    public SelectionLog(long time, String userId, boolean supervisor) {
        this.time = time;
        this.userId = userId;
        this.supervisor = supervisor;
    }

    public SelectionLog() {

    }

    public long getId() {
        return id;
    }

    public long getTime() {
        return time;
    }

    public String getUserId() {
        return userId;
    }

    public boolean isSupervisor() {
        return supervisor;
    }

    public Set<SelectionLogKeywords> getKeywords() {
        return keywords;
    }

    @Entity
    public static class SelectionLogKeywords {

        @Id
        @GeneratedValue
        private long id;

        private long keywordId;
        private String keywordName;

        public SelectionLogKeywords(long keywordId, String keywordName) {
            this.keywordId = keywordId;
            this.keywordName = keywordName;
        }

        public SelectionLogKeywords() {

        }

        public void setKeywordId(long keywordId) {
            this.keywordId = keywordId;
        }

        public void setKeywordName(String keywordName) {
            this.keywordName = keywordName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }

            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            SelectionLogKeywords that = (SelectionLogKeywords) o;

            return keywordId == that.keywordId && Objects.equals(keywordName, that.keywordName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(keywordId, keywordName);
        }
    }

}
