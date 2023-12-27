package ac.uk.soton.ecs.projectalloc.datagen;

import ac.uk.soton.ecs.projectalloc.ErrorResponse;
import java.lang.reflect.InvocationTargetException;
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
@RequestMapping("/v1.0/datagen")
public class DataGenController {

    @PostMapping
    public ResponseEntity executeDataGen(@RequestBody Map<String, Integer> params) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        DataSetBuilder builder = new DataSetBuilder();

        for(String key : params.keySet()) {
            builder.getClass().getDeclaredMethod(key, int.class).invoke(builder, params.get(key));
        }

        try {
            return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(builder.build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorResponse(e.getMessage()));
        }
    }

}
