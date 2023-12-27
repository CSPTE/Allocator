package ac.uk.soton.ecs.projectalloc.choices;

import ac.uk.soton.ecs.projectalloc.Node;
import ac.uk.soton.ecs.projectalloc.choices.SelectionLog.SelectionLogKeywords;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.transaction.Transactional;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1.0/choices")
public class ChoicesController {

    @Autowired
    private KeywordNodeRepository repository;
    @Autowired
    private ParticipantKeywordsRepository participantKeywordsRepository;
    @Autowired
    private SelectedKeywordNodeRepository selectedKeywordNodeRepository;
    @Autowired
    private SelectionLogRepository selectionLogRepository;

    @PostMapping("keywordsSuperTree")
    @Transactional
    public ResponseEntity setKeywordsSuperTree(@RequestBody Node root) {
        if(repository.findRootKeywordNode().isPresent()) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .build();
        }

        KeywordNode keywordNode = convertToKeywordNode(root, null);

        repository.save(keywordNode);

        return ResponseEntity.status(HttpStatus.OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body(keywordNode);
    }

    @GetMapping("keywordsSuperTree")
    public ResponseEntity<KeywordNode> getKeywordsSuperTree() {
        Optional<KeywordNode> root = repository.findRootKeywordNode();

        return root.map(keywordNode -> ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(keywordNode))
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    public KeywordNode convertToKeywordNode(Node node, KeywordNode parent) {
        KeywordNode keywordNode = new KeywordNode();

        keywordNode.setParent(parent);
        keywordNode.setValue(node.getInterest());
        keywordNode.setChildren(node.getChildren().stream()
            .map(child -> convertToKeywordNode(child, keywordNode))
            .collect(Collectors.toList()));

        return keywordNode;
    }

    @PostMapping("selections")
    @Transactional
    public ResponseEntity addSelection(@RequestBody SelectionRequest request) {
        participantKeywordsRepository.deleteById(request.getId());

        Map<Long, SelectedKeywordNode> idsInTree = new HashMap<>();

        SelectedKeywordNode rootKeyword = null;

        for(long keywordId : request.getKeywordIds()) {
            KeywordNode keywordNode = repository.getReferenceById(keywordId);

            SelectedKeywordNode selectedKeywordNode = new SelectedKeywordNode();
            selectedKeywordNode.setKeywordNodeId(keywordId);

            SelectedKeywordNode prior;

            while(keywordNode.getParent() != null) {
                prior = selectedKeywordNode;
                keywordNode = keywordNode.getParent();

                selectedKeywordNode = idsInTree.getOrDefault(keywordNode.getId(), new SelectedKeywordNode());
                idsInTree.put(keywordNode.getId(), selectedKeywordNode);
                selectedKeywordNode.setKeywordNodeId(keywordNode.getId());

                selectedKeywordNode.addChild(prior);
            }

            rootKeyword = selectedKeywordNode;
        }

        selectedKeywordNodeRepository.save(rootKeyword);

        ParticipantKeywordsSelection selection = new ParticipantKeywordsSelection();
        selection.setId(request.getId());
        selection.setSupervisor(request.isSupervisor());
        selection.setSelectedKeywordTree(rootKeyword);

        participantKeywordsRepository.save(selection);

        SelectionLog log = new SelectionLog(System.currentTimeMillis(), request.getId(), request.isSupervisor());
        log.getKeywords().addAll(request.getKeywordIds().stream().map(
            keywordId -> {
                KeywordNode node = repository.getReferenceById(keywordId);

                return new SelectionLogKeywords(node.getId(), node.getValue());
            }
        ).collect(Collectors.toSet()));
        selectionLogRepository.save(log);

        return ResponseEntity.status(HttpStatus.OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body(selection);
    }

    public static class SelectionRequest {

        private final String id;
        private final boolean supervisor;
        private final Set<Long> keywordIds = new HashSet<>();

        @JsonCreator
        public SelectionRequest(@JsonProperty("id") String id,
            @JsonProperty("supervisor") boolean supervisor) {
            this.id = id;
            this.supervisor = supervisor;
        }

        public String getId() {
            return id;
        }

        public boolean isSupervisor() {
            return supervisor;
        }

        public Set<Long> getKeywordIds() {
            return keywordIds;
        }
    }


}
