package tower;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Pruebas unitarias para Tower.
 */
public class TowerC2Test {


    @Test
    public void shouldCreateTowerWithNCups() {
        Tower t = new Tower(4);
        
        assertTrue(t.ok());
        assertEquals(4, t.stackingItems().length);
    }

    @Test
    public void shouldSwapTwoExistingObjects() {
        Tower t = new Tower(2); 
        
        
        t.swap(
            new String[]{"cup", "1"}, 
            new String[]{"cup", "2"}
        );
        
        assertTrue(t.ok());
        
        
        String[][] items = t.stackingItems();
        assertEquals("2", items[0][1]); 
        assertEquals("1", items[1][1]);
    }

    @Test
    public void shouldCoverCupsWithMatchingLids() {
        Tower t = new Tower(100, 100);
        
        t.pushCup(1);
        t.pushCup(2);
        
        
        t.pushLid(1);
        
        t.cover();
        
        
        assertEquals(2, t.lidedCups().length);
        assertTrue(t.ok());
    }

    @Test
    public void shouldFindSwapToReduceHeight() {
        Tower t = new Tower(1000, 1000);
        
        
        t.pushCup(1);
        t.pushCup(5);
        
        
        String[][] recommendation = t.swapToReduce();
        
        assertEquals(2, recommendation.length);
        assertTrue(t.ok());
    }

    @Test
    public void shouldNotSwapNonExistentObjects() {
        Tower t = new Tower(2); 
        
        
        t.swap(
            new String[]{"cup", "1"}, 
            new String[]{"cup", "99"}
        );
        
        
        assertFalse(t.ok());
    }

    @Test
    public void shouldNotFailWhenCoveringWithoutLids() {
        Tower t = new Tower(100, 100);
        
        t.pushCup(1);
        t.pushCup(2);
        t.pushCup(3);
        
        
        t.cover();
        
        
        assertEquals(3, t.lidedCups().length);
        assertTrue(t.ok());
    }

}
