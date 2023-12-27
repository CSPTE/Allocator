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

public class AllocatorSimulatedAnnealingAlgorithmClassic extends AllocatorSimulatedAnnealingAlgorithm {
    double startingTemperature = 320000.0;
    double percentageOfStudentsToShuffle = 0.1;

    StableAnnealingPerformanceCalculator stableAnnealingPerformanceCalculator = new StableAnnealingPerformanceCalculator();

    public AllocatorSimulatedAnnealingAlgorithmClassic(List<ChosenStudentData> students, List<ChosenSupervisorData> supervisors) {
        super(students, supervisors);
        this.coolingFactor = 0.9995;
        this.supervisorOverSuperviseesCountWeight = -1.5;
        this.studentTutorMatchWeight = -1.5;
    }

    public void setStartingTemperature(double startingTemperature) {
        this.startingTemperature = startingTemperature;
    }

    public void setPercentageOfStudentsToShuffle(double percentageOfStudentsToShuffle) {
        this.percentageOfStudentsToShuffle = percentageOfStudentsToShuffle;
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
        Map<ChosenStudentData, ChosenSupervisorData> currentPairing = new HashMap<>(currentBestPairing);

        double currentBestPairingScore = scorePairing(currentBestPairing);
        double currentPairingScore = currentBestPairingScore;

        int numberOfOverworkedSupervisors = calculateNumberOfOverworkedSupervisors(currentBestPairing);
        int numberOfStudentTutorPairs = calculateNumberOfStudentTutorPairings(currentBestPairing);
        stableAnnealingPerformanceCalculator.addIteration(0, currentBestPairingScore, 0, numberOfOverworkedSupervisors,numberOfStudentTutorPairs);

        for (double currentTemperature = startingTemperature; currentTemperature > 1; currentTemperature *= coolingFactor) {
            Map<ChosenStudentData, ChosenSupervisorData> pairingUnderTest = new HashMap<>(currentPairing);

            generatePairingIteration(pairingUnderTest, getNumberOfStudentsToShuffle());

            double pairingUnderTestScore = scorePairing(pairingUnderTest);

            // Decide if to swap to pairing under test for next iteration
            if (Math.random() > energyProbabilityFunction(currentPairingScore, pairingUnderTestScore, currentTemperature)) {
                currentPairing = pairingUnderTest;
                currentPairingScore = pairingUnderTestScore;
            }

            if (pairingUnderTestScore > currentBestPairingScore) {
                currentBestPairing = pairingUnderTest;
                currentBestPairingScore = pairingUnderTestScore;
            }
        }

        return returnPairings(currentBestPairing);
    }

    public int getNumberOfStudentsToShuffle() {
        return (int) ((double) students.size() * percentageOfStudentsToShuffle);
    }

    private double energyProbabilityFunction(double currentBestScore, double pairingUnderTestScore, double temperature) {
        return Math.exp((currentBestScore - pairingUnderTestScore) / temperature);
    }

    private void generatePairingIteration(Map<ChosenStudentData, ChosenSupervisorData> pairingUnderTest, int numberOfStudentsToReassign) {
        Collections.shuffle(students);

        for (int i=0; i<Math.max(1, numberOfStudentsToReassign); i++) {
            pairingUnderTest.put(students.get(i), supervisors.get(new Random().nextInt(supervisors.size())));
        }

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