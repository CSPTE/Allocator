package ac.uk.soton.ecs.projectalloc.datagen;
import ac.uk.soton.ecs.projectalloc.*;
import junit.framework.TestCase;
import org.junit.Assert;

public class DataSetTest extends TestCase{
    public void testContainsStudentSameStorageSameStu() {
        DataSet ds = new DataSet();
        ChosenStudentData stu = new ChosenStudentData(new Student("af9g19@soton.ac.uk"),70);
        Node superTree = new Node(0);

        stu.setInterestTree(superTree);
        ds.setSuperInterestTree(superTree);
        ds.addStudent(stu);
        Assert.assertTrue(ds.containsStudent(stu));
    }

    public void testContainsStudentDiffStorageSameStu() {
        DataSet ds = new DataSet();
        ChosenStudentData stu = new ChosenStudentData(new Student("af9g19@soton.ac.uk"),70);
        ChosenStudentData stu2 = new ChosenStudentData(new Student("af9g19@soton.ac.uk"),70);
        Node superTree = new Node(0);
        Node superTree2 = new Node(0);

        ds.setSuperInterestTree(superTree);
        stu.setInterestTree(superTree);
        stu2.setInterestTree(superTree2);
        ds.addStudent(stu);

        Assert.assertTrue(ds.containsStudent(stu2));
    }

    public void testContainsSupervisorSameStorageSameStu() {
        DataSet ds = new DataSet();
        ChosenSupervisorData sup = new ChosenSupervisorData(new Supervisor("mark@soton.ac.uk"),3);
        Node superTree = new Node(0);

        ds.setSuperInterestTree(superTree);
        sup.setInterestTree(superTree);
        ds.addSupervisor(sup);

        Assert.assertTrue(ds.containsSupervisor(sup));
    }

    public void testContainsSupervisorDiffStorageSameStu() {
        DataSet ds = new DataSet();
        ChosenSupervisorData sup = new ChosenSupervisorData(new Supervisor("mark@soton.ac.uk"),3);
        ChosenSupervisorData sup2 = new ChosenSupervisorData(new Supervisor("mark@soton.ac.uk"),3);
        Node superTree = new Node(0);
        Node superTree2 = new Node(0);

        ds.setSuperInterestTree(superTree);
        sup.setInterestTree(superTree);
        sup2.setInterestTree(superTree2);

        ds.addSupervisor(sup);

        Assert.assertTrue(ds.containsSupervisor(sup2));
    }
}
