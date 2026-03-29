package tower;

import static org.junit.Assert.*;
import org.junit.Test;

public class TowerC4Test {

    @Test
    public void testNormalCupAndLid() {
        Tower t = new Tower(100, 100);
        t.pushCup(2,"normal");
        t.pushLid(2, "normal");
        assertTrue(t.ok());
        assertEquals(2, t.stackingItems().length);
    }

    @Test
    public void testOpenerCupRemovesLids() {
        Tower t = new Tower(100, 100);
        t.pushCup(2, "normal");
        t.pushLid(2, "normal"); 
        t.pushCup(5, "opener"); 
        assertTrue(t.ok());
        assertEquals(2, t.stackingItems().length); 
    }

    @Test
    public void testHierarchicalCupPushesSmaller() {
        Tower t = new Tower(100, 100);
        t.pushCup(3, "normal");
        t.pushCup(5, "hierarchical"); 
        assertEquals("5", t.stackingItems()[0][1]); 
    }

    @Test
    public void testHierarchicalCupStopsAtLarger() {
        Tower t = new Tower(100, 100);
        t.pushCup(8, "normal");
        t.pushCup(5, "normal"); 
        assertEquals("8", t.stackingItems()[0][1]); 
    }

    @Test
    public void testFearfulLidFailsWithoutCup() {
        Tower t = new Tower(100, 100);
        t.pushLid(3, "fearful"); 
        assertFalse(t.ok()); 
    }

    @Test
    public void testFearfulLidEntersWithCup() {
        Tower t = new Tower(100, 100);
        t.pushCup(3, "normal");
        t.pushLid(3, "fearful"); 
        assertTrue(t.ok()); 
    }

    @Test
    public void testCrazyLidGoesToBottom() {
        Tower t = new Tower(100, 100);
        t.pushCup(5, "normal");
        t.pushLid(5, "crazy");
        assertEquals("lid", t.stackingItems()[0][0]); 
    }

    @Test
    public void testAnchorCupEntersSuccessfully() {
        Tower t = new Tower(100, 100);
        t.pushCup(4, "anchor");
        assertTrue(t.ok());
    }

    @Test
    public void testAnchorCupCannotBeRemoved() {
        Tower t = new Tower(100, 100);
        t.pushCup(4, "anchor");
        t.popCup();
        assertFalse(t.ok());
    }

    @Test
    public void testHierarchicalAtBottomCannotBeRemoved() {
        Tower t = new Tower(100, 100);
        t.pushCup(6, "hierarchical"); 
        t.popCup();
        assertFalse(t.ok()); 
    }
}