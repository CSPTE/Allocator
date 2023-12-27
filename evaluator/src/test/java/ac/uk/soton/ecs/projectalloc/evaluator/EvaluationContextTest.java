package ac.uk.soton.ecs.projectalloc.evaluator;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;

import ac.uk.soton.ecs.projectalloc.Pairing;
import ac.uk.soton.ecs.projectalloc.Student;
import ac.uk.soton.ecs.projectalloc.Supervisor;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EvaluationContextTest {

    @Test
    public void testContext_containsStudentPreferences() {
        Student student = new Student("abc1g20");
        StudentPreference studentPreference = new StudentPreference(student);

        studentPreference.addSupervisorWithScore(new Supervisor("first_preference"), 1);
        studentPreference.addSupervisorWithScore(new Supervisor("second_preference"), 0.5);
        studentPreference.addSupervisorWithScore(new Supervisor("third_preference"), 0.1);

        EvaluationContext evaluationContext = new EvaluationContext();
        evaluationContext.addStudentPreference(studentPreference);

        assertThat(evaluationContext.getStudentPreferences().values(), contains(studentPreference));
    }

    @Test
    public void testContext_containsAllocationResult() {
        EvaluationContext evaluationContext = new EvaluationContext();

        Set<Pairing> pairs = new HashSet<>(asList(new Pairing(new Student("abc1g20"), new Supervisor("supervisor")),
            new Pairing(new Student("xyz1g21"), new Supervisor("supervisor2")),
            new Pairing(new Student("qwe1g22"), new Supervisor("supervisor3"))));

        pairs.forEach(evaluationContext::addAllocation);

        assertThat(evaluationContext.getAllocationResult(), containsInAnyOrder(pairs.toArray()));
    }

    @Test
    public void studentHasNoAllocation() {
        EvaluationContext context = new EvaluationContext();
        Student student = new Student(UUID.randomUUID().toString());

        assertThat(context.getAllocationResult(student), hasProperty("empty", is(true)));
    }

}