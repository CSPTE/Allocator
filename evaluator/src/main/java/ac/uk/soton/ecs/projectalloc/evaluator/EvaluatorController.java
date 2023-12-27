package ac.uk.soton.ecs.projectalloc.evaluator;

import ac.uk.soton.ecs.projectalloc.ErrorResponse;
import ac.uk.soton.ecs.projectalloc.evaluator.EvaluatorController.EvaluatorResponse.EvaluatorMetricResponse;
import ac.uk.soton.ecs.projectalloc.evaluator.metric.EMTopN;
import ac.uk.soton.ecs.projectalloc.evaluator.metric.KeywordSimilarity;
import ac.uk.soton.ecs.projectalloc.evaluator.metric.Stability;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
@RequestMapping("/v1.0/evaluator")
public class EvaluatorController {

    @PostMapping
    public ResponseEntity executeEvaluator(@RequestBody EvaluationContext context) {
        EvaluatorResponse response = new EvaluatorResponse();

        if(context == null ||
            context.getStudentPreferences().isEmpty() ||
            context.getAllocationResult().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorResponse("Student preferences or allocation is empty"));
        }

        List.of(
            new EMTopN(1, context),
            new EMTopN(3, context),
            new EMTopN(5, context),
            new EMTopN(10, context),
            new Stability(context),
            new KeywordSimilarity(context)
        ).forEach(metric -> {
            response.getMetrics().put(metric.getName().toLowerCase(Locale.ROOT), new EvaluatorMetricResponse(
                metric.getPositivePercent(),
                metric.getPositiveCount()
            ));
        });

        return ResponseEntity.status(HttpStatus.OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body(response);
    }

    public static class EvaluatorResponse {

        private final Map<String, EvaluatorMetricResponse> metrics = new HashMap<>();

        public Map<String, EvaluatorMetricResponse> getMetrics() {
            return metrics;
        }

        public record EvaluatorMetricResponse(double percentage, int count) {

            @JsonCreator
            public EvaluatorMetricResponse(@JsonProperty("percentage") double percentage,
                @JsonProperty("count") int count) {
                this.percentage = percentage;
                this.count = count;
            }

        }

    }

}
