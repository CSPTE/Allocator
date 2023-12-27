package ac.uk.soton.ecs.projectalloc.evaluator;

import ac.uk.soton.ecs.projectalloc.Student;
import ac.uk.soton.ecs.projectalloc.Supervisor;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class StudentPreference {

    private final Student student;

    public Map<Supervisor, SupervisorRanking> getSupervisorRankings() {
        return supervisorRankings;
    }

    private final Map<Supervisor, SupervisorRanking> supervisorRankings = new HashMap<>();

    @JsonCreator
    public StudentPreference(@JsonProperty("student") Student student) {
        this.student = student;
    }

    @JsonIgnore
    public List<SupervisorRanking> getOrderedSupervisorRanking() {
        return supervisorRankings.values().stream()
            .sorted((o1, o2) -> Double.compare(o2.score, o1.score))
            .toList();
    }

    public void addSupervisorWithScore(Supervisor supervisor, double score) {
        supervisorRankings.put(supervisor, new SupervisorRanking(supervisor, score));
    }

    public Student getStudent() {
        return student;
    }

    public Optional<Integer> getSupervisorPosition(Supervisor supervisor) {
        int position = 0;
        double score = Double.MAX_VALUE;

        for(SupervisorRanking sup : getOrderedSupervisorRanking()) {
            if(sup.getScore() != score) {
                position++;
                score = sup.getScore();
            }

            if(sup.getSupervisor().equals(supervisor)) {
                return Optional.of(position);
            }
        }

        return Optional.empty();
    }

    public static class SupervisorRanking {

        private final Supervisor supervisor;
        private final double score;

        @JsonCreator
        SupervisorRanking(@JsonProperty("supervisor") Supervisor supervisorId,
            @JsonProperty("score") double score) {
            this.supervisor = supervisorId;
            this.score = score;
        }

        public Supervisor getSupervisor() {
            return supervisor;
        }

        public double getScore() {
            return score;
        }

    }

}
