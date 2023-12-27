package ac.uk.soton.ecs.projectalloc.evaluator.metric;

import ac.uk.soton.ecs.projectalloc.Pairing;
import ac.uk.soton.ecs.projectalloc.Student;
import ac.uk.soton.ecs.projectalloc.Supervisor;
import ac.uk.soton.ecs.projectalloc.evaluator.EvaluationContext;
import ac.uk.soton.ecs.projectalloc.evaluator.StudentPreference;
import ac.uk.soton.ecs.projectalloc.evaluator.StudentPreference.SupervisorRanking;
import java.util.Optional;

/**
 * EMTopN measures the number of students who get a supervisor in the top N of their preference list.
 * <p>
 * For a keyword-selection system, the preference list is extrapolated.
 */
public class EMTopN extends Metric {

    private final int n;

    public EMTopN(int n, EvaluationContext evaluationContext) {
        this.n = n;

        this.evaluationContext = evaluationContext;
    }

    @Override
    public String getName() {
        return "EMTopN-" + n;
    }

    private final EvaluationContext evaluationContext;

    public boolean isInTopN(Student student) {
        Optional<Pairing> pairing = evaluationContext.getAllocationResult(student);

        if(pairing.isEmpty()) {
            return false;
        }

        Supervisor supervisor = pairing.get().getSupervisor();

        return getPositionInStudentPreferenceRanking(student, supervisor)
            .map(position -> position <= n)
            .orElse(false);
    }

    private int getNumberOfStudentsInTopN() {
        return (int) evaluationContext.getStudentPreferences().values().stream()
            .filter(studentPreference -> isInTopN(studentPreference.getStudent()))
            .count();
    }

    private double getStudentsInTopNPercentage() {
        return (double) getNumberOfStudentsInTopN() / (double) evaluationContext.getStudentPreferences().size();
    }

    /**
     * Return the position (starting from 1) where the supervisor is in the student's rankings.
     *
     * @param student The student.
     * @param supervisor The supervisor.
     * @return The position, or empty Optional if not in list.
     */
    public Optional<Integer> getPositionInStudentPreferenceRanking(Student student, Supervisor supervisor) {
        StudentPreference studentPreference = evaluationContext.getStudentPreference(student);

        return studentPreference.getSupervisorPosition(supervisor);
    }

    public int getN() {
        return n;
    }

    @Override
    public int getPositiveCount() {
        return getNumberOfStudentsInTopN();
    }

    @Override
    public double getPositivePercent() {
        return getStudentsInTopNPercentage();
    }
}
