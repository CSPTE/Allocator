package ac.uk.soton.ecs.projectalloc.allocator;

import ac.uk.soton.ecs.projectalloc.*;

import java.util.*;

import ac.uk.soton.ecs.projectalloc.datagen.DataSet;
import ac.uk.soton.ecs.projectalloc.datagen.DataSetFactory;
import ac.uk.soton.ecs.projectalloc.datagen.TreeFactory;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AllocatorSimulatedAnnealingClassicTest {
    @Test
    void newInterestTree() {
        TreeFactory treeFactory = new TreeFactory();
        Node superTree = treeFactory.generateTree(13, 3,3);
        Node subTree1 = superTree.getChildren().get(0);
        Node subTree2 = superTree.getChildren().get(1);
        Node subTree3 = superTree.getChildren().get(2);

        List<ChosenSupervisorData> supervisors = new ArrayList<>();
        List<ChosenStudentData> students = new ArrayList<>();

        for (int i=0; i<3; i++) {
            Supervisor supervisor = new Supervisor(String.format("sup%d", i));
            ChosenSupervisorData chosenSupervisorData = new ChosenSupervisorData(supervisor, 5);

            supervisors.add(chosenSupervisorData);

            Student student = new Student(String.format("stu%d", i));
            ChosenStudentData chosenStudentData = new ChosenStudentData(student, 70);

            students.add(chosenStudentData);
        }

        supervisors.get(0).setInterestTree(subTree1);
        supervisors.get(1).setInterestTree(subTree2);
        supervisors.get(2).setInterestTree(subTree3);

        students.get(0).setInterestTree(subTree1);
        students.get(1).setInterestTree(subTree2);
        students.get(2).setInterestTree(subTree3);

        AllocatorSimulatedAnnealingAlgorithmClassic allocatorSimulatedAnnealingClassic = new AllocatorSimulatedAnnealingAlgorithmClassic(students, supervisors);

        assertEquals(allocatorSimulatedAnnealingClassic.calculateInterestScoreForPairing(students.get(0), supervisors.get(0)), 1);
        assertEquals(allocatorSimulatedAnnealingClassic.calculateInterestScoreForPairing(students.get(0), supervisors.get(1)), 0);
        assertEquals(allocatorSimulatedAnnealingClassic.calculateInterestScoreForPairing(students.get(0), supervisors.get(2)), 0);
        assertEquals(allocatorSimulatedAnnealingClassic.calculateInterestScoreForPairing(students.get(1), supervisors.get(0)), 0);
        assertEquals(allocatorSimulatedAnnealingClassic.calculateInterestScoreForPairing(students.get(1), supervisors.get(1)), 1);
        assertEquals(allocatorSimulatedAnnealingClassic.calculateInterestScoreForPairing(students.get(1), supervisors.get(2)), 0);
        assertEquals(allocatorSimulatedAnnealingClassic.calculateInterestScoreForPairing(students.get(2), supervisors.get(0)), 0);
        assertEquals(allocatorSimulatedAnnealingClassic.calculateInterestScoreForPairing(students.get(2), supervisors.get(1)), 0);
        assertEquals(allocatorSimulatedAnnealingClassic.calculateInterestScoreForPairing(students.get(2), supervisors.get(2)), 1);

        Map<ChosenStudentData, ChosenSupervisorData> pairings = new HashMap<>();
        pairings.put(students.get(0), supervisors.get(0));
        pairings.put(students.get(1), supervisors.get(0));
        pairings.put(students.get(2), supervisors.get(2));

        assertEquals(allocatorSimulatedAnnealingClassic.calculateTotalInterestScore(pairings), 2);
    }

    @Test
    void simulatePartIIIAndMSc() {
        int studentNumbers = 400;
        int supervisorNumbers = 100;
        int interestNumbers = 5;

        DataSetFactory dataSetFactory = new DataSetFactory();
        DataSet ds = dataSetFactory.genDataSet(studentNumbers,supervisorNumbers,20,20,20,20,20,100,0,0,0,0,
                100,0,0,0,0,6,6,interestNumbers,5,2,5);

        List<ChosenStudentData> students = new ArrayList<>(ds.getStudents());
        List<ChosenSupervisorData> supervisors = new ArrayList<>(ds.getSupervisors());

        AllocatorSimulatedAnnealingAlgorithmClassic allocatorSimulatedAnnealingClassic = new AllocatorSimulatedAnnealingAlgorithmClassic(students, supervisors);

        List<Pairing> pairings = allocatorSimulatedAnnealingClassic.generatePairings();

        assertEquals(pairings.size(), studentNumbers);
    }

    @Test
    void numberOfStudentsToShuffleTest() {
        int studentNumbers = 400;
        int supervisorNumbers = 100;
        int interestNumbers = 5;

        DataSetFactory dataSetFactory = new DataSetFactory();
        DataSet ds = dataSetFactory.genDataSet(studentNumbers,supervisorNumbers,20,20,20,20,20,100,0,0,0,0,
                100,0,0,0,0,6,6,interestNumbers,5,2,5);

        List<ChosenStudentData> students = new ArrayList<>(ds.getStudents());
        List<ChosenSupervisorData> supervisors = new ArrayList<>(ds.getSupervisors());

        AllocatorSimulatedAnnealingAlgorithmClassic allocatorSimulatedAnnealingClassic = new AllocatorSimulatedAnnealingAlgorithmClassic(students, supervisors);

        allocatorSimulatedAnnealingClassic.setPercentageOfStudentsToShuffle(0.02);
        assertEquals(8, allocatorSimulatedAnnealingClassic.getNumberOfStudentsToShuffle());

        allocatorSimulatedAnnealingClassic.setPercentageOfStudentsToShuffle(0.01);
        assertEquals(4, allocatorSimulatedAnnealingClassic.getNumberOfStudentsToShuffle());

    }

    @Test
    void emptyStudentList() {
        int studentNumbers = 5;
        int supervisorNumbers = 1;
        int interestNumbers = 5;

        DataSetFactory dataSetFactory = new DataSetFactory();
        DataSet ds = dataSetFactory.genDataSet(studentNumbers,supervisorNumbers,20,20,20,20,20,100,0,0,0,0,
                100,0,0,0,0,6,6,interestNumbers,5,2,5);

        List<ChosenStudentData> noStudents = new ArrayList<>();
        List<ChosenSupervisorData> supervisors = new ArrayList<>(ds.getSupervisors());

        AllocatorSimulatedAnnealingAlgorithmClassic allocatorSimulatedAnnealingClassic = new AllocatorSimulatedAnnealingAlgorithmClassic(noStudents, supervisors);

        assertThrows(IllegalArgumentException.class, allocatorSimulatedAnnealingClassic::generatePairings);
    }

    @Test
    void emptySupervisorList() {
        TreeFactory treeFactory = new TreeFactory();
        Node superTree = treeFactory.generateTree(12, 3,3);
        Node subTree1 = superTree.getChildren().get(0);
        Node subTree2 = superTree.getChildren().get(1);
        Node subTree3 = superTree.getChildren().get(2);

        List<ChosenSupervisorData> supervisors = new ArrayList<>();
        List<ChosenStudentData> students = new ArrayList<>();

        for (int i=0; i<3; i++) {
            Supervisor supervisor = new Supervisor(String.format("sup%d", i));

            Student student = new Student(String.format("stu%d", i));
            ChosenStudentData chosenStudentData = new ChosenStudentData(student, 70);

            chosenStudentData.setTutor(supervisor);

            students.add(chosenStudentData);
        }

        students.get(0).setInterestTree(subTree1);
        students.get(1).setInterestTree(subTree2);
        students.get(2).setInterestTree(subTree3);

        AllocatorSimulatedAnnealingAlgorithmClassic allocatorSimulatedAnnealingClassic = new AllocatorSimulatedAnnealingAlgorithmClassic(students, supervisors);

        assertThrows(IllegalArgumentException.class, allocatorSimulatedAnnealingClassic::generatePairings);
    }
    
    @Test
    void insufficientSuperviseeSlots() {
        int studentNumbers = 10;
        int supervisorNumbers = 2;
        int interestNumbers = 5;
        int numberOfSupervisees = 4;

        Assert.assertThrows(IllegalArgumentException.class, () -> {
            DataSetFactory dataSetFactory = new DataSetFactory();
            DataSet ds = dataSetFactory.genDataSet(studentNumbers, supervisorNumbers, 20, 20, 20, 20, 20, 100, 0, 0, 0, 0,
                    100, 0, 0, 0, 0, numberOfSupervisees, numberOfSupervisees, interestNumbers, 5, 2, 5);

            List<ChosenStudentData> students = new ArrayList<>(ds.getStudents());
            List<ChosenSupervisorData> supervisors = new ArrayList<>(ds.getSupervisors());

            AllocatorSimulatedAnnealingAlgorithmClassic allocatorSimulatedAnnealingClassic = new AllocatorSimulatedAnnealingAlgorithmClassic(students, supervisors);

            List<Pairing> pairings = allocatorSimulatedAnnealingClassic.generatePairings();

        });
    }
}
