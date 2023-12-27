package ac.uk.soton.ecs.projectalloc.choices;

import com.fasterxml.jackson.databind.util.StdConverter;

public class KeywordNodeSanitizer extends StdConverter<KeywordNode, KeywordNode> {

    @Override
    public KeywordNode convert(KeywordNode keywordNode) {
        keywordNode.getChildren().forEach(child -> child.setParent(keywordNode));

        return keywordNode;
    }

}
