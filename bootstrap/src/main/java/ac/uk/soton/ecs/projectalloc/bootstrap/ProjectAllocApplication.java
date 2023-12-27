package ac.uk.soton.ecs.projectalloc.bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "ac.uk.soton.ecs.projectalloc")
public class ProjectAllocApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProjectAllocApplication.class, args);
    }

}
