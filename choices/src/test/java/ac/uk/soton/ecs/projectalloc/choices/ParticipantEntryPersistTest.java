package ac.uk.soton.ecs.projectalloc.choices;

import static org.hamcrest.MatcherAssert.assertThat;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.junit.jupiter.Container;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@ContextConfiguration(classes = {ChoicesApplication.class})
public class ParticipantEntryPersistTest {

    @Autowired
    private SelectedKeywordNodeRepository selectedKeywordNodeRepository;

    @Autowired
    private KeywordNodeRepository keywordNodeRepository;

    @Autowired
    private ParticipantKeywordsRepository repository;

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

    @Test
    @Transactional
    public void loadAndSaveSimpleParticipant_success() {
        String studentId = UUID.randomUUID().toString();

        ParticipantKeywordsSelection selection = new ParticipantKeywordsSelection();
        selection.setId(studentId);

        KeywordNode parent = new KeywordNode();
        KeywordNode node = new KeywordNode();

        node.setParent(parent);
        parent.setChildren(List.of(node));

        keywordNodeRepository.save(parent);

        SelectedKeywordNode selectedKeywordTree = new SelectedKeywordNode();
        selectedKeywordTree.setKeywordNodeId(parent.getId());

        SelectedKeywordNode child = new SelectedKeywordNode();
        child.setKeywordNodeId(node.getId());

        selectedKeywordTree.addChild(child);
        selectedKeywordNodeRepository.save(selectedKeywordTree);

        selection.setSelectedKeywordTree(selectedKeywordTree);
        repository.save(selection);

        ParticipantKeywordsSelection respondedSelection = repository.getReferenceById(studentId);
        assertThat(respondedSelection.getId(), Matchers.is(studentId));
        assertThat(respondedSelection.getSelectedKeywordTree(), Matchers.is(Matchers.equalTo(selectedKeywordTree)));
    }

}
