package ac.uk.soton.ecs.projectalloc.evaluator;

import ac.uk.soton.ecs.projectalloc.Pairing;
import ac.uk.soton.ecs.projectalloc.Student;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class EvaluationContext {

    private final Map<Student, StudentPreference> studentPreferences = new HashMap<>();
    private final Set<Pairing> allocationResult = new HashSet<>();

    public void addStudentPreference(StudentPreference studentPreference) {
        studentPreferences.put(studentPreference.getStudent(), studentPreference);
    }

    public Map<Student, StudentPreference> getStudentPreferences() {
        return Collections.unmodifiableMap(studentPreferences);
    }

    public Collection<Pairing> getAllocationResult() {
        return Collections.unmodifiableCollection(allocationResult);
    }

    public void addAllocation(Pairing pairing) {
        allocationResult.add(pairing);
    }

    public Optional<Pairing> getAllocationResult(Student student) {
        return allocationResult.stream()
            .filter(alloc -> alloc.getStudent().equals(student))
            .findFirst();
    }

    public StudentPreference getStudentPreference(Student student) {
        return studentPreferences.get(student);
    }

}
