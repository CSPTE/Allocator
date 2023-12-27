package ac.uk.soton.ecs.projectalloc.allocator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import ac.uk.soton.ecs.projectalloc.ErrorResponse;
import ac.uk.soton.ecs.projectalloc.Pairing;
import ac.uk.soton.ecs.projectalloc.Student;
import ac.uk.soton.ecs.projectalloc.allocator.AllocatorController.AlgorithmType;
import ac.uk.soton.ecs.projectalloc.allocator.AllocatorController.AllocatorRequest;
import ac.uk.soton.ecs.projectalloc.allocator.AllocatorController.AllocatorResponse;
import ac.uk.soton.ecs.projectalloc.datagen.DataSet;
import ac.uk.soton.ecs.projectalloc.datagen.DataSetBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
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
@ContextConfiguration(classes = {AllocatorApplication.class})
public class AllocatorControllerTest {

    @LocalServerPort
    private int serverPort;

    private final TestRestTemplate restTemplate = new TestRestTemplate();

    @Test
    public void stable_success() {
        int studentCount = 100;

        DataSet builder = getHealthyDataSet(studentCount).build();

        ResponseEntity<AllocatorResponse> response = restTemplate.postForEntity("http://localhost:" + serverPort + "/v1.0/allocator",
            new AllocatorRequest(builder.getStudents(), builder.getSupervisors(), AlgorithmType.STABLE),
            AllocatorResponse.class);

        AllocatorResponse responseBody = response.getBody();

        assertThat(responseBody.algorithm(), is(AlgorithmType.STABLE));
        assertThat(responseBody.pairings().size(), is(studentCount));
        assertThat(getUniqueStudents(responseBody.pairings()).size(), is(studentCount));
        assertThat(response.getStatusCode().is2xxSuccessful(), is(true));
    }

    @Test
    public void stable_emptyStudentListInput_error() {
        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity("http://localhost:" + serverPort + "/v1.0/allocator",
            new AllocatorRequest(new ArrayList<>(), new ArrayList<>(), AlgorithmType.STABLE),
            ErrorResponse.class);

        assertThat(response.getBody().errorMessage(), is("More students than slots available"));
        assertThat(response.getStatusCode().is4xxClientError(), is(true));
    }

    @Test
    public void unknownAlgo_error() {
        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity("http://localhost:" + serverPort + "/v1.0/allocator",
            new AllocatorRequest(new ArrayList<>(), new ArrayList<>(), null),
            ErrorResponse.class);

        assertThat(response.getBody().errorMessage(), is("Unknown algorithm"));
        assertThat(response.getStatusCode().is4xxClientError(), is(true));
    }

    @Test
    public void linear_success() {
        int studentCount = 30;

        DataSet builder = getHealthyDataSet(studentCount).build();

        ResponseEntity<AllocatorResponse> response = restTemplate.postForEntity("http://localhost:" + serverPort + "/v1.0/allocator",
            new AllocatorRequest(builder.getStudents(), builder.getSupervisors(), AlgorithmType.LINEAR),
            AllocatorResponse.class);

        AllocatorResponse responseBody = response.getBody();

        assertThat(responseBody.algorithm(), is(AlgorithmType.LINEAR));
        assertThat(responseBody.pairings().size(), is(studentCount));
        assertThat(getUniqueStudents(responseBody.pairings()).size(), is(studentCount));
        assertThat(response.getStatusCode().is2xxSuccessful(), is(true));
    }

    @Test
    public void linear_emptySupervisorListInput_error() {
        int studentCount = 30;

        DataSet builder = getHealthyDataSet(studentCount).build();

        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity("http://localhost:" + serverPort + "/v1.0/allocator",
            new AllocatorRequest(builder.getStudents(), new ArrayList<>(), AlgorithmType.LINEAR),
            ErrorResponse.class);

        assertThat(response.getBody().errorMessage(), is("No Supervisors in list"));
        assertThat(response.getStatusCode().is4xxClientError(), is(true));
    }

    @Test
    public void annealingClassic_success() {
        int studentCount = 30;

        DataSet builder = getHealthyDataSet(studentCount).build();

        ResponseEntity<AllocatorResponse> response = restTemplate.postForEntity("http://localhost:" + serverPort + "/v1.0/allocator",
            new AllocatorRequest(builder.getStudents(), builder.getSupervisors(), AlgorithmType.ANNEALING_CLASSIC),
            AllocatorResponse.class);

        AllocatorResponse responseBody = response.getBody();

        assertThat(responseBody.algorithm(), is(AlgorithmType.ANNEALING_CLASSIC));
        assertThat(responseBody.pairings().size(), is(studentCount));
        assertThat(getUniqueStudents(responseBody.pairings()).size(), is(studentCount));
        assertThat(response.getStatusCode().is2xxSuccessful(), is(true));
    }

    @Test
    public void annealingVariant_success() {
        int studentCount = 30;

        DataSet builder = getHealthyDataSet(studentCount).build();

        ResponseEntity<AllocatorResponse> response = restTemplate.postForEntity("http://localhost:" + serverPort + "/v1.0/allocator",
            new AllocatorRequest(builder.getStudents(), builder.getSupervisors(), AlgorithmType.ANNEALING_VARIANT),
            AllocatorResponse.class);

        AllocatorResponse responseBody = response.getBody();

        assertThat(responseBody.algorithm(), is(AlgorithmType.ANNEALING_VARIANT));
        assertThat(responseBody.pairings().size(), is(studentCount));
        assertThat(getUniqueStudents(responseBody.pairings()).size(), is(studentCount));
        assertThat(response.getStatusCode().is2xxSuccessful(), is(true));
    }


    private static DataSetBuilder getHealthyDataSet(int studentCount) {
        return new DataSetBuilder()
            .numStudents(studentCount)
            .numSupervisors(80)
            .perc1st(20)
            .perc21(20)
            .perc22(20)
            .perc3rd(20)
            .percLT3rd(20)
            .stuPercComp(20)
            .stuPercAi(20)
            .stuPercElec(20)
            .stuPercCS(20)
            .stuPercSE(20)
            .supPercComp(20)
            .supPercAi(20)
            .supPercElec(20)
            .supPercCS(20)
            .supPercSE(20)
            .minSupervisees(5)
            .maxSupervisees(10)
            .numInterests(10)
            .numSelectedInterests(5)
            .minChildren(2)
            .maxChildren(5);
    }

    private static Set<Student> getUniqueStudents(List<Pairing> pairings) {
        return pairings.stream()
            .map(Pairing::getStudent)
            .collect(Collectors.toSet());
    }

}