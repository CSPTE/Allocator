package ac.uk.soton.ecs.projectalloc;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

public class ChosenTest {

    @Test
    public void canAddStudentInterestAndGrade() {
        Student student = new Student("abc1g20");
        ChosenStudentData studentData = new ChosenStudentData(student, 10);

        assertThat(studentData, allOf(
            hasProperty("student", is(equalTo(student))),
            hasProperty("username", is(equalTo(student.getUsername()))),
            hasProperty("grade", is(10))
        ));

        Node root = new Node(0, "Root");
        Node child1 = new Node(1, "Child1");
        Node child2 = new Node(2, "Child2");
        studentData.setInterestTree(root);
        studentData.getInterestTree().addChildren(List.of(child1, child2));

        assertEquals(studentData.getInterestTree().getInterest(), root.getInterest());
        assertEquals(studentData.getInterestTree().getChildren().get(0).getInterest(), child1.getInterest());
        assertEquals(studentData.getInterestTree().getChildren().get(1).getInterest(), child2.getInterest());

    }

    @Test
    public void canAddSupervisorInterestAndMaxSupervisees() {
        Supervisor supervisor = new Supervisor("xyz");
        ChosenSupervisorData supervisorData = new ChosenSupervisorData(supervisor, 10);

        assertThat(supervisorData, allOf(
            hasProperty("supervisor", is(supervisor)),
            hasProperty("username", is(equalTo(supervisor.getUsername()))),
            hasProperty("maxSupervisees", is(10))
        ));

        Node root = new Node(0, "Root");
        Node child1 = new Node(1, "Child1");
        Node child2 = new Node(2, "Child2");
        supervisorData.setInterestTree(root);
        supervisorData.getInterestTree().addChildren(List.of(child1, child2));

        assertEquals(supervisorData.getInterestTree().getInterest(), root.getInterest());
        assertEquals(supervisorData.getInterestTree().getChildren().get(0).getInterest(), child1.getInterest());
        assertEquals(supervisorData.getInterestTree().getChildren().get(1).getInterest(), child2.getInterest());
    }

    @Test
    public void twoStudentDataAreCorrectlyEqual() {
        Student student = new Student("abc1g20");
        ChosenStudentData studentDataOne = new ChosenStudentData(student, 10);
        ChosenStudentData studentDataTwo = new ChosenStudentData(student, 10);

        Node root = new Node(0, "Root");
        Node child1 = new Node(1, "Child1");
        Node child2 = new Node(2, "Child2");

        root.addChildren(List.of(child1, child2));

        studentDataOne.setInterestTree(root);

        studentDataTwo.setInterestTree(root);

        assertEquals(studentDataOne, studentDataTwo);
    }

    @Test
    public void twoStudentDataWithDifferentInterestsButSameNameAreCorrectlyNotEqual() {
        Student student = new Student("abc1g20");
        ChosenStudentData studentDataOne = new ChosenStudentData(student, 10);
        ChosenStudentData studentDataTwo = new ChosenStudentData(student, 10);

        Node root1 = new Node(0, "Root");
        Node root2 = new Node(0, "Root");
        Node child1 = new Node(1, "Child1");
        Node child2 = new Node(2, "Child2");

        studentDataOne.setInterestTree(root1);
        studentDataOne.getInterestTree().addChildren(List.of(child1));

        studentDataTwo.setInterestTree(root2);
        studentDataTwo.getInterestTree().addChildren(List.of(child2));

        assertNotEquals(studentDataOne, studentDataTwo);
    }

    @Test
    public void supervisorDataAndStudentDataAreNotEqual() {
        Student student = new Student("abc1g20");
        ChosenStudentData studentData = new ChosenStudentData(student, 10);

        Supervisor supervisor = new Supervisor("xyz");
        ChosenSupervisorData supervisorData = new ChosenSupervisorData(supervisor, 10);

        assertNotEquals(studentData, supervisorData);
    }

    @Test
    public void studentHasSupervisor() {
        Supervisor tutor = new Supervisor("supervisor");
        Student student = new Student("abc1g20");

        ChosenStudentData studentData = new ChosenStudentData(student, 10);
        studentData.setTutor(tutor);

        assertThat(studentData, hasProperty("tutor", is(tutor)));
    }

}
