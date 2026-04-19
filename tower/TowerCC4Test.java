package tower;

import static org.junit.Assert.*;
import org.junit.Test;

public class TowerCC4Test {

    @Test
    public void shouldLidCannotBePoppedIfCovering() {
        Tower t = new Tower(100, 100);
        t.pushCup("normal", 4);
        t.pushLid("fearful", 4);
        t.popLid();
        assertFalse(t.ok()); 
    }

    @Test
    public void shouldMultipleCrazyLidsOrderAtBottom() {
        Tower t = new Tower(100, 100);
        t.pushCup("normal", 5);
        t.pushLid("crazy", 1);
        t.pushLid("crazy", 2);
        assertEquals("2", t.stackingItems()[0][1]);
        assertEquals("1", t.stackingItems()[1][1]);
    }

    @Test
    public void shouldOpenerCupRemovesMultipleLids() {
        Tower t = new Tower(100, 100);
        t.pushCup("normal", 1);
        t.pushLid("normal", 1);
        t.pushCup("normal", 2);
        t.pushLid("normal", 2);
        t.pushCup("opener", 8); 
        assertEquals(3, t.stackingItems().length); 
    }

    @Test
    public void shouldAnchorStopsHierarchicalPush() {
        Tower t = new Tower(100, 100);
        t.pushCup("anchor", 2);
        t.pushCup("hierarchical", 5); 
        assertEquals("2", t.stackingItems()[0][1]);
    }

    @Test
    public void shouldHierarchicalPushesCrazyLids() {
        Tower t = new Tower(100, 100);
        t.pushCup("normal", 3);
        t.pushLid("crazy", 1); 
        t.pushCup("hierarchical", 5); 
        assertEquals("5", t.stackingItems()[0][1]); 
    }

    @Test
    public void shouldFearfulLidSpecificRemoveFails() {
        Tower t = new Tower(100, 100);
        t.pushCup("normal", 7);
        t.pushLid("fearful", 7);
        t.removeLid(7);
        assertFalse(t.ok());
    }

    @Test
    public void shouldOpenerCupInEmptyTower() {
        Tower t = new Tower(100, 100);
        t.pushCup("opener", 4); 
        assertTrue(t.ok());
    }

    @Test
    public void shouldAnchorCupSpecificRemoveFails() {
        Tower t = new Tower(100, 100);
        t.pushCup("anchor", 3);
        t.pushCup("normal", 5);
        t.removeCup(3); 
        assertFalse(t.ok());
    }

    @Test
    public void shouldFearfulLidDoesNotAffectOthers() {
        Tower t = new Tower(100, 100);
        t.pushCup("normal", 2);
        t.pushLid("normal", 2);
        t.pushLid("fearful", 9); 
        assertTrue(t.stackingItems().length == 2); 
    }

    @Test
    public void shouldHierarchicalMovesCorrectAmountOfSteps() {
        Tower t = new Tower(100, 100);
        t.pushCup("normal", 9);
        t.pushCup("normal", 2);
        t.pushCup("hierarchical", 5); 
        assertEquals("9", t.stackingItems()[0][1]);
        assertEquals("5", t.stackingItems()[1][1]);
        assertEquals("2", t.stackingItems()[2][1]);
    }
}