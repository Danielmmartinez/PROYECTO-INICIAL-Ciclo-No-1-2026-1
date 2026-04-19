package tower;

import static org.junit.Assert.*;
import org.junit.Test;

public class TowerContestCTest {

    @Test
    public void shouldRejectNegativeOrZeroHeights() {
        assertEquals("impossible", TowerContest.solve(4, 0));
        assertEquals("impossible", TowerContest.solve(4, -5));
    }

    @Test
    public void shouldTriggerAntiFreezeProtection() {
        assertEquals("impossible_too_large", TowerContest.solve(15, 30));
    }
    
    @Test
    public void shouldFindAbsoluteMinimumHeightForN4() {
        assertEquals("7 5 3 1", TowerContest.solve(4, 7));
    }
}