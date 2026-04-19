package tower;

import static org.junit.Assert.*;
import org.junit.Test;

public class TowerC4Test {

    @Test
    public void shouldNormalCupAndLid() {
        Tower t = new Tower(100, 100);
        t.pushCup("normal", 2);
        t.pushLid("normal", 2);
        assertTrue(t.ok());
        assertEquals(2, t.stackingItems().length);
    }

    @Test
    public void shouldOpenerCupRemovesLids() {
        Tower t = new Tower(100, 100);
        t.pushCup("normal", 2);
        t.pushLid("normal", 2); 
        t.pushCup("opener", 5); 
        assertTrue(t.ok());
        assertEquals(2, t.stackingItems().length); 
    }

    @Test
    public void shouldHierarchicalCupPushesSmaller() {
        Tower t = new Tower(100, 100);
        t.pushCup("normal", 3);
        t.pushCup("hierarchical", 5); 
        assertEquals("5", t.stackingItems()[0][1]); 
    }

    @Test
    public void shouldHierarchicalCupStopsAtLarger() {
        Tower t = new Tower(100, 100);
        t.pushCup("normal", 8);
        t.pushCup("normal", 5); 
        assertEquals("8", t.stackingItems()[0][1]); 
    }

    @Test
    public void shouldFearfulLidFailsWithoutCup() {
        Tower t = new Tower(100, 100);
        t.pushLid("fearful", 3); 
        assertFalse(t.ok()); 
    }

    @Test
    public void shouldFearfulLidEntersWithCup() {
        Tower t = new Tower(100, 100);
        t.pushCup("normal", 3);
        t.pushLid("fearful", 3); 
        assertTrue(t.ok()); 
    }

    @Test
    public void shouldCrazyLidGoesToBottom() {
        Tower t = new Tower(100, 100);
        t.pushCup("normal", 5);
        t.pushLid("crazy", 5);
        assertEquals("lid", t.stackingItems()[0][0]); 
    }

    @Test
    public void shouldAnchorCupEntersSuccessfully() {
        Tower t = new Tower(100, 100);
        t.pushCup("anchor", 4);
        assertTrue(t.ok());
    }

    @Test
    public void shouldAnchorCupCannotBeRemoved() {
        Tower t = new Tower(100, 100);
        t.pushCup("anchor", 4);
        t.popCup();
        assertFalse(t.ok());
    }

    @Test
    public void shouldHierarchicalAtBottomCannotBeRemoved() {
        Tower t = new Tower(100, 100);
        t.pushCup("hierarchical", 6); 
        t.popCup();
        assertFalse(t.ok()); 
    }
}