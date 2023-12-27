package ac.uk.soton.ecs.projectalloc.choices;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.arrayContainingInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ac.uk.soton.ecs.projectalloc.Node;
import ac.uk.soton.ecs.projectalloc.choices.ChoicesController.SelectionRequest;
import ac.uk.soton.ecs.projectalloc.choices.SelectionLog.SelectionLogKeywords;
import ac.uk.soton.ecs.projectalloc.datagen.TreeFactory;
import jakarta.transaction.Transactional;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.hamcrest.Matchers;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.junit.jupiter.Container;

/**
 * gdp-projectalloc - Developed by Lewes D. B. (Boomclaw). All rights reserved 2023.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@ContextConfiguration(classes = {ChoicesApplication.class})
public class ChoicesControllerTest {

    @LocalServerPort
    private int serverPort;

    @Autowired
    private KeywordNodeRepository keywordNodeRepository;
    @Autowired
    private ParticipantKeywordsRepository participantKeywordsRepository;
    @Autowired
    private SelectedKeywordNodeRepository selectedKeywordNodeRepository;
    @Autowired
    private SelectionLogRepository selectionLogRepository;

    private final TestRestTemplate restTemplate = new TestRestTemplate();

    @Container
    private static final MSSQLServerContainer<?> SQLSERVER_CONTAINER = new MSSQLServerContainer<>("mcr.microsoft.com/mssql/server:2022-latest").acceptLicense();

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.datasource.url", SQLSERVER_CONTAINER::getJdbcUrl);
        dynamicPropertyRegistry.add("spring.datasource.username", SQLSERVER_CONTAINER::getUsername);
        dynamicPropertyRegistry.add("spring.datasource.password", SQLSERVER_CONTAINER::getPassword);
    }

    static {
        SQLSERVER_CONTAINER.start();
    }

    @BeforeEach
    public void reset() {
        keywordNodeRepository.deleteAllInBatch();
        participantKeywordsRepository.deleteAllInBatch();
        selectedKeywordNodeRepository.deleteAllInBatch();
        selectionLogRepository.deleteAllInBatch();

        keywordNodeRepository.flush();
        participantKeywordsRepository.flush();
        selectedKeywordNodeRepository.flush();
        selectionLogRepository.flush();
    }

    @Test
    public void setKeywordSuperTreeToACMAndFetch_success() {
        Node root = loadACMTreeFromFile();

        ResponseEntity<KeywordNode> response = restTemplate.postForEntity("http://localhost:" + serverPort + "/v1.0/choices/keywordsSuperTree",
            root,
            KeywordNode.class);

        assertThat(response.getStatusCode().is2xxSuccessful(), is(true));
        assertThat(response.getBody().equals(root), is(true));

        ResponseEntity<KeywordNode> getResponse = restTemplate.getForEntity("http://localhost:" + serverPort + "/v1.0/choices/keywordsSuperTree",
            KeywordNode.class);

        assertThat(getResponse.getStatusCode().is2xxSuccessful(), is(true));
        assertThat(getResponse.getBody(), equalTo(response.getBody()));

        ResponseEntity<KeywordNode> repeat = restTemplate.postForEntity("http://localhost:" + serverPort + "/v1.0/choices/keywordsSuperTree",
            root,
            KeywordNode.class);

        assertThat(repeat.getStatusCode().is4xxClientError(), is(true));
    }

    @Test
    public void addKeywordToStudent_success() {
        Node root = loadACMTreeFromFile();
        KeywordNode acmRoot = uploadSuperTree(root);

        SelectionRequest selectionRequest = new SelectionRequest(UUID.randomUUID().toString(), false);
        selectionRequest.getKeywordIds().add(acmRoot.getId());

        ResponseEntity<ParticipantKeywordsSelection> response = restTemplate.postForEntity("http://localhost:" + serverPort + "/v1.0/choices/selections",
            selectionRequest,
            ParticipantKeywordsSelection.class);

        assertThat(response.getBody().getId(), is(selectionRequest.getId()));
        assertThat(response.getBody().getSelectedKeywordTree().getKeywordNodeId(), is(acmRoot.getId()));

        assertThat(selectionLogRepository.count(), is(1L));
        assertThat(selectionLogRepository.findAll(), hasItem(
            allOf(
                hasProperty("userId", is(selectionRequest.getId())),
                hasProperty("time", allOf(
                    is(Matchers.lessThan(System.currentTimeMillis())),
                    is(Matchers.greaterThan(System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(1)))
                ))
        )));

        assertTrue(selectionLogRepository.findAll().get(0).getKeywords().contains(new SelectionLogKeywords(
            acmRoot.getId(),
            acmRoot.getValue()
        )));
    }

    @Test
    public void addKeywordToStudentUsingRawJson_success() {
        Node root = loadACMTreeFromFile();
        KeywordNode acmRoot = uploadSuperTree(root);

        ResponseEntity<ParticipantKeywordsSelection> response = restTemplate.postForEntity("http://localhost:" + serverPort + "/v1.0/choices/selections",
            Map.of(
                "id", UUID.randomUUID().toString(),
                "supervisor", false,
                "keywordIds", List.of(acmRoot.getId())
            ),
            ParticipantKeywordsSelection.class);

        assertThat(response.getStatusCode().is2xxSuccessful(), is(true));
    }

    @Test
    public void addTwiceReplaces_success() {
        Node root = loadACMTreeFromFile();
        KeywordNode acmRoot = uploadSuperTree(root);

        SelectionRequest selectionRequest = new SelectionRequest(UUID.randomUUID().toString(), false);
        selectionRequest.getKeywordIds().add(acmRoot.getId());

        restTemplate.postForEntity("http://localhost:" + serverPort + "/v1.0/choices/selections",
            selectionRequest,
            ParticipantKeywordsSelection.class);

        selectionRequest.getKeywordIds().clear();
        selectionRequest.getKeywordIds().add(acmRoot.getChildren().get(0).getId());

        ResponseEntity<ParticipantKeywordsSelection> response = restTemplate.postForEntity("http://localhost:" + serverPort + "/v1.0/choices/selections",
            selectionRequest,
            ParticipantKeywordsSelection.class);

        assertThat(response.getBody().getId(), is(selectionRequest.getId()));
        assertThat(response.getBody().getSelectedKeywordTree().getKeywordNodeId(), is(acmRoot.getId()));
        assertThat(response.getBody().getSelectedKeywordTree().getChildren().size(), is(1));
        assertThat(response.getBody().getSelectedKeywordTree().getChildren().stream()
                .map(SelectedKeywordNode::getKeywordNodeId)
                .toArray(),
            is(arrayContainingInAnyOrder(
                acmRoot.getChildren().get(0).getId()
            ))
        );
    }

    @Test
    public void addingMultipleChildrenKeywords_success() {
        Node root = loadACMTreeFromFile();
        KeywordNode acmRoot = uploadSuperTree(root);

        SelectionRequest selectionRequest = new SelectionRequest(UUID.randomUUID().toString(), false);
        selectionRequest.getKeywordIds().add(acmRoot.getChildren().get(0).getId());
        selectionRequest.getKeywordIds().add(acmRoot.getChildren().get(1).getId());

        ResponseEntity<ParticipantKeywordsSelection> response = restTemplate.postForEntity("http://localhost:" + serverPort + "/v1.0/choices/selections",
            selectionRequest,
            ParticipantKeywordsSelection.class);

        ParticipantKeywordsSelection respondedSelection = response.getBody();

        assertThat(respondedSelection.getId(), is(selectionRequest.getId()));
        assertThat(respondedSelection.getSelectedKeywordTree().getKeywordNodeId(), is(acmRoot.getId()));
        assertThat(respondedSelection.getSelectedKeywordTree().getChildren().stream()
            .map(SelectedKeywordNode::getKeywordNodeId)
            .toArray(),
            is(arrayContainingInAnyOrder(
                selectionRequest.getKeywordIds().toArray()
            ))
        );
    }

    private Node loadACMTreeFromFile() {
        return new TreeFactory().importTree(new File(this.getClass().getResource("/acm.json").getFile()).getParentFile().getAbsolutePath(), "acm");
    }

    @Nullable
    private KeywordNode uploadSuperTree(Node root) {
        return restTemplate.postForEntity("http://localhost:" + serverPort + "/v1.0/choices/keywordsSuperTree",
            root,
            KeywordNode.class).getBody();
    }

}