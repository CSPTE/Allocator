package ac.uk.soton.ecs.projectalloc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class PairingTest {

    @Test
    public void pairingHasBothSupervisorAndStudent() {
        Pairing pairing = new Pairing(new Student("abc1g20"), new Supervisor("xyz"));

        assertEquals("abc1g20", pairing.getStudent().getUsername());
        assertEquals("xyz", pairing.getSupervisor().getUsername());
    }

}
