package ac.uk.soton.ecs.projectalloc.evaluator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import ac.uk.soton.ecs.projectalloc.ErrorResponse;
import ac.uk.soton.ecs.projectalloc.Pairing;
import ac.uk.soton.ecs.projectalloc.Student;
import ac.uk.soton.ecs.projectalloc.Supervisor;
import ac.uk.soton.ecs.projectalloc.evaluator.EvaluatorController.EvaluatorResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;

/**
 * gdp-projectalloc - Developed by Lewes D. B. (Boomclaw). All rights reserved 2023.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@ContextConfiguration(classes = {EvaluatorApplication.class})
public class EvaluatorControllerTest {

    @LocalServerPort
    private int serverPort;

    private final TestRestTemplate restTemplate = new TestRestTemplate();

    @Test
    public void valid_success() throws JsonProcessingException {
        EvaluationContext context = new EvaluationContext();

        Student student = new Student("abc1g20");
        Supervisor supervisor = new Supervisor("xyz");

        StudentPreference studentPreference = new StudentPreference(student);
        studentPreference.addSupervisorWithScore(supervisor, 1);

        context.addStudentPreference(studentPreference);
        context.addAllocation(new Pairing(student, supervisor));

        ResponseEntity<EvaluatorResponse> response = restTemplate.postForEntity("http://localhost:" + serverPort + "/v1.0/evaluator",
            context,
            EvaluatorResponse.class);

        assertThat(response.getStatusCode().is2xxSuccessful(), is(true));

        assertThat(response.getBody().getMetrics().get("emtopn-1").count(), is(1));
        assertThat(response.getBody().getMetrics().get("emtopn-1").percentage(), is(1.0));

        assertThat(response.getBody().getMetrics().get("stability").count(), is(1));
        assertThat(response.getBody().getMetrics().get("stability").percentage(), is(1.0));

        assertThat(response.getBody().getMetrics().get("keywordsimilarity").count(), is(1));
        assertThat(response.getBody().getMetrics().get("keywordsimilarity").percentage(), is(1.0));
    }

    @Test
    public void noData_error() {
        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity("http://localhost:" + serverPort + "/v1.0/evaluator",
            new EvaluationContext(),
            ErrorResponse.class);

        assertThat(response.getStatusCode().is4xxClientError(), is(true));
        assertThat(response.getBody().errorMessage(), is("Student preferences or allocation is empty"));
    }

}