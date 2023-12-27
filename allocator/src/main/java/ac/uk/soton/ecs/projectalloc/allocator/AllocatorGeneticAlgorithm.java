package ac.uk.soton.ecs.projectalloc.allocator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import ac.uk.soton.ecs.projectalloc.ChosenStudentData;
import ac.uk.soton.ecs.projectalloc.ChosenSupervisorData;
import ac.uk.soton.ecs.projectalloc.Pairing;

public class AllocatorGeneticAlgorithm extends AllocatorAlgorithm {
    private Map<ChosenStudentData, Map<ChosenSupervisorData, Double>> studentPreferenceMap = new HashMap<>();
    private Map<ChosenSupervisorData, Map<ChosenStudentData, Double>> supervisorPreferenceMap = new HashMap<>();
    private int iterationsToDo = 900; //"b" : needs experimenting with or made into argument
    private int solutionsToKeep = 16; //"a" : needs experimenting with or made into argument -> must be divisible by 2
    private boolean convergenceTestEnabled = false;
    private List<Double> summedScoresPerGeneration = new ArrayList<>();

    public AllocatorGeneticAlgorithm(List<ChosenStudentData> students, List<ChosenSupervisorData> supervisors) {
        super(students, supervisors);
    }

    /**
    * 1.Create "a"(6) initial Pairings
    * 2.Iterate "b"(10) times
    * 2.1.Evaluatate to keep best "a" solutions
    * 2.2.Mutate previous solutions to create "a" more (1 for each solution) - based on paper
    * 2.3.Crossover previous solutions to create "a/2" more (2 input/output) - based on paper
    * 2.4.Have total "a + a + a/2"(=15) solutions
    * 3.Evaluate to keep best solution
    */
    public List<Pairing> generatePairings() throws NoViableSolutionException{
        //Parameters
        final double mutationProbability = 0.05; //between 0.05 and 0.5 -> 0.05 recommended by paper
        final double swapProbability = 0.2; //between 0.1 and 0.9 -> 0.2 recommended by paper

        if (!checkIfAllocationPossible()){
            throw new NoViableSolutionException("More students than slots available");
        }

        createPreferenceMaps();
        Random random = new Random();
        List<List<ChosenPairing>> bestChosenSolutions = initializePopulation(solutionsToKeep, random);

        for (int currentIteration = 0; currentIteration < iterationsToDo; currentIteration++) {
            List<List<ChosenPairing>> newBestSolutions;
            if(currentIteration == 0){
                newBestSolutions = bestChosenSolutions;
            } else {
                newBestSolutions = evaluateAndSelect(bestChosenSolutions, solutionsToKeep);
            }
            List<List<ChosenPairing>> mutatedSolutions = mutate(newBestSolutions, random, mutationProbability, swapProbability);
            List<List<ChosenPairing>> crossedOverSolutions = crossover(newBestSolutions, random, solutionsToKeep);

            bestChosenSolutions.clear();
            bestChosenSolutions.addAll(newBestSolutions);
            bestChosenSolutions.addAll(mutatedSolutions);
            bestChosenSolutions.addAll(crossedOverSolutions);
        }
         
        List<ChosenPairing> solutionCP = evaluateAndSelect(bestChosenSolutions, 1).get(0);
        List<Pairing> solution = new ArrayList<>();
        for (ChosenPairing cp : solutionCP){
            solution.add(new Pairing(cp.getStudentData().getStudent(), cp.getSupervisorData().getSupervisor()));
        }
             
        return solution;
    }

    private List<List<ChosenPairing>> crossover(List<List<ChosenPairing>> newBestSolutions, Random random, int nrToKeep) {
        //List<List<ChosenPairing>> dontHaveChildrenYET = newBestSolutions;
        List<List<ChosenPairing>> dontHaveChildrenYet = new ArrayList<>();
        dontHaveChildrenYet.addAll(newBestSolutions);
        List<List<ChosenPairing>> childrenSolutions = new ArrayList<>();

        for (int currentIteration = 0; currentIteration < (nrToKeep/2); currentIteration++){
            List<ChosenPairing> daddySolution = dontHaveChildrenYet.get(random.nextInt(dontHaveChildrenYet.size()));
            dontHaveChildrenYet.remove(daddySolution);
            List<ChosenPairing> mommySolution = dontHaveChildrenYet.get(random.nextInt(dontHaveChildrenYet.size()));
            dontHaveChildrenYet.remove(mommySolution);
            
            //Merge
            List<ChosenPairing> mergedChildSolution = new ArrayList<>(mommySolution);
            for (ChosenPairing daddyPair : daddySolution){
                boolean isPairPresent = false;
                for (ChosenPairing mommyPair : mergedChildSolution){
                    if(daddyPair.equals(mommyPair)){
                        isPairPresent = true;
                    }
                }
                if (!isPairPresent){
                    mergedChildSolution.add(daddyPair);
                }
            }

            //Simplify
            List<List<ChosenPairing>> simplifiedChildSolutionAndLockedEdges = simplifySolution(mergedChildSolution);
            List<ChosenPairing> unlockedEdges = simplifiedChildSolutionAndLockedEdges.get(0);
            List<ChosenPairing> lockedEdges = simplifiedChildSolutionAndLockedEdges.get(1);

            //Lock&Remove
            lockAndRemove(unlockedEdges, lockedEdges, random);

            //Add Edges (Pairings)
            handleUnmatchedStudents(lockedEdges, random);
            

            childrenSolutions.add(lockedEdges);
        }

        return childrenSolutions;
    }

    private void handleUnmatchedStudents(List<ChosenPairing> lockedEdges, Random random) {
        List<ChosenStudentData> unassignedStudents = new ArrayList<>(students);
        List<ChosenSupervisorData> fullSupervisors = new ArrayList<>();
        int unassignedStudentsProcessed = 0;
        Set<ChosenStudentData> studentsToRemove = new HashSet<>();
        for (ChosenPairing lockedPairs : lockedEdges){
            if (unassignedStudents.contains(lockedPairs.getStudentData())){
                studentsToRemove.add(lockedPairs.getStudentData());
            }
        }
        unassignedStudents.removeAll(studentsToRemove);

        while (unassignedStudentsProcessed < unassignedStudents.size()){
            ChosenStudentData student = unassignedStudents.get(random.nextInt(unassignedStudents.size()));
            ChosenSupervisorData supervisor = supervisors.get(random.nextInt(supervisors.size()));
            if (student.getTutor() != null){
                while((isSupervisorFull(supervisor, lockedEdges, fullSupervisors)) && (student.getTutor().equals(supervisor.getSupervisor()))){
                    supervisor = supervisors.get(random.nextInt(supervisors.size()));
                }
            } else {
                while(isSupervisorFull(supervisor, lockedEdges, fullSupervisors)){
                    supervisor = supervisors.get(random.nextInt(supervisors.size()));
                }
            } 
            
            lockedEdges.add(new ChosenPairing(student, supervisor));
            unassignedStudentsProcessed++;
        }
    }

    private void lockAndRemove(List<ChosenPairing> unlockedEdges, List<ChosenPairing> lockedEdges, Random random) {
        List<ChosenPairing> edgesLeft = new ArrayList<>(unlockedEdges);
        List<ChosenSupervisorData> fullSupervisors = new ArrayList<>();
        int edgesProcessed = 0;
        while (edgesProcessed < unlockedEdges.size()) {
            ChosenPairing chosenEdge = edgesLeft.get(random.nextInt(edgesLeft.size()));
            lockedEdges.add(chosenEdge);
            edgesLeft.remove(chosenEdge);
            edgesProcessed++;
    
            ChosenStudentData selectedStudent = chosenEdge.getStudentData();
            List<ChosenPairing> edgesToRemoveStudent = new ArrayList<>();
            for (ChosenPairing pair : edgesLeft){
                if (pair.getStudentData().equals(selectedStudent)){
                    edgesToRemoveStudent.add(pair);
                    edgesProcessed++;
                }
            }
            edgesLeft.removeAll(edgesToRemoveStudent);
    
            if (isSupervisorFull(chosenEdge.getSupervisorData(), lockedEdges, fullSupervisors)) {
                ChosenSupervisorData selectedSupervisor = chosenEdge.getSupervisorData();
                List<ChosenPairing> edgesToRemoveSupervisor = new ArrayList<>();
                for (ChosenPairing pair : edgesLeft){
                    if (pair.getSupervisorData().equals(selectedSupervisor)){
                        edgesToRemoveSupervisor.add(pair);
                        edgesProcessed++;
                    }
                }
                edgesLeft.removeAll(edgesToRemoveSupervisor);
            }
        }
    }

    /**
     * Create Map of students with a list of all pairings that contain them
     * Loop while solution can be simplified
     *      Iterate through all students
     *          Check if any students have one element in their list in pairingsByStudent
     *              If such a student found check the supervisor from their pairing by calling isSupervisorFull function
     *                  If returns false, add pairing to simplifiedSolution
     *                  If returns true, remove all pairings from pairingsByStudent that include the respective supervisor
     * Return locked and unlocked edges
     */
    private List<List<ChosenPairing>> simplifySolution(List<ChosenPairing> mergedChildSolution) {
        List<List<ChosenPairing>> lockedAndUnlockedEdges = new ArrayList<>();
        List<ChosenPairing> lockedEdges = new ArrayList<>();
        List<ChosenPairing> unlockedEdges = new ArrayList<>();

        Map<ChosenStudentData, List<ChosenPairing>> pairingsByStudent = new HashMap<>();
        for (ChosenStudentData student : students){
            pairingsByStudent.put(student, new ArrayList<>());
        }
        for (ChosenPairing pair : mergedChildSolution){
            pairingsByStudent.get(pair.getStudentData()).add(pair);
        }

        List<ChosenSupervisorData> fullSupervisors = new ArrayList<>();
        boolean changesMade;
        do {
            changesMade = false;
            List<ChosenStudentData> studentsToRemove = new ArrayList<>();
            for (List<ChosenPairing> studentAllocations : pairingsByStudent.values()) {
                if (studentAllocations.size() == 1) {
                    ChosenPairing pair = studentAllocations.get(0);
                    if (!isSupervisorFull(pair.getSupervisorData(), lockedEdges, fullSupervisors)) {
                        lockedEdges.add(pair);
                        studentsToRemove.add(pair.getStudentData());
                        break;
                    } else {
                        pairingsByStudent.values().forEach(p -> p.removeIf(paired -> paired.getSupervisorData().equals(pair.getSupervisorData())));
                    }
                    changesMade = true;
                }
            }
            for (ChosenStudentData stu : studentsToRemove){
                pairingsByStudent.remove(stu);
            }
        } while (changesMade);

        pairingsByStudent.values().forEach(unlockedEdges::addAll);
        lockedAndUnlockedEdges.add(unlockedEdges);
        lockedAndUnlockedEdges.add(lockedEdges);

        return lockedAndUnlockedEdges;
    }

    private List<List<ChosenPairing>> mutate(List<List<ChosenPairing>> newBestSolutions, Random random, double mutationProbability, double swapProbability) {
        List<List<ChosenPairing>> mutatedSolutions = new ArrayList<>();

        for (List<ChosenPairing> solution : newBestSolutions){
            List<ChosenPairing> mutatedSolution = new ArrayList<>(solution);

            for (ChosenPairing pair : solution) {
                if (random.nextDouble() <= mutationProbability) {
                    if (random.nextDouble() > swapProbability) {
                        //Transfer operation
                        List<List<ChosenSupervisorData>> fullAndNotSupervisors = getSupervisorCapacity(solution);
                        List<ChosenSupervisorData> nonFullSupervisors = fullAndNotSupervisors.get(1);
                        if (!nonFullSupervisors.isEmpty()) {
                            ChosenSupervisorData supervisor = nonFullSupervisors.get(random.nextInt(nonFullSupervisors.size()));

                            if ((pair.getStudentData().getTutor() != null) && (nonFullSupervisors.size() > 1)){
                                while(pair.getStudentData().getTutor().equals(supervisor.getSupervisor())){
                                    supervisor = nonFullSupervisors.get(random.nextInt(nonFullSupervisors.size()));
                                }
                            } 

                            mutatedSolution.add(new ChosenPairing(pair.getStudentData(), supervisor));
                            mutatedSolution.remove(pair); 
                        }
                    } else if (supervisors.size() > 1){
                        //Swap operation
                        ChosenPairing randomPairing = getRandomPair(solution, pair, random);
                        if (pair.getStudentData().getTutor() != null){
                            while((pair.getStudentData().getTutor().equals(randomPairing.getSupervisorData().getSupervisor())) && 
                                (randomPairing.getStudentData().getTutor().equals(pair.getSupervisorData().getSupervisor()))){
                                    randomPairing = getRandomPair(solution, pair, random);
                            }
                        }
                        ChosenSupervisorData randomSupervisor = randomPairing.getSupervisorData();
                        mutatedSolution.add(new ChosenPairing(pair.getStudentData(), randomSupervisor));
                        mutatedSolution.add(new ChosenPairing(randomPairing.getStudentData(), pair.getSupervisorData()));
                        mutatedSolution.remove(randomPairing);
                        mutatedSolution.remove(pair);
                        
                    }
                
                }

            }

            Set<ChosenStudentData> studentAlreadyHasPairing = new HashSet<>();
            List<ChosenPairing> solutionsToRemove = new ArrayList<>();
            for (ChosenPairing pair : mutatedSolution){
                if (studentAlreadyHasPairing.contains(pair.getStudentData())){
                    solutionsToRemove.add(pair);
                } else {
                    studentAlreadyHasPairing.add(pair.getStudentData());
                }
            }
            mutatedSolution.removeAll(solutionsToRemove);

            mutatedSolutions.add(mutatedSolution);
        }

        return mutatedSolutions;
    }

    private ChosenPairing getRandomPair(List<ChosenPairing> solution, ChosenPairing pair, Random random) {
        List<ChosenPairing> pairingsWithDifferentSupervisor = new ArrayList<>();

        for (ChosenPairing pairing : solution) {
            if (!pairing.getSupervisorData().equals(pair.getSupervisorData())) {
                pairingsWithDifferentSupervisor.add(pairing);
            }
        }

        return pairingsWithDifferentSupervisor.get(random.nextInt(pairingsWithDifferentSupervisor.size()));
    }

    private List<List<ChosenSupervisorData>> getSupervisorCapacity(List<ChosenPairing> solution) {
        List<List<ChosenSupervisorData>> fullAndNonFullSupervisors = new ArrayList<>();
        List<ChosenSupervisorData> fullSupervisors = new ArrayList<>();
        List<ChosenSupervisorData> nonFullSupervisors = new ArrayList<>();

        for (ChosenSupervisorData sup : supervisors){
            if (!isSupervisorFull(sup, solution, fullSupervisors)){
                nonFullSupervisors.add(sup);
            }
        }

        fullAndNonFullSupervisors.add(fullSupervisors);
        fullAndNonFullSupervisors.add(nonFullSupervisors);
        return fullAndNonFullSupervisors;
    }

    /**
     * Currently optimising for Student Satisfaction.
     * Can be modified to optimise for:
     * 1.SupervisorScore/Preference
     * 2.Student AND Supervisor Score/Preference
     * The two lines commented out inside the method will allow you to change what you optimise for easily.
     */
    private List<List<ChosenPairing>> evaluateAndSelect(List<List<ChosenPairing>> newSolutionSet, int nrToKeep) {
        List<List<ChosenPairing>> bestSolutions = new ArrayList<>();
        Map<List<ChosenPairing>, Double> solutionsAndScores = new HashMap<>();

        for (List<ChosenPairing> currentSolution : newSolutionSet){
            Double currentSolutionScore = 0.00;
            for (ChosenPairing pair : currentSolution){
                Double studentScore = studentPreferenceMap.get(pair.getStudentData()).get(pair.getSupervisorData());
                //Double supervisorScore = supervisorPreferenceMap.get(pair.getSupervisorData()).get(pair.getStudentData());
                //currentSolutionScore = currentSolutionScore + (0.5 * studentScore) + (0.5 * supervisorScore);
                currentSolutionScore = currentSolutionScore + studentScore;
            }
            solutionsAndScores.put(currentSolution, currentSolutionScore);
        }
        
        List<Map.Entry<List<ChosenPairing>, Double>> sortedSolutions = new ArrayList<>(solutionsAndScores.entrySet());
        sortedSolutions.sort((entry1, entry2) -> {
            int scoreCompare = entry2.getValue().compareTo(entry1.getValue());      
            if(scoreCompare != 0) {
                return scoreCompare;
            } else {
                long hash1 = entry1.getKey().hashCode();
                long hash2 = entry2.getKey().hashCode();
                return Long.compare(hash1, hash2);
            }
        });
        
        List<Double> topPairScores = new ArrayList<>();
        for (int i = 0; i < Math.min(nrToKeep, sortedSolutions.size()); i++) {
            bestSolutions.add(sortedSolutions.get(i).getKey());
            
            if(convergenceTestEnabled){
                topPairScores.add(sortedSolutions.get(i).getValue());
            }
        }

        if(convergenceTestEnabled){
            Double sumTopPairScores = topPairScores.stream().mapToDouble(Double::doubleValue).sum();
            Double averageTopPairScore = sumTopPairScores / Math.min(nrToKeep, sortedSolutions.size());
            summedScoresPerGeneration.add(averageTopPairScore);
        }

        return bestSolutions;
    }

    private List<List<ChosenPairing>> initializePopulation(int nrToGenerate, Random random){
        List<List<ChosenPairing>> initialSolutions = new ArrayList<>();

        for(int currentlyGenerated = 0; currentlyGenerated < nrToGenerate; currentlyGenerated++){
            List<ChosenPairing> pairings = new ArrayList<>();
            List<ChosenSupervisorData> fullSupervisors = new ArrayList<>();

            for (int i = 0; i < students.size(); i++) {
                ChosenStudentData student = students.get(i);
                ChosenSupervisorData supervisor = supervisors.get(random.nextInt(supervisors.size()));

                if (student.getTutor() != null){
                    while((isSupervisorFull(supervisor, pairings, fullSupervisors)) && (student.getTutor().equals(supervisor.getSupervisor()))){
                        supervisor = supervisors.get(random.nextInt(supervisors.size()));
                    }
                } else {
                    while(isSupervisorFull(supervisor, pairings, fullSupervisors)){
                        supervisor = supervisors.get(random.nextInt(supervisors.size()));
                    }
                } 

                ChosenPairing pairing = new ChosenPairing(student, supervisor);
                pairings.add(pairing);
            }

            initialSolutions.add(pairings);
        }

        return initialSolutions;
    }

    private boolean isSupervisorFull(ChosenSupervisorData supervisor, List<ChosenPairing> pairings, List<ChosenSupervisorData> fullSupervisors){
        boolean isSupervisorFull;
        if (fullSupervisors.contains(supervisor)){
            isSupervisorFull = true;
        } else {
            int count = 0;
            for (ChosenPairing pairing : pairings) {
                if (pairing.getSupervisorData().equals(supervisor)) {
                    count++;
                }
            }
            if (count < supervisor.getMaxSupervisees()){
                isSupervisorFull = false;
            } else {
                isSupervisorFull = true;
                fullSupervisors.add(supervisor);
            }
        }
        return isSupervisorFull;
    }

    public void setParameters(int nrOfGenerations, int keepBestNSolutionsEachGeneration){
        iterationsToDo = nrOfGenerations;
        solutionsToKeep = keepBestNSolutionsEachGeneration;
    }

    public void enableConvergenceTest(boolean enable){
        convergenceTestEnabled = enable;
    }

    public List<Double> getGenerationScores(){
        return summedScoresPerGeneration;
    }


    public void createPreferenceMaps() {
        students.forEach(this::calculateStudentPreferences);
        supervisors.forEach(this::calculateSupervisorPreferences);
    }

    private void calculateStudentPreferences(ChosenStudentData student) {
        HashMap<ChosenSupervisorData, Double> supervisorScores = new HashMap<>();
        for (ChosenSupervisorData sup : supervisors){
            Double score = (double) calculateInterestScoreForPairing(student, sup);
            supervisorScores.put(sup, score);
        }
        studentPreferenceMap.put(student, supervisorScores);
    }

    private void calculateSupervisorPreferences(ChosenSupervisorData supervisor) {
        HashMap<ChosenStudentData, Double> studentScores = new HashMap<>();
        for (ChosenStudentData stu : students){
            Double score = (double) calculateInterestScoreForPairing(stu, supervisor);
            studentScores.put(stu, score);
        }

        supervisorPreferenceMap.put(supervisor, studentScores);
    }

    public Map<ChosenStudentData, Map<ChosenSupervisorData, Double>> getStudentPreferenceMap(){
        return studentPreferenceMap;
    }

    public Map<ChosenSupervisorData, Map<ChosenStudentData, Double>> getSupervisorPreferenceMap(){
        return supervisorPreferenceMap;
    }

    private boolean checkIfAllocationPossible(){
        int superviseeSlots = 0;
        int studentsToAllocate = students.size();

        for (ChosenSupervisorData sup : supervisors){
            superviseeSlots = superviseeSlots + sup.getMaxSupervisees();
        }

        return superviseeSlots > studentsToAllocate;
    }

    class ChosenPairing{
        private final ChosenStudentData student;
        private final ChosenSupervisorData supervisor;

        public ChosenPairing(ChosenStudentData student, ChosenSupervisorData supervisor){
            this.student = student;
            this.supervisor = supervisor;
        }

        public ChosenStudentData getStudentData() {
            return student;
        }

        public ChosenSupervisorData getSupervisorData() {
            return supervisor;
        }

    }
    
}
