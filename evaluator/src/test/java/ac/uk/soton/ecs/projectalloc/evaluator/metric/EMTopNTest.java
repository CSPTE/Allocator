package ac.uk.soton.ecs.projectalloc.evaluator.metric;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import ac.uk.soton.ecs.projectalloc.Pairing;
import ac.uk.soton.ecs.projectalloc.Student;
import ac.uk.soton.ecs.projectalloc.Supervisor;
import ac.uk.soton.ecs.projectalloc.evaluator.EvaluationContext;
import ac.uk.soton.ecs.projectalloc.evaluator.StudentPreference;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import org.junit.jupiter.api.Test;

public class EMTopNTest {

    @Test
    public void testValidPositionInStudentRanking() {
        Student student = new Student("abc1g20");
        StudentPreference studentPreference = new StudentPreference(student);
        EvaluationContext evaluationContext = new EvaluationContext();

        addThreeStudentSupervisorPreferences(studentPreference);

        evaluationContext.addStudentPreference(studentPreference);

        List.of(new Pairing(new Student("abc1g20"), new Supervisor("first_preference")))
            .forEach(evaluationContext::addAllocation);

        EMTopN emTopN = new EMTopN(3, evaluationContext);

        assertThat(emTopN.getN(), is(3));

        assertThat(emTopN.getPositionInStudentPreferenceRanking(student, new Supervisor("first_preference")).get(), is(equalTo(1)));
        assertThat(emTopN.getPositionInStudentPreferenceRanking(student, new Supervisor("second_preference")).get(), is(equalTo(2)));
        assertThat(emTopN.getPositionInStudentPreferenceRanking(student, new Supervisor("third_preference")).get(), is(equalTo(3)));

        assertThat(emTopN.isInTopN(student), is(true));
    }

    @Test
    public void studentGotSupervisorOutsideOfPreferenceList() {
        Student student = new Student("abc1g20");
        StudentPreference studentPreference = new StudentPreference(student);
        EvaluationContext evaluationContext = new EvaluationContext();

        addThreeStudentSupervisorPreferences(studentPreference);

        evaluationContext.addStudentPreference(studentPreference);

        List.of(new Pairing(new Student("abc1g20"), new Supervisor("not_in_list")))
            .forEach(evaluationContext::addAllocation);

        EMTopN emTopN = new EMTopN(3, evaluationContext);
        assertThat(emTopN.isInTopN(student), is(false));
    }

    @Test
    public void testStudentGotOutsideOfTopN() {
        Student student = new Student("abc1g20");
        StudentPreference studentPreference = new StudentPreference(student);
        EvaluationContext evaluationContext = new EvaluationContext();

        addThreeStudentSupervisorPreferences(studentPreference);

        evaluationContext.addStudentPreference(studentPreference);

        List.of(new Pairing(new Student("abc1g20"), new Supervisor("second_preference")))
            .forEach(evaluationContext::addAllocation);

        EMTopN emTopN = new EMTopN(1, evaluationContext);
        assertThat(emTopN.isInTopN(student), is(false));
    }

    private static void addThreeStudentSupervisorPreferences(StudentPreference studentPreference) {
        studentPreference.addSupervisorWithScore(new Supervisor("first_preference"), 1);
        studentPreference.addSupervisorWithScore(new Supervisor("second_preference"), 0.9);
        studentPreference.addSupervisorWithScore(new Supervisor("third_preference"), 0.8);
    }

    @Test
    public void testMassStudentsReturnCorrectPercent() {
        double numOfHappyStudents = 5;
        double numOfUnhappyStudents = 30; // unhappy: students who did not get their top choice

        List<StudentPreference> preferences = new ArrayList<>();
        List<Pairing> pairings = new ArrayList<>();

        for(int x = 0; x < numOfHappyStudents; x++) {
            Student student = new Student(UUID.randomUUID().toString());
            StudentPreference studentPreference = new StudentPreference(student);

            for(int pos = 1; pos <= new Random().nextInt(10) + 10; pos++) {
                studentPreference.addSupervisorWithScore(new Supervisor(UUID.randomUUID().toString()), pos);
            }

            preferences.add(studentPreference);
            pairings.add(new Pairing(student, studentPreference.getOrderedSupervisorRanking().get(0).getSupervisor()));
        }

        for(int x = 0; x < numOfUnhappyStudents; x++) {
            Student student = new Student(UUID.randomUUID().toString());
            StudentPreference studentPreference = new StudentPreference(student);

            int numOfSups = new Random().nextInt(10) + 10;

            for(int pos = 1; pos <= numOfSups; pos++) {
                studentPreference.addSupervisorWithScore(new Supervisor(UUID.randomUUID().toString()), pos);
            }

            preferences.add(studentPreference);
            pairings.add(new Pairing(student, studentPreference.getOrderedSupervisorRanking().get(new Random().nextInt(numOfSups - 1) + 1).getSupervisor()));
        }

        EvaluationContext evaluationContext = new EvaluationContext();
        preferences.forEach(evaluationContext::addStudentPreference);
        pairings.forEach(evaluationContext::addAllocation);

        EMTopN emTopN = new EMTopN(1, evaluationContext);

        assertThat(emTopN.getPositivePercent(), is(equalTo(numOfHappyStudents / (numOfHappyStudents + numOfUnhappyStudents))));
    }

    @Test
    public void testJointPositionInStudentRanking() {
        Student student = new Student("abc1g20");
        StudentPreference studentPreference = new StudentPreference(student);
        EvaluationContext evaluationContext = new EvaluationContext();

        studentPreference.addSupervisorWithScore(new Supervisor("first_preference"), 1);
        studentPreference.addSupervisorWithScore(new Supervisor("joint_first_preference"), 1);
        studentPreference.addSupervisorWithScore(new Supervisor("second_preference"), 0.7);

        evaluationContext.addStudentPreference(studentPreference);

        List.of(new Pairing(new Student("abc1g20"), new Supervisor("first_preference")))
            .forEach(evaluationContext::addAllocation);

        EMTopN emTopN = new EMTopN(3, evaluationContext);

        assertThat(emTopN.getN(), is(3));

        assertThat(emTopN.getPositionInStudentPreferenceRanking(student, new Supervisor("first_preference")).get(), is(equalTo(1)));
        assertThat(emTopN.getPositionInStudentPreferenceRanking(student, new Supervisor("joint_first_preference")).get(), is(equalTo(1)));
        assertThat(emTopN.getPositionInStudentPreferenceRanking(student, new Supervisor("second_preference")).get(), is(equalTo(2)));

        assertThat(emTopN.isInTopN(student), is(true));
    }

}