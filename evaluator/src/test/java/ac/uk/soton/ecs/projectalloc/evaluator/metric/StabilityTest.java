package ac.uk.soton.ecs.projectalloc.evaluator.metric;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import ac.uk.soton.ecs.projectalloc.Pairing;
import ac.uk.soton.ecs.projectalloc.Student;
import ac.uk.soton.ecs.projectalloc.Supervisor;
import ac.uk.soton.ecs.projectalloc.evaluator.EvaluationContext;
import ac.uk.soton.ecs.projectalloc.evaluator.StudentPreference;
import org.junit.jupiter.api.Test;

public class StabilityTest {

    @Test
    public void singularPairIsStable() {
        Student student = new Student("abc1g20");
        Supervisor supervisor = new Supervisor("xyz");

        StudentPreference studentPreference = new StudentPreference(student);
        studentPreference.addSupervisorWithScore(supervisor, 1);

        Pairing pairing = new Pairing(student, supervisor);

        EvaluationContext evaluationContext = new EvaluationContext();
        evaluationContext.addStudentPreference(studentPreference);
        evaluationContext.addAllocation(pairing);

        Stability stability = new Stability(evaluationContext);
        assertThat(stability.isStable(pairing), is(true));

        assertThat(stability.getPositiveCount(), is(equalTo(evaluationContext.getAllocationResult().size())));
        assertThat(stability.getPositivePercent(), is(equalTo(1.0)));
    }

    @Test
    public void twoUnhappyPairsAreNotStable() {
        Student studentOne = new Student("abc1g20");
        Student studentTwo = new Student("xyz1g20");

        Supervisor supervisorOne = new Supervisor("xyz");
        Supervisor supervisorTwo = new Supervisor("abc");

        StudentPreference studentPreferenceOne = new StudentPreference(studentOne);
        studentPreferenceOne.addSupervisorWithScore(supervisorTwo, 1);
        studentPreferenceOne.addSupervisorWithScore(supervisorOne, 0.5);

        StudentPreference studentPreferenceTwo = new StudentPreference(studentTwo);
        studentPreferenceTwo.addSupervisorWithScore(supervisorTwo, 0.5);
        studentPreferenceTwo.addSupervisorWithScore(supervisorOne, 1);

        Pairing pairingOne = new Pairing(studentOne, supervisorOne);
        Pairing pairingTwo = new Pairing(studentTwo, supervisorTwo);

        EvaluationContext evaluationContext = new EvaluationContext();
        evaluationContext.addStudentPreference(studentPreferenceOne);
        evaluationContext.addStudentPreference(studentPreferenceTwo);

        evaluationContext.addAllocation(pairingOne);
        evaluationContext.addAllocation(pairingTwo);

        Stability stability = new Stability(evaluationContext);
        assertThat(stability.isStable(pairingOne), is(false));
        assertThat(stability.isStable(pairingTwo), is(false));

        assertThat(stability.getPositiveCount(), is(equalTo(0)));
        assertThat(stability.getPositivePercent(), is(equalTo(0.0)));
    }

    @Test
    public void twoJointPairsArStable() {
        Student studentOne = new Student("abc1g20");
        Student studentTwo = new Student("xyz1g20");

        Supervisor supervisorOne = new Supervisor("xyz");
        Supervisor supervisorTwo = new Supervisor("abc");

        StudentPreference studentPreferenceOne = new StudentPreference(studentOne);
        studentPreferenceOne.addSupervisorWithScore(supervisorTwo, 1);
        studentPreferenceOne.addSupervisorWithScore(supervisorOne, 1);

        StudentPreference studentPreferenceTwo = new StudentPreference(studentTwo);
        studentPreferenceTwo.addSupervisorWithScore(supervisorTwo, 1);
        studentPreferenceTwo.addSupervisorWithScore(supervisorOne, 1);

        Pairing pairingOne = new Pairing(studentOne, supervisorOne);
        Pairing pairingTwo = new Pairing(studentTwo, supervisorTwo);

        EvaluationContext evaluationContext = new EvaluationContext();
        evaluationContext.addStudentPreference(studentPreferenceOne);
        evaluationContext.addStudentPreference(studentPreferenceTwo);

        evaluationContext.addAllocation(pairingOne);
        evaluationContext.addAllocation(pairingTwo);

        Stability stability = new Stability(evaluationContext);
        assertThat(stability.isStable(pairingOne), is(true));
        assertThat(stability.isStable(pairingTwo), is(true));

        assertThat(stability.getPositiveCount(), is(equalTo(2)));
        assertThat(stability.getPositivePercent(), is(equalTo(1.0)));
    }

}
