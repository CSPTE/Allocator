package ac.uk.soton.ecs.projectalloc.allocator;

import ac.uk.soton.ecs.projectalloc.ChosenStudentData;
import ac.uk.soton.ecs.projectalloc.ChosenSupervisorData;
import ac.uk.soton.ecs.projectalloc.Pairing;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.ojalgo.optimisation.Expression;
import org.ojalgo.optimisation.ExpressionsBasedModel;
import org.ojalgo.optimisation.Optimisation;
import org.ojalgo.optimisation.Variable;

public class AllocatorLinearAlgorithm extends AllocatorAlgorithm {
    Map<String, ChosenStudentData> studentUsernameDataMap = new HashMap<>();
    Map<String, ChosenSupervisorData> supervisorUsernameDataMap = new HashMap<>();


    /**
     *
     * @param students - List of Students
     * @param supervisors - List of Supervisors
     */
    public AllocatorLinearAlgorithm(List<ChosenStudentData> students, List<ChosenSupervisorData> supervisors) {
        super(students, supervisors);

        students.forEach(student -> studentUsernameDataMap.put(student.getUsername(), student));
        supervisors.forEach(supervisor -> supervisorUsernameDataMap.put(supervisor.getUsername(), supervisor));
    }

    /**
     * Generates Pairings between the Students and Supervisors using a Linear Programmer
     * @return - A list of Parings between each student and supervisor
     */
    public List<Pairing> generatePairings() {
        if (supervisors.isEmpty()) {
            throw new IllegalArgumentException("No Supervisors in list");
        }

        if (students.isEmpty()) {
            throw new IllegalArgumentException("No students in list");
        }

        int totalSupervisorCapacity = supervisors.stream()
                .mapToInt(ChosenSupervisorData::getMaxSupervisees)
                .sum();

        if (totalSupervisorCapacity < students.size()) {
            System.out.println("Insufficient supervisor capacity - suboptimal pairing will be produced"); // to be reported to front end
        }

        Map<ChosenStudentData, List<Variable>> studentDecisionVariables = new HashMap<>();
        for (ChosenStudentData student : students) {
            studentDecisionVariables.put(student, new ArrayList<>());
        }

        Map<ChosenSupervisorData, List<Variable>> supervisorDecisionVariables = new HashMap<>();
        for (ChosenSupervisorData supervisor: supervisors) {
            supervisorDecisionVariables.put(supervisor, new ArrayList<>());
        }

        ExpressionsBasedModel model = new ExpressionsBasedModel();
        model.options.experimental = true; // Needed to avoid running out of RAM

        // Create a decision variable for each possible student supervisor pair
        for (ChosenStudentData student : students) {
            for (ChosenSupervisorData supervisor: supervisors) {
                // Block student supervisor pairs
                if (shouldBlockPair(student, supervisor)) {
                    continue;
                }

                Variable possibleParing =
                        model.newVariable(String.format("%s:%s", student.getUsername(), supervisor.getUsername())).binary();

                possibleParing.weight(generateInterestScore(student, supervisor));

                // Enforce variable as integer for whole solutions only
                possibleParing.integer(true);

                studentDecisionVariables.get(student).add(possibleParing);
                supervisorDecisionVariables.get(supervisor).add(possibleParing);
            }
        }

        // Create constraint on number of project student is assigned
        for (ChosenStudentData student : students) {
            Expression oneSupervisorPerStudent =
                    model.newExpression(String.format("OneProjectFor%s", student.getUsername()));
            oneSupervisorPerStudent.upper(1).lower(1);

            for (Variable possibleParing : studentDecisionVariables.get(student)) {
                oneSupervisorPerStudent.set(possibleParing, 1);
            }
        }


        // Create constraints on supervisor supervise numbers
        for (ChosenSupervisorData supervisor : supervisors) {
            Expression supervisorMaxSupervises  = model.newExpression(String.format("MaxSupervisesFor%s", supervisor.getUsername()));
            supervisorMaxSupervises.lower(0).upper(supervisor.getMaxSupervisees());

            for (Variable possibleParing : supervisorDecisionVariables.get(supervisor)) {
                supervisorMaxSupervises.set(possibleParing, 1);
            }
        }

        // Solve the problem maximising the number the score of interest matches
        Optimisation.Result result = model.maximise();
        System.out.println(result.getValue());

        return returnPairings(studentDecisionVariables);
    }

    /*
        Generate a score between the student and supervisor for pairing quality - looks for simple matching of interests
        Ideally will have a few different was to generate the interest score for optimal testing
     */
    private double generateInterestScore(ChosenStudentData student, ChosenSupervisorData supervisor) {
        return calculateInterestScoreForPairing(student, supervisor);
    }

    /*
        Method to take the results from the linear solver and convert them in to the pairings
    */
    private List<Pairing> returnPairings(Map<ChosenStudentData, List<Variable>> studentDecisionVariables){
        List<Pairing> pairings = new ArrayList<>();

        for (ChosenStudentData student : students) {
            for (Variable possiblePairing: studentDecisionVariables.get(student)) {
                if (possiblePairing.getValue().intValue() == 1) {
                    String[] names = possiblePairing.getName().split(":");

                    pairings.add(new Pairing(studentUsernameDataMap.get(names[0]).getStudent(), supervisorUsernameDataMap.get(names[1]).getSupervisor()));
                }
            }
        }


        return pairings;
    }

    private boolean shouldBlockPair(ChosenStudentData student, ChosenSupervisorData supervisor){
        return student.getTutor().equals(supervisor.getSupervisor());
    }

}
