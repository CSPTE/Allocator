package ac.uk.soton.ecs.projectalloc.allocator;

import ac.uk.soton.ecs.projectalloc.ChosenStudentData;
import ac.uk.soton.ecs.projectalloc.Pairing;
import java.util.ArrayList;
import java.util.List;

import ac.uk.soton.ecs.projectalloc.datagen.DataSet;
import ac.uk.soton.ecs.projectalloc.datagen.DataSetFactory;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AllocatorLinearTest {

    @Test
    void simulatePartIII() {
        int studentNumbers = 100;
        int supervisorNumbers = 25;
        int interestNumbers = 5;
        DataSetFactory dataSetFactory = new DataSetFactory();
        DataSet ds = dataSetFactory.genDataSet(studentNumbers,supervisorNumbers,20,20,20,20,20,100,0,0,0,0,
                100,0,0,0,0,6,6,10,interestNumbers,2,5);

        AllocatorLinearAlgorithm allocatorLinear = new AllocatorLinearAlgorithm(ds.getStudents(), ds.getSupervisors());

        List<Pairing> pairings =  allocatorLinear.generatePairings();

        assertEquals(pairings.size(), studentNumbers);
    }

    @Test
    void emptyStudentList() {
        int studentNumbers = 5;
        int supervisorNumbers = 1;
        int interestNumbers = 5;

        DataSetFactory dataSetFactory = new DataSetFactory();
        DataSet ds = dataSetFactory.genDataSet(studentNumbers,supervisorNumbers,20,20,20,20,20,100,0,0,0,0,
                100,0,0,0,0,6,6,10,interestNumbers,2,5);

        List<ChosenStudentData> noStudents = new ArrayList<>();

        AllocatorLinearAlgorithm allocatorLinear = new AllocatorLinearAlgorithm(noStudents, ds.getSupervisors());

        assertThrows(IllegalArgumentException.class, allocatorLinear::generatePairings);
    }

    @Test
    void emptySupervisorList() {
        int studentNumbers = 100;
        int supervisorNumbers = 1;
        int interestNumbers = 5;

        Assert.assertThrows(IllegalArgumentException.class, () -> {
            DataSetFactory dataSetFactory = new DataSetFactory();
            DataSet ds = dataSetFactory.genDataSet(studentNumbers, supervisorNumbers, 20, 20, 20, 20, 20, 100, 0, 0, 0, 0,
                    100, 0, 0, 0, 0, 6, 6, 10, interestNumbers, 2, 5);

            ArrayList<ChosenStudentData> noStudents = new ArrayList<>();

            AllocatorLinearAlgorithm allocatorLinear = new AllocatorLinearAlgorithm(noStudents, ds.getSupervisors());

        });

    }

    @Test
    void insufficientSuperviseeSlots() {
        int studentNumbers = 12;
        int supervisorNumbers = 2;
        int interestNumbers = 5;
        int numberOfSupervisees = 4;

        Assert.assertThrows(IllegalArgumentException.class, () -> {
                    DataSetFactory dataSetFactory = new DataSetFactory();
                    DataSet ds = dataSetFactory.genDataSet(studentNumbers, supervisorNumbers, 20, 20, 20, 20, 20, 100, 0, 0, 0, 0,
                            100, 0, 0, 0, 0, numberOfSupervisees, numberOfSupervisees, 10, interestNumbers, 2, 5);

                    AllocatorLinearAlgorithm allocatorLinear = new AllocatorLinearAlgorithm(ds.getStudents(), ds.getSupervisors());

                    List<Pairing> pairings =  allocatorLinear.generatePairings();

                    for (Pairing pairing : pairings){
                        System.out.printf("%s is matched to %s%n", pairing.getStudent().getUsername(), pairing.getSupervisor().getUsername());
                    }
        });
    }
}
