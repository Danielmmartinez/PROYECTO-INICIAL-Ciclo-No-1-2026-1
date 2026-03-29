package tower;

/**
 * Make a Cup that go all the way down moving minor objects (cups), if it get all the way down 
 * it didn't gonna move anywhere
 */
public class HierarchicalCup extends Cup
{
    public HierarchicalCup (int id) {
        super(id, "hierarchical");
        applyVisualDistinction();
    }
    
    @Override
    protected void applyVisualDistinction(){
        this.leftWall.changeColor("black");
        this.rightWall.changeColor("black");
    }
}