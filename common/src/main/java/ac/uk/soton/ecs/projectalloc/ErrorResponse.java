package ac.uk.soton.ecs.projectalloc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record ErrorResponse(String errorMessage) {

    @JsonCreator
    public ErrorResponse(@JsonProperty("errorMessage") String errorMessage) {
        this.errorMessage = errorMessage;
    }

}
