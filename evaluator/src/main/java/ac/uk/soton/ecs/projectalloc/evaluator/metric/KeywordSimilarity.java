package ac.uk.soton.ecs.projectalloc.evaluator.metric;

import ac.uk.soton.ecs.projectalloc.Pairing;
import ac.uk.soton.ecs.projectalloc.Student;
import ac.uk.soton.ecs.projectalloc.evaluator.EvaluationContext;
import ac.uk.soton.ecs.projectalloc.evaluator.StudentPreference.SupervisorRanking;
import java.util.Optional;

/**
 * Keyword Similarity returns the average score across all student's to their allocated supervisor.
 * <p/>
 * For example, if studentA got supervisorB with a 0.77 score, they would return 0.77 to be averaged.
 */
public class KeywordSimilarity extends Metric {

    public KeywordSimilarity(EvaluationContext evaluationContext) {
        this.evaluationContext = evaluationContext;
    }

    @Override
    public String getName() {
        return "KeywordSimilarity";
    }

    private final EvaluationContext evaluationContext;

    public double getScore(Student student) {
        Optional<Pairing> pairing = evaluationContext.getAllocationResult(student);

        if(pairing.isEmpty()) {
            return 0;
        }

        return Optional.ofNullable(evaluationContext.getStudentPreference(student).getSupervisorRankings().get(pairing.get().getSupervisor()))
            .map(SupervisorRanking::getScore)
            .orElse(0D);
    }

    public double getAverageScore() {
        return evaluationContext.getAllocationResult()
            .stream().mapToDouble(pairing -> getScore(pairing.getStudent()))
            .average()
            .getAsDouble();
    }

    @Override
    public int getPositiveCount() {
        return evaluationContext.getAllocationResult().size();
    }

    @Override
    public double getPositivePercent() {
        return getAverageScore();
    }
}
