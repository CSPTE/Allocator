package ac.uk.soton.ecs.projectalloc.choices;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SelectedKeywordNodeRepository extends JpaRepository<SelectedKeywordNode, String> {


}