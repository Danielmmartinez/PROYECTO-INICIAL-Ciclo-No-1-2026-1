import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Pruebas colectivas para TowerContest.
 */
public class TowerContestCTest {

    /**
     * Prueba propuesta para el repositorio colectivo.
     */
    @Test
    public void ShouldRejectNegativeOrZeroHeights() {
        TowerContest contest = new TowerContest();
        
        assertEquals("impossible", contest.solve(4, 0));
        assertEquals("impossible", contest.solve(4, -5));
    }

    /**
     * Prueba propuesta para el repositorio colectivo.
     * Valida que se active el límite de seguridad (anti-freeze) 
     */
    @Test
    public void ShouldTriggerAntiFreezeProtection() {
        TowerContest contest = new TowerContest();
        
        assertEquals("impossible_too_large", contest.solve(15, 30));
    }
    
    /**
     * Prueba propuesta para el repositorio colectivo.
     * 
     */
    @Test
    public void ShouldFindAbsoluteMinimumHeightForN4() {
        TowerContest contest = new TowerContest();
        
        assertEquals("7 5 3 1", contest.solve(4, 7));
    }
}