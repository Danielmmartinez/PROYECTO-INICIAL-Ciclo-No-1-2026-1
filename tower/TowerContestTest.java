package tower;

import static org.junit.Assert.*;
import org.junit.Test;

public class TowerContestTest {

    @Test
    public void deberiaResolverSample1DelICPC() {
        assertEquals("7 3 5 1", TowerContest.solve(4, 9));
    }

    @Test
    public void deberiaRechazarSample2DelICPC() {
        assertEquals("impossible", TowerContest.solve(4, 100));
    }

    @Test
    public void deberiaCalcularAlturaMaximaParaN3() {
        assertEquals("1 3 5", TowerContest.solve(3, 9));
        assertEquals("impossible", TowerContest.solve(3, 10));
    }

    @Test
    public void deberiaCalcularAlturaMinimaParaN3() {
        assertEquals("5 3 1", TowerContest.solve(3, 5));
        assertEquals("impossible", TowerContest.solve(3, 4));
    }

    @Test
    public void deberiaManejarUnaSolaTaza() {
        assertEquals("1", TowerContest.solve(1, 1));
        assertEquals("impossible", TowerContest.solve(1, 2));
    }
}