import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Pruebas unitarias para el simulador de Stacking Cups.
 */
public class TowerContestTest {


    @Test
    public void deberiaResolverSample1DelICPC() {
        TowerContest contest = new TowerContest();
        
        assertEquals("7 3 5 1", contest.solve(4, 9));
    }

    @Test
    public void deberiaRechazarSample2DelICPC() {
        TowerContest contest = new TowerContest();
        
        assertEquals("impossible", contest.solve(4, 100));
    }

    @Test
    public void deberiaCalcularAlturaMaximaParaN3() {
        TowerContest contest = new TowerContest();
        assertEquals("1 3 5", contest.solve(3, 9));
        
        assertEquals("impossible", contest.solve(3, 10));
    }

    @Test
    public void deberiaCalcularAlturaMinimaParaN3() {
        TowerContest contest = new TowerContest();
        assertEquals("5 3 1", contest.solve(3, 5));
        
        assertEquals("impossible", contest.solve(3, 4));
    }

    @Test
    public void deberiaManejarUnaSolaTaza() {
        TowerContest contest = new TowerContest();
        
        assertEquals("1", contest.solve(1, 1));
        assertEquals("impossible", contest.solve(1, 2));
    }
}