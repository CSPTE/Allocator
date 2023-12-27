package ac.uk.soton.ecs.projectalloc.allocator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import ac.uk.soton.ecs.projectalloc.datagen.DataSet;
import ac.uk.soton.ecs.projectalloc.datagen.DataSetFactory;
import org.junit.jupiter.api.Test;

import ac.uk.soton.ecs.projectalloc.ChosenStudentData;
import ac.uk.soton.ecs.projectalloc.ChosenSupervisorData;
import ac.uk.soton.ecs.projectalloc.Pairing;

class AllocatorGeneticAlgorithmTest {

    @Test
    void testGeneticAlgorithm() throws NoViableSolutionException{
        int studentNumbers = 100;
        int supervisorNumbers = 25;
        int interestNumbers = 5;

        DataSetFactory dataSetFactory = new DataSetFactory();
        DataSet ds = dataSetFactory.genDataSet(studentNumbers,supervisorNumbers,20,20,20,20,20,100,0,0,0,0,
                100,0,0,0,0,6,6,interestNumbers,5,2,5);


        AllocatorGeneticAlgorithm ga = new AllocatorGeneticAlgorithm(ds.getStudents(), ds.getSupervisors());
        List<Pairing> pairings = ga.generatePairings();

        assertEquals(studentNumbers, pairings.size());
    }

    @Test
    void testConstraintsGeneticAlgorithm() throws NoViableSolutionException{
        int studentNumbers = 100;
        int supervisorNumbers = 25;
        int interestNumbers = 5;

        DataSetFactory dataSetFactory = new DataSetFactory();
        DataSet ds = dataSetFactory.genDataSet(studentNumbers,supervisorNumbers,20,20,20,20,20,100,0,0,0,0,
                100,0,0,0,0,6,6,interestNumbers,5,2,5);


        AllocatorGeneticAlgorithm ga = new AllocatorGeneticAlgorithm(ds.getStudents(), ds.getSupervisors());
        List<Pairing> pairings = ga.generatePairings();

        assertEquals(100, pairings.size());
    }

    @Test
    void testConvergenceGeneticAlgorithm() throws NoViableSolutionException{
        int studentNumbers = 100;
        int supervisorNumbers = 25;
        int interestNumbers = 5;

        DataSetFactory dataSetFactory = new DataSetFactory();
        DataSet ds = dataSetFactory.genDataSet(studentNumbers,supervisorNumbers,20,20,20,20,20,100,0,0,0,0,
                100,0,0,0,0,6,6,interestNumbers,5,2,5);

        AllocatorGeneticAlgorithm ga = new AllocatorGeneticAlgorithm(ds.getStudents(), ds.getSupervisors());

        ga.setParameters(100, 12);
        ga.enableConvergenceTest(true);

        List<Pairing> pairings = ga.generatePairings();

        List<Double> generationScores = ga.getGenerationScores();

        for (int i = 1; i < generationScores.size(); i++) {
            System.out.println("Generation " + i + " score: " + generationScores.get(i));
            assertTrue(generationScores.get(i) >= generationScores.get(i - 1), "Generation " + i + " score should be greater than or equal to Generation " + (i - 1) + " score");
        }
    }

    @Test
    void testTutor() throws NoViableSolutionException{
        int studentNumbers = 100;
        int supervisorNumbers = 25;
        int interestNumbers = 5;

        DataSetFactory dataSetFactory = new DataSetFactory();
        DataSet ds = dataSetFactory.genDataSet(studentNumbers,supervisorNumbers,20,20,20,20,20,100,0,0,0,0,
                100,0,0,0,0,6,6,interestNumbers,5,2,5);


        Random random = new Random();
        for (ChosenStudentData stu : ds.getStudents()){
            ChosenSupervisorData supervisor = ds.getSupervisors().get(random.nextInt(ds.getSupervisors().size()));
            stu.setTutor(supervisor.getSupervisor());
        }

        AllocatorGeneticAlgorithm ga = new AllocatorGeneticAlgorithm(ds.getStudents(), ds.getSupervisors());
        ga.setParameters(100, 12);
        ga.enableConvergenceTest(true);

        List<Pairing> pairings = ga.generatePairings();

        List<Double> generationScores = ga.getGenerationScores();

        assertEquals(100, pairings.size());

        for (int i = 1; i < generationScores.size(); i++) {
            System.out.println("Generation " + i + " score: " + generationScores.get(i));
            assertTrue(generationScores.get(i) >= generationScores.get(i - 1), "Generation " + i + " score should be greater than or equal to Generation " + (i - 1) + " score");
        }
    }

    /**
     * Observations:
     *  Increasing Score Correlates With Increasing:
     *      NR OF GENERATIONS
     *      INTERESTS SELECTED BY PARTICIPANT
     *  Reducing Variation Correlates With Increasing:
     *      CHILDREN KEPT PER GENERATION
     *      TOTAL NR OF INTERESTS IN SYSTEM
     */

    @Test
    void simulatePartIIIAndMSc() throws NoViableSolutionException {
        int studentNumbers = 400;
        int supervisorNumbers = 100;
        int interestNumbers = 5;

        DataSetFactory dataSetFactory = new DataSetFactory();
        DataSet ds = dataSetFactory.genDataSet(studentNumbers,supervisorNumbers,20,20,20,20,20,100,0,0,0,0,
                100,0,0,0,0,6,6,interestNumbers,5,2,5);

        List<ChosenStudentData> students = new ArrayList<>(ds.getStudents());
        List<ChosenSupervisorData> supervisors = new ArrayList<>(ds.getSupervisors());

        AllocatorGeneticAlgorithm allocatorGeneticAlgorithm = new AllocatorGeneticAlgorithm(students, supervisors);

        List<Pairing> pairings = allocatorGeneticAlgorithm.generatePairings();

        assertEquals(pairings.size(), studentNumbers);
    }

    @Test
    void testGeneticAlgorithmParametersToFindAcceptableDefaultSetupTestCaseMethodThing() throws NoViableSolutionException {
        int studentNumbers = 400;
        int supervisorNumbers = 120;
        int interestNumbers = 10;

        DataSetFactory dataSetFactory = new DataSetFactory();
        DataSet ds = dataSetFactory.genDataSet(studentNumbers,supervisorNumbers,20,20,20,20,20,100,0,0,0,0,
                100,0,0,0,0,6,6,interestNumbers,5,2,5);

        List<List<Integer>> parameterSets = Arrays.asList(Arrays.asList(300, 30), Arrays.asList(350, 35), 
            Arrays.asList(400, 40));

        for (List<Integer> parameters : parameterSets) {
            int iterationsToDo = parameters.get(0);
            int solutionsToKeep = parameters.get(1);

            for (int run = 1; run <= 1; run++) {
                AllocatorGeneticAlgorithm ga = new AllocatorGeneticAlgorithm(ds.getStudents(), ds.getSupervisors());
                ga.setParameters(iterationsToDo, solutionsToKeep);
                ga.enableConvergenceTest(true);

                List<Pairing> pairings = ga.generatePairings();

                double lastGenerationScore = ga.getGenerationScores().get(ga.getGenerationScores().size() - 1);

                System.out.println("Run: " + run + ", Generations: " + iterationsToDo + ", Offsprings: " + solutionsToKeep + ", Final Score: " + lastGenerationScore);
                //System.out.println("Final Score: " + lastGenerationScore);
            }
            System.out.println();
        }   
    }   
}
