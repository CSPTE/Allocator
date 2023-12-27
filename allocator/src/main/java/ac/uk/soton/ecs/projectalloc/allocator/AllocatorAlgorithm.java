package ac.uk.soton.ecs.projectalloc.allocator;

import ac.uk.soton.ecs.projectalloc.*;

import java.util.*;
import java.util.stream.Collectors;

abstract class AllocatorAlgorithm {
    protected final List<ChosenStudentData> students;
    protected final List<ChosenSupervisorData> supervisors;
    protected Set<String> allSupervisorInterests;

    public AllocatorAlgorithm(List<ChosenStudentData> students, List<ChosenSupervisorData> supervisors) {
        this.students = students;
        this.supervisors = supervisors;

        allSupervisorInterests = generateSetOfAllSupervisorsInterests();
    }

    public HashSet<String> generateSetOfAllSupervisorsInterests() {
        HashSet<String> allSupervisorInterests = new HashSet<>();
        for(ChosenSupervisorData supervisorData : supervisors) {
            List<String> supervisorInterests = new ArrayList();
            visitNodeToGetFlatList(supervisorData.getInterestTree(), supervisorInterests);
            allSupervisorInterests.addAll(supervisorInterests);
        }
        return  allSupervisorInterests;
    }

    public abstract List<Pairing> generatePairings() throws NoViableSolutionException;

    public double calculateTotalInterestScore(Map<ChosenStudentData, ChosenSupervisorData> pairingToScore) {
        return students.stream()
                .mapToDouble(student -> calculateInterestScoreForPairing(student, pairingToScore.get(student)))
                .sum();
    }

    public int calculateNumberOfCommonInterests (ChosenStudentData student, ChosenSupervisorData supervisor) {
        List<String> supervisorInterestList = getInterestListAsStrings(supervisor);
        List<String> studentInterestList = getInterestListAsStrings(student);

        return studentInterestList.stream()
                .filter(supervisorInterestList::contains)
                .toList()
                .size();

    }

    public double calculateInterestScoreForPairing(ChosenStudentData student, ChosenSupervisorData supervisor) {
        int numberOfInterestsStudentHasWithAllSupervisors = (int) getInterestListAsStrings(student).stream()
                .filter(interest -> allSupervisorInterests.contains(interest))
                .count();

        int numberOfInterestsInCommon = calculateNumberOfCommonInterests(student, supervisor);

        return (double) numberOfInterestsInCommon / numberOfInterestsStudentHasWithAllSupervisors;
    }

    private void visitNodeToGetFlatList(Node currentNode,List<String> interests) {
        currentNode.getChildren().forEach(node -> visitNodeToGetFlatList(node, interests));

        interests.add(currentNode.getInterest());
    }

    public List<String> getInterestListAsStrings(ChosenParticipantData participantData) {
        List<String> interestList = new ArrayList<>();
        visitNodeToGetFlatList(participantData.getInterestTree(), interestList);

        return interestList;
    }

    private class InterestScorePair {
        private final String interest;
        private final int score;

        public InterestScorePair(String interest, int score){
            this.interest = interest;
            this.score = score;
        }

        public String getInterest(){
            return interest;
        }

        public int getScore() {
            return score;
        }
    }
}

