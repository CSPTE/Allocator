package ac.uk.soton.ecs.projectalloc.choices;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface KeywordNodeRepository extends JpaRepository<KeywordNode, Long> {

    @Query("SELECT n FROM KeywordNode n WHERE n.parent=null")
    Optional<KeywordNode> findRootKeywordNode();

}