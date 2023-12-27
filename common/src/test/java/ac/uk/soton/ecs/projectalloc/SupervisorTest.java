package ac.uk.soton.ecs.projectalloc;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class SupervisorTest {

    @Test
    public void supervisorHasUsername() {
        Supervisor supervisor = new Supervisor("xyz");

        assertThat(supervisor, hasProperty("username", is("xyz")));
    }

    @Test
    public void twoStudentsAreEqual() {
        Supervisor supervisorOne = new Supervisor("xyz");
        Supervisor supervisorTwo = new Supervisor("xyz");

        assertEquals(supervisorOne, supervisorTwo);
    }

    @Test
    public void sameSupervisorObjectIsEqual() {
        Supervisor supervisor = new Supervisor("xyz");

        assertEquals(supervisor, supervisor);
    }

}
