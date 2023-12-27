package ac.uk.soton.ecs.projectalloc.choices;

import ac.uk.soton.ecs.projectalloc.choices.SelectionLog.SelectionLogKeywords;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SelectionLogKeywordsRepository extends JpaRepository<SelectionLogKeywords, Long> {


}