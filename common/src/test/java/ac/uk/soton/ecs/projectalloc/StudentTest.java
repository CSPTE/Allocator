package ac.uk.soton.ecs.projectalloc;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.Random;
import org.junit.jupiter.api.Test;

public class StudentTest {

    @Test
    public void studentHasUsername() {
        Student student = new Student("abc1g20");

        assertThat(student, hasProperty("username", is("abc1g20")));
    }

    @Test
    public void twoStudentsAreEqual() {
        Student studentOne = new Student("abc1g20");
        Student studentTwo = new Student("abc1g20");

        assertEquals(studentOne, studentTwo);
    }

    @Test
    public void twoStudentsAreNotEqual() {
        Student studentOne = new Student("abc1g20");
        Student studentTwo = new Student("xyz1g20");

        assertNotEquals(studentOne, studentTwo);
    }

    @Test
    public void studentAndNonStudentAreNotEqual() {
        assertNotEquals(new Student("abc1g20"), new Supervisor("xyz"));
        assertNotEquals(new Student("abc1g20"), new Supervisor("abc1g20"));
        assertNotEquals(new Student("abc1g20"), new Random().nextInt(10));
    }

    @Test
    public void sameStudentObjectIsEqual() {
        Student student = new Student("abc1g20");

        assertEquals(student, student);
    }

}
