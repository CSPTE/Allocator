package ac.uk.soton.ecs.projectalloc.evaluator.metric;

import ac.uk.soton.ecs.projectalloc.Pairing;
import ac.uk.soton.ecs.projectalloc.Student;
import ac.uk.soton.ecs.projectalloc.Supervisor;
import ac.uk.soton.ecs.projectalloc.evaluator.EvaluationContext;
import ac.uk.soton.ecs.projectalloc.evaluator.StudentPreference.SupervisorRanking;

/**
 * Stability measures the number of students in a stable pair.
 * <p>
 * A pair is stable when given any other pair, the two students would prefer to have swapped supervisors.
 */
public class Stability extends Metric {

    private final EvaluationContext evaluationContext;

    public Stability(EvaluationContext evaluationContext) {
        this.evaluationContext = evaluationContext;
    }

    @Override
    public String getName() {
        return "Stability";
    }

    private int getNumberOfUnstablePairs() {
        return (int) evaluationContext.getAllocationResult()
            .stream()
            .filter(pair -> !isStable(pair))
            .count();
    }

    public boolean isStable(Pairing checkingPair) {
        int checkingAllocated = getAllocatedSupervisorPosition(checkingPair);

        for(Pairing comparePair : evaluationContext.getAllocationResult()) {
            if(comparePair.equals(checkingPair)) {
                continue;
            }

            int compareAllocated = getAllocatedSupervisorPosition(comparePair);

            int compareWantedChecking = getSupervisorPosition(comparePair.getStudent(), checkingPair.getSupervisor());
            int checkingWantedCompare = getSupervisorPosition(checkingPair.getStudent(), comparePair.getSupervisor());

            if(compareWantedChecking < compareAllocated && checkingWantedCompare < checkingAllocated) {
                return false;
            }
        }

        return true;
    }

    private Integer getAllocatedSupervisorPosition(Pairing pairing) {
        return getSupervisorPosition(pairing.getStudent(), pairing.getSupervisor());
    }

    private Integer getSupervisorPosition(Student student, Supervisor supervisor) {
        return evaluationContext.getStudentPreference(student).getSupervisorPosition(supervisor).orElse(Integer.MAX_VALUE);
    }

    @Override
    public int getPositiveCount() {
        return evaluationContext.getAllocationResult().size() - getNumberOfUnstablePairs();
    }

    @Override
    public double getPositivePercent() {
        if(evaluationContext.getAllocationResult().isEmpty()) {
            return 0;
        }

        return 1 - (double) getNumberOfUnstablePairs() / evaluationContext.getAllocationResult().size();
    }
}
