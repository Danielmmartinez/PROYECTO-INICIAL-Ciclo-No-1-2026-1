package tower;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Propuesta de prueba colectiva para TowerCC2Test.
 */
public class TowerCC2Test {

    /**
     * Prueba propuesta para el repositorio colectivo.
     * Verifica que si la torre ya está en su altura mínima óptima,
     * swapToReduce() no recomiende ningún movimiento inválido.
     */
    @Test
    public void ShouldReturnEmptyIfHeightIsOptimal() {
        Tower t = new Tower(100, 1000);
        
        t.pushCup(5);
        t.pushCup(1);
        
        
        String[][] recommendation = t.swapToReduce();
        
        
        assertEquals(0, recommendation.length);
        assertTrue(t.ok());
    }

    @Test
    public void shouldNotPushDuplicateCup() {
        Tower t = new Tower(100, 100);
        t.pushCup(1);
        assertTrue(t.ok());
        
        
        t.pushCup(1);
        assertFalse(t.ok()); 
    }

    @Test
    public void shouldNotPushDuplicateLid() {
        Tower t = new Tower(100, 100);
        t.pushLid(1);
        assertTrue(t.ok());
        
        
        t.pushLid(1);
        assertFalse(t.ok());
    }

    @Test
    public void shouldPopCupCorrectly() {
        Tower t = new Tower(2); 
        assertEquals(2, t.stackingItems().length);
        
        t.popCup(); 
        assertTrue(t.ok());
        assertEquals(1, t.stackingItems().length);
        assertEquals("1", t.stackingItems()[0][1]); 
    }

    @Test
    public void shouldFailPopCupOnEmptyTower() {
        Tower t = new Tower(100, 100); 
        t.popCup();
        assertFalse(t.ok()); 
    }


    @Test
    public void shouldRemoveSpecificCup() {
        Tower t = new Tower(3); 
        
        t.removeCup(2); 
        assertTrue(t.ok());
        
        String[][] items = t.stackingItems();
        assertEquals(2, items.length);
        assertEquals("1", items[0][1]); 
        assertEquals("3", items[1][1]); 
    }

    @Test
    public void shouldFailToRemoveNonExistentCup() {
        Tower t = new Tower(2);
        
        t.removeCup(5); 
        assertFalse(t.ok());
    }


    @Test
    public void shouldOrderTowerCorrectly() {
        Tower t = new Tower(100, 100);
        
        t.pushCup(3);
        t.pushCup(1);
        t.pushCup(5);
        
        t.orderTower();
        assertTrue(t.ok());
        
        
        String[][] items = t.stackingItems();
        assertEquals("5", items[0][1]);
        assertEquals("3", items[1][1]);
        assertEquals("1", items[2][1]);
    }

    @Test
    public void shouldReverseTower() {
        Tower t = new Tower(100, 100);
        t.pushCup(1);
        t.pushCup(2);
        t.pushCup(3);
        
        t.reverseTower();
        assertTrue(t.ok());
        
        
        String[][] items = t.stackingItems();
        assertEquals("3", items[0][1]);
        assertEquals("2", items[1][1]);
        assertEquals("1", items[2][1]);
    }
}
