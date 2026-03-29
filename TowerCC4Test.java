package tower;

import static org.junit.Assert.*;
import org.junit.Test;

public class TowerCC4Test {

    @Test
    public void testFearfulLidCannotBePoppedIfCovering() {
        Tower t = new Tower(100, 100);
        t.pushCup(4, "normal");
        t.pushLid(4, "fearful");
        t.popLid();
        assertFalse(t.ok()); 
    }

    @Test
    public void testMultipleCrazyLidsOrderAtBottom() {
        Tower t = new Tower(100, 100);
        t.pushCup(5, "normal");
        t.pushLid(1, "crazy");
        t.pushLid(2, "crazy");
        assertEquals("2", t.stackingItems()[0][1]);
        assertEquals("1", t.stackingItems()[1][1]);
    }

    @Test
    public void testOpenerCupRemovesMultipleLids() {
        Tower t = new Tower(100, 100);
        t.pushCup(1, "normal");
        t.pushLid(1, "normal");
        t.pushCup(2, "normal");
        t.pushLid(2, "normal");
        t.pushCup(8, "opener"); 
        assertEquals(3, t.stackingItems().length); 
    }

    @Test
    public void testAnchorStopsHierarchicalPush() {
        Tower t = new Tower(100, 100);
        t.pushCup(2, "anchor");
        t.pushCup(5, "hierarchical"); 
        assertEquals("2", t.stackingItems()[0][1]);
    }

    @Test
    public void testHierarchicalPushesCrazyLids() {
        Tower t = new Tower(100, 100);
        t.pushCup(3, "normal");
        t.pushLid(1, "crazy"); 
        t.pushCup(5, "hierarchical"); 
        assertEquals("5", t.stackingItems()[0][1]); 
    }

    @Test
    public void testFearfulLidSpecificRemoveFails() {
        Tower t = new Tower(100, 100);
        t.pushCup(7, "normal");
        t.pushLid(7, "fearful");
        t.removeLid(7);
        assertFalse(t.ok());
    }

    @Test
    public void testOpenerCupInEmptyTower() {
        Tower t = new Tower(100, 100);
        t.pushCup(4, "opener"); 
        assertTrue(t.ok());
    }

    @Test
    public void testAnchorCupSpecificRemoveFails() {
        Tower t = new Tower(100, 100);
        t.pushCup(3, "anchor");
        t.pushCup(5, "normal");
        t.removeCup(3); 
        assertFalse(t.ok());
    }

    @Test
    public void testFearfulLidDoesNotAffectOthers() {
        Tower t = new Tower(100, 100);
        t.pushCup(2, "normal");
        t.pushLid(2, "normal");
        t.pushLid(9, "fearful"); 
        assertTrue(t.stackingItems().length == 2); 
    }

    @Test
    public void testHierarchicalMovesCorrectAmountOfSteps() {
        Tower t = new Tower(100, 100);
        t.pushCup(9, "normal");
        t.pushCup(2, "normal");
        t.pushCup(5, "hierarchical"); 
        assertEquals("9", t.stackingItems()[0][1]);
        assertEquals("5", t.stackingItems()[1][1]);
        assertEquals("2", t.stackingItems()[2][1]);
    }
}