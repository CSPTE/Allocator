package ac.uk.soton.ecs.projectalloc.evaluator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;

import ac.uk.soton.ecs.projectalloc.Student;
import ac.uk.soton.ecs.projectalloc.Supervisor;
import ac.uk.soton.ecs.projectalloc.evaluator.StudentPreference.SupervisorRanking;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class StudentPreferenceTest {

    @Test
    public void studentPreferences_hasStudent() {
        Student student = new Student("abc1g20");

        StudentPreference studentPreference = new StudentPreference(student);

        assertThat(studentPreference.getStudent(), is(student));
    }

    @Test
    public void studentPreferences_hasOrderedRanking() {
        Student student = new Student("abc1g20");
        StudentPreference studentPreference = new StudentPreference(student);

        Map<Supervisor, Double> supervisorId2Position = Map.of(new Supervisor("first_preference_supervisor"), 1.0,
            new Supervisor("second_preference"), 0.5,
            new Supervisor("third_preference"), 0.1);

        supervisorId2Position.forEach(studentPreference::addSupervisorWithScore);

        double previousScore = Double.MAX_VALUE;

        for(SupervisorRanking supervisorRanking : studentPreference.getOrderedSupervisorRanking()) {
            assertThat(supervisorRanking.getScore(), is(allOf(
                lessThanOrEqualTo(previousScore),
                equalTo(supervisorId2Position.get(supervisorRanking.getSupervisor()))
            )));

            previousScore = supervisorRanking.getScore();
        }
    }

}
