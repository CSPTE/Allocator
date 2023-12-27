package ac.uk.soton.ecs.projectalloc.allocator;

import ac.uk.soton.ecs.projectalloc.ChosenStudentData;
import ac.uk.soton.ecs.projectalloc.ChosenSupervisorData;
import ac.uk.soton.ecs.projectalloc.Pairing;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class AllocatorStableMarriageAlgorithm extends AllocatorAlgorithm {
    private final Map<ChosenStudentData, List<ChosenSupervisorData>> studentPreferenceList = new HashMap<>();
    private final Map<ChosenSupervisorData, List<ChosenStudentData>> supervisorPreferenceList = new HashMap<>();

    public AllocatorStableMarriageAlgorithm(List<ChosenStudentData> students, List<ChosenSupervisorData> supervisors) {
        super(students, supervisors);
    }

    private static class TemporaryPairing {

        private final ChosenStudentData studentData;
        private final ChosenSupervisorData supervisorData;

        private TemporaryPairing(ChosenStudentData studentData, ChosenSupervisorData supervisorData) {
            this.studentData = studentData;
            this.supervisorData = supervisorData;
        }

        public ChosenStudentData getStudentData() {
            return studentData;
        }

        public ChosenSupervisorData getSupervisorData() {
            return supervisorData;
        }

        public Pairing toPairing() {
            return new Pairing(studentData.getStudent(), supervisorData.getSupervisor());
        }

    }

    public List<Pairing> generatePairings() throws NoViableSolutionException{
        if (!checkIfAllocationPossible()){
            throw new NoViableSolutionException("More students than slots available");
        }
        Set<ChosenStudentData> assignedStudents = new HashSet<>();
        List<TemporaryPairing> output = new ArrayList<>();
        createPreferenceLists();

        while (assignedStudents.size() != students.size()){
            for (ChosenStudentData student : students){
                if (!assignedStudents.contains(student)){
                    for (ChosenSupervisorData supervisor : studentPreferenceList.get(student)){
                        int nrOfStudentsAssigned = 0;
                        ChosenStudentData leastLikedStudent = supervisorPreferenceList.get(supervisor).get(0);
                        int leastLikedStudentIndexInPairing = -1;

                        for (TemporaryPairing existingPairs : output){
                            if (supervisor.equals(existingPairs.getSupervisorData())){
                                nrOfStudentsAssigned++;

                                if (supervisorPreferenceList.get(supervisor).indexOf(leastLikedStudent) < supervisorPreferenceList.get(supervisor).indexOf(existingPairs.getStudentData())){
                                    leastLikedStudent = existingPairs.getStudentData();
                                    leastLikedStudentIndexInPairing = output.indexOf(existingPairs);
                                }
                            }
                        }

                        if (supervisor.getMaxSupervisees() > nrOfStudentsAssigned){
                            output.add(new TemporaryPairing(student, supervisor));
                            assignedStudents.add(student);
                            break;
                        } else if (supervisorPreferenceList.get(supervisor).indexOf(leastLikedStudent) > supervisorPreferenceList.get(supervisor).indexOf(student)){
                            output.add(new TemporaryPairing(student, supervisor));
                            output.remove(leastLikedStudentIndexInPairing);
                            assignedStudents.add(student);
                            assignedStudents.remove(leastLikedStudent);
                            break;
                        }  
                    }
                }
            }
        }

        return output.stream()
            .map(TemporaryPairing::toPairing)
            .collect(Collectors.toList());
    }

    private boolean checkIfAllocationPossible(){
        int superviseeSlots = 0;
        int studentsToAllocate = students.size();

        for (ChosenSupervisorData sup : supervisors){
            superviseeSlots = superviseeSlots + sup.getMaxSupervisees();
        }

        return superviseeSlots > studentsToAllocate;
    }

    public void createPreferenceLists() {
        students.forEach(this::calculateStudentPreferences);
        supervisors.forEach(this::calculateSupervisorPreferences);
    }

    private void calculateStudentPreferences(ChosenStudentData student) {
        Map<ChosenSupervisorData, Double> supervisorScore = new HashMap<>();
        for (ChosenSupervisorData sup : supervisors){
            double interestScoreForPairing = calculateInterestScoreForPairing(student, sup);
            if (student.getTutor() != null){
                if (!student.getTutor().equals(sup.getSupervisor())){
                    supervisorScore.put(sup, interestScoreForPairing);
                }
            } else {
                supervisorScore.put(sup, interestScoreForPairing);
            }            
        }

        List<Map.Entry<ChosenSupervisorData, Double>> sortedSupervisors = new ArrayList<>(supervisorScore.entrySet());
        sortedSupervisors.sort((entry1, entry2) -> {
            int scoreCompare = entry2.getValue().compareTo(entry1.getValue());
            if(scoreCompare != 0) {
                return scoreCompare;
            } else {
                return entry1.getKey().getUsername().compareTo(entry2.getKey().getUsername());
            }
        });
        ArrayList<ChosenSupervisorData> sortedSupervisorsList = new ArrayList<>();
        for (Map.Entry<ChosenSupervisorData, Double> entry : sortedSupervisors) {
            sortedSupervisorsList.add(entry.getKey());
        }

        studentPreferenceList.put(student, sortedSupervisorsList);
    }

    private void calculateSupervisorPreferences(ChosenSupervisorData supervisor) {
        Map<ChosenStudentData, Double> studentScore = new HashMap<>();
        for (ChosenStudentData stu : students){
            Double interestScoreForPairing = calculateInterestScoreForPairing(stu, supervisor);
            studentScore.put(stu, interestScoreForPairing);
        }

        List<Map.Entry<ChosenStudentData, Double>> sortedStudents = new ArrayList<>(studentScore.entrySet());
        sortedStudents.sort((entry1, entry2) -> {
            int scoreCompare = entry2.getValue().compareTo(entry1.getValue());
            int gradeCompare = Integer.compare(entry2.getKey().getGrade(), entry1.getKey().getGrade());
            if(scoreCompare != 0) {
                return scoreCompare;
            } else if (gradeCompare != 0) {
                return gradeCompare;
            } else {
                return entry1.getKey().getUsername().compareTo(entry2.getKey().getUsername());
            }
        });
        ArrayList<ChosenStudentData> sortedStudentsList = new ArrayList<>();
        for (Map.Entry<ChosenStudentData, Double> entry : sortedStudents) {
            sortedStudentsList.add(entry.getKey());
        }

        supervisorPreferenceList.put(supervisor, sortedStudentsList);
    }

    public Map<ChosenStudentData, List<ChosenSupervisorData>> getStudentPreferenceList(){
        return studentPreferenceList;
    }

    public Map<ChosenSupervisorData, List<ChosenStudentData>> getSupervisorPreferenceList(){
        return supervisorPreferenceList;
    }
}
