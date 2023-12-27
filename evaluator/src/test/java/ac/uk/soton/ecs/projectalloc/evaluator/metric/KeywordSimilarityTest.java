package ac.uk.soton.ecs.projectalloc.evaluator.metric;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import ac.uk.soton.ecs.projectalloc.Pairing;
import ac.uk.soton.ecs.projectalloc.Student;
import ac.uk.soton.ecs.projectalloc.Supervisor;
import ac.uk.soton.ecs.projectalloc.evaluator.EvaluationContext;
import ac.uk.soton.ecs.projectalloc.evaluator.StudentPreference;
import java.util.List;
import org.junit.jupiter.api.Test;

public class KeywordSimilarityTest {

    @Test
    public void testSingularStudentGetsCorrectAverage() {
        Student student = new Student("abc1g20");
        StudentPreference studentPreference = new StudentPreference(student);
        EvaluationContext evaluationContext = new EvaluationContext();

        addThreeStudentSupervisorPreferences(studentPreference);

        evaluationContext.addStudentPreference(studentPreference);

        List.of(new Pairing(new Student("abc1g20"), new Supervisor("first_preference")))
            .forEach(evaluationContext::addAllocation);

        KeywordSimilarity keywordSimilarity = new KeywordSimilarity(evaluationContext);

        assertThat(keywordSimilarity.getScore(student), is(1.0));
        assertThat(keywordSimilarity.getAverageScore(), is(1.0));
    }

    @Test
    public void testMultipleStudentGetsCorrectAverage() {
        Student studentOne = new Student("abc1g20");
        Student studentTwo = new Student("xyz1g20");

        Supervisor supervisorOne = new Supervisor("xyz");
        Supervisor supervisorTwo = new Supervisor("abc");

        StudentPreference studentPreferenceOne = new StudentPreference(studentOne);
        studentPreferenceOne.addSupervisorWithScore(supervisorTwo, 0.5);
        studentPreferenceOne.addSupervisorWithScore(supervisorOne, 1);

        StudentPreference studentPreferenceTwo = new StudentPreference(studentTwo);
        studentPreferenceTwo.addSupervisorWithScore(supervisorTwo, 0.5);
        studentPreferenceTwo.addSupervisorWithScore(supervisorOne, 0.2);

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

        KeywordSimilarity keywordSimilarity = new KeywordSimilarity(evaluationContext);

        assertThat(keywordSimilarity.getScore(studentOne), is(1.0));
        assertThat(keywordSimilarity.getScore(studentTwo), is(0.5));
        assertThat(keywordSimilarity.getAverageScore(), is(0.75));
    }

    private static void addThreeStudentSupervisorPreferences(StudentPreference studentPreference) {
        studentPreference.addSupervisorWithScore(new Supervisor("first_preference"), 1);
        studentPreference.addSupervisorWithScore(new Supervisor("second_preference"), 0.9);
        studentPreference.addSupervisorWithScore(new Supervisor("third_preference"), 0.8);
    }

}