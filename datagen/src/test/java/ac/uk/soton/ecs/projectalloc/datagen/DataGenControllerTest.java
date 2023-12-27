package ac.uk.soton.ecs.projectalloc.datagen;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.RequestEntity.post;

import ac.uk.soton.ecs.projectalloc.ErrorResponse;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
@ContextConfiguration(classes = {DataGenApplication.class})
public class DataGenControllerTest {

    @LocalServerPort
    private int serverPort;

    @Autowired
    private final TestRestTemplate restTemplate = new TestRestTemplate();

    @Test
    public void valid_success() {
        ResponseEntity<DataSet> response = restTemplate.postForEntity("http://localhost:" + serverPort + "/v1.0/datagen",
            getHealthyParams(),
            DataSet.class);

        assertThat(response.getStatusCode().is2xxSuccessful(), is(true));
    }

    private static Map<String, Object> getHealthyParams() {
        return new HashMap<>() {
            {
                put("numStudents", 100);
                put("numSupervisors", 80);
                put("perc1st", 20);
                put("perc21", 20);
                put("perc22", 20);
                put("perc3rd", 20);
                put("percLT3rd", 20);
                put("stuPercComp", 20);
                put("stuPercAi", 20);
                put("stuPercElec", 20);
                put("stuPercCS", 20);
                put("stuPercSE", 20);
                put("supPercComp", 20);
                put("supPercAi", 20);
                put("supPercElec", 20);
                put("supPercCS", 20);
                put("supPercSE", 20);
                put("minSupervisees", 5);
                put("maxSupervisees", 10);
                put("numInterests", 10);
                put("numSelectedInterests", 5);
                put("minChildren", 2);
                put("maxChildren", 5);
            }
        };
    }

    @Test
    public void zeroStudents_error() {
        Map<String, Object> params = getHealthyParams();
        params.put("numStudents", 0);

        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity("http://localhost:" + serverPort + "/v1.0/datagen",
            params,
            ErrorResponse.class);

        assertThat(response.getStatusCode().is4xxClientError(), is(true));
        assertThat(response.getBody().errorMessage(), is("Must generate at least 1 student"));
    }

    @Test
    public void missingParams_error() {
        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity("http://localhost:" + serverPort + "/v1.0/datagen",
            new HashMap<>() {{
                put("numStudents", 100);
                put("numSupervisors", 80);
            }},
            ErrorResponse.class);

        assertThat(response.getStatusCode().is4xxClientError(), is(true));
        assertThat(response.getBody().errorMessage().contains("Missing params"), is(true));
    }

}