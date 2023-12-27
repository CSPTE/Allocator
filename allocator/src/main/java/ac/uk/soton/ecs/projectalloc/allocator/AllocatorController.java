package ac.uk.soton.ecs.projectalloc.allocator;

import ac.uk.soton.ecs.projectalloc.ChosenStudentData;
import ac.uk.soton.ecs.projectalloc.ChosenSupervisorData;
import ac.uk.soton.ecs.projectalloc.ErrorResponse;
import ac.uk.soton.ecs.projectalloc.Pairing;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * gdp-projectalloc - Developed by Lewes D. B. (Boomclaw). All rights reserved 2023.
 */
@RestController
@RequestMapping("/v1.0/allocator")
public class AllocatorController {

    @PostMapping
    public ResponseEntity execute(@RequestBody AllocatorRequest context) {
        if(context.algorithm == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorResponse("Unknown algorithm"));
        }

        try {
            return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new AllocatorResponse(getAlgorithm(context).generatePairings(), context.algorithm));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorResponse(e.getMessage()));
        }
    }

    private AllocatorAlgorithm getAlgorithm(AllocatorRequest context) {
        if (context.algorithm == AlgorithmType.STABLE) {
            return new AllocatorStableMarriageAlgorithm(context.students(), context.supervisors());
        } else if (context.algorithm == AlgorithmType.LINEAR) {
            return new AllocatorLinearAlgorithm(context.students(), context.supervisors());
        } else if (context.algorithm == AlgorithmType.ANNEALING_CLASSIC) {
            return new AllocatorSimulatedAnnealingAlgorithmClassic(context.students(), context.supervisors());
        } else if (context.algorithm == AlgorithmType.ANNEALING_VARIANT) {
            return new AllocatorSimulatedAnnealingAlgorithmVariant(context.students(), context.supervisors());
        }

        return null;
    }

    public record AllocatorResponse(List<Pairing> pairings, AlgorithmType algorithm) {

        @JsonCreator
        public AllocatorResponse(@JsonProperty("pairing") List<Pairing> pairings,
            @JsonProperty("algorithm") AlgorithmType algorithm) {
            this.pairings = pairings;
            this.algorithm = algorithm;
        }

    }

    public record AllocatorRequest(List<ChosenStudentData> students, List<ChosenSupervisorData> supervisors, AlgorithmType algorithm) {

        @JsonCreator
        public AllocatorRequest(@JsonProperty("students") List<ChosenStudentData> students,
                @JsonProperty("supervisors") List<ChosenSupervisorData> supervisors,
                @JsonProperty("algorithm") AlgorithmType algorithm) {
                this.students = students;
                this.supervisors = supervisors;
                this.algorithm = algorithm;
        }

    }

    public enum AlgorithmType {
        STABLE,
        LINEAR,
        ANNEALING_CLASSIC,
        ANNEALING_VARIANT
    }

}
