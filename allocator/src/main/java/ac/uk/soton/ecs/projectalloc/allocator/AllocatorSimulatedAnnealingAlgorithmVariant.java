package ac.uk.soton.ecs.projectalloc.allocator;

import ac.uk.soton.ecs.projectalloc.ChosenStudentData;
import ac.uk.soton.ecs.projectalloc.ChosenSupervisorData;
import ac.uk.soton.ecs.projectalloc.Pairing;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class AllocatorSimulatedAnnealingAlgorithmVariant extends AllocatorSimulatedAnnealingAlgorithm {

    int maxNumberOfIterations = 20000;
    int coolingAlgorithm = 2;
    StableAnnealingPerformanceCalculator stableAnnealingPerformanceCalculator = new StableAnnealingPerformanceCalculator();

    public AllocatorSimulatedAnnealingAlgorithmVariant(List<ChosenStudentData> students, List<ChosenSupervisorData> supervisors) {
        super(students, supervisors);
        this.supervisorOverSuperviseesCountWeight = -2;
        this.studentTutorMatchWeight = -0.5;
    }

    public void setMaxNumberOfIterations(int maxNumberOfIterations) {
        this.maxNumberOfIterations = maxNumberOfIterations;
    }

    public void setCoolingAlgorithm(int coolingAlgorithm) {
        this.coolingAlgorithm = coolingAlgorithm;
    }

    /**
     * Generates Pairings between the Students and Supervisors using a Stable Annealing Genetics Style Algorithm
     * @return - A list of Parings between each student and supervisor
     */
    public List<Pairing> generatePairings() {
        if (supervisors.isEmpty()) {
            System.out.println("No supervisors"); // to be reported to front end
            throw new IllegalArgumentException("No Supervisors in list");
        }

        if (students.isEmpty()) {
            System.out.println("No students"); // to be reported to front end
            throw new IllegalArgumentException("No students in list");
        }

        int totalSupervisorCapacity = supervisors.stream()
                .mapToInt(ChosenSupervisorData::getMaxSupervisees)
                .sum();

        if (totalSupervisorCapacity < students.size()) {
            System.out.println("Insufficient supervisor capacity - suboptimal pairing will be produced"); // to be reported to front end
        }

        Map<ChosenStudentData, ChosenSupervisorData> currentBestPairing = generateInitialPairing();
        double currentBestPairingScore = scorePairing(currentBestPairing);

        int numberOfOverworkedSupervisors = calculateNumberOfOverworkedSupervisors(currentBestPairing);
        int numberOfStudentTutorPairs = calculateNumberOfStudentTutorPairings(currentBestPairing);
        stableAnnealingPerformanceCalculator.addIteration(0, currentBestPairingScore, 0, numberOfOverworkedSupervisors,numberOfStudentTutorPairs);

        for (int currentIteration = 1; currentIteration < maxNumberOfIterations; currentIteration++) {
            Map<ChosenStudentData, ChosenSupervisorData> potentialPairing = generatePairingIteration(currentBestPairing, currentIteration);
            double potentialPairingScore = scorePairing(potentialPairing);

            if (potentialPairingScore > currentBestPairingScore) {
                currentBestPairing = potentialPairing;
                currentBestPairingScore = potentialPairingScore;

                numberOfOverworkedSupervisors = calculateNumberOfOverworkedSupervisors(currentBestPairing);
                numberOfStudentTutorPairs = calculateNumberOfStudentTutorPairings(currentBestPairing);
                stableAnnealingPerformanceCalculator.addIteration(currentIteration, currentBestPairingScore, calculateNumberOfStudentsToShuffle(currentIteration), numberOfOverworkedSupervisors, numberOfStudentTutorPairs);
            }
        }

        return returnPairings(currentBestPairing);
    }

    private Map<ChosenStudentData, ChosenSupervisorData> generatePairingIteration(Map<ChosenStudentData, ChosenSupervisorData> currentBestIteration, int iterationCount) {
        Map<ChosenStudentData, ChosenSupervisorData> temporaryPairing = new HashMap<>(currentBestIteration);

        int numberOfStudentsToReassign = calculateNumberOfStudentsToShuffle(iterationCount);

        Collections.shuffle(students);

        for (int i=0; i<numberOfStudentsToReassign; i++) {
            temporaryPairing.put(students.get(i), supervisors.get(new Random().nextInt(supervisors.size())));
        }

        return temporaryPairing;
    }

    private int calculateNumberOfStudentsToShuffle(int iterationCount) {
        return switch (coolingAlgorithm) {
            case 0 ->
                    Math.max(1, (int) (students.size() * (1.0 - Math.pow((iterationCount / (coolingFactor * maxNumberOfIterations)), (double) 1 / 4))));
            case 1 -> Math.max(1, (int) ((students.size() * (1.0 - ((double) iterationCount / maxNumberOfIterations)))));
            default -> 1;
        };

    }

    private static class StableAnnealingPerformanceCalculator {
        List<String> iterations = new ArrayList<>();

        public void addIteration(int iterationNumber, double score, int numberOfShuffledStudents, int numberOfOverworkedSupervisors, int numberOfStudentTutorPairs) {
            iterations.add(String.format("%d, %d, %f, %d, %d", iterationNumber, numberOfShuffledStudents, score, numberOfOverworkedSupervisors, numberOfStudentTutorPairs));
        }

        public void printIterations() {
            iterations.forEach(System.out::println);
        }

        public void printLastIterations() {
            System.out.println(iterations.get(iterations.size()-1));
        }
    }
}