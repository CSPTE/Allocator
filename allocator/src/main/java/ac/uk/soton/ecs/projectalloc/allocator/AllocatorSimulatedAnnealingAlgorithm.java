package ac.uk.soton.ecs.projectalloc.allocator;

import ac.uk.soton.ecs.projectalloc.*;

import java.util.*;

abstract class AllocatorSimulatedAnnealingAlgorithm extends AllocatorAlgorithm {
    // Weightings for stable annealing algorithm
    protected double supervisorOverSuperviseesCountWeight = -1;
    protected double studentTutorMatchWeight = -1;
    protected double coolingFactor = 0.9999;

    public abstract List<Pairing> generatePairings();

    public AllocatorSimulatedAnnealingAlgorithm(List<ChosenStudentData> students, List<ChosenSupervisorData> supervisors) {
        super(students, supervisors);
    }

    public void setCoolingRate(double coolingFactor) {
        this.coolingFactor = coolingFactor;
    }

    public void setSupervisorOverSuperviseesCountWeight(double supervisorOverSuperviseesCountWeight) {
        this.supervisorOverSuperviseesCountWeight = supervisorOverSuperviseesCountWeight;
    }

    public void setStudentTutorMatchWeight(double studentTutorMatchWeight) {
        this.studentTutorMatchWeight = studentTutorMatchWeight;
    }

    protected Map<ChosenStudentData, ChosenSupervisorData> generateInitialPairing() {
        Map<ChosenStudentData, ChosenSupervisorData> temporaryPairing = new HashMap<>();

        students.forEach(student -> temporaryPairing.put(student, supervisors.get(new Random().nextInt(supervisors.size()))));

        return temporaryPairing;
    }

    protected int calculateNumberOfOverworkedSupervisors(Map<ChosenStudentData, ChosenSupervisorData> pairingToScore) {
        Map<ChosenSupervisorData, Integer> numberOfStudentsPerSupervisor = new HashMap<>();

        supervisors.forEach(supervisor -> numberOfStudentsPerSupervisor.put(supervisor, 0));
        students.forEach(student ->
                numberOfStudentsPerSupervisor.put(pairingToScore.get(student), numberOfStudentsPerSupervisor.get(pairingToScore.get(student)) + 1));

        return (int) supervisors.stream()
                .filter(supervisor -> numberOfStudentsPerSupervisor.get(supervisor) > supervisor.getMaxSupervisees())
                .count();

    }

    protected int calculateNumberOfStudentTutorPairings(Map<ChosenStudentData, ChosenSupervisorData> pairingToScore) {
        return (int) students.stream()
                .filter(student -> student.getTutor().equals(pairingToScore.get(student).getSupervisor()))
                .count();
    }

    protected double scorePairing(Map<ChosenStudentData, ChosenSupervisorData> pairingToScore) {
        double currentScore = 0;

        currentScore += calculateTotalInterestScore(pairingToScore);

        currentScore += supervisorOverSuperviseesCountWeight * calculateNumberOfOverworkedSupervisors(pairingToScore);

        currentScore += studentTutorMatchWeight * calculateNumberOfStudentTutorPairings(pairingToScore);

        return currentScore;
    }

    protected List<Pairing> returnPairings(Map<ChosenStudentData, ChosenSupervisorData> pairingsToConvert){
        List<Pairing> pairings = new ArrayList<>();

        students.forEach(student -> pairings.add(new Pairing(student.getStudent(), pairingsToConvert.get(student).getSupervisor())));

        return pairings;
    }

}
