package tower;


/**
 * Make a Cup that delete all the Lids in his way.
 */
public class OpenerCup extends Cup {
    public OpenerCup(int id) {
        super(id, "opener");
        applyVisualDistinction();
    }
    
    @Override
    protected void applyVisualDistinction() {
        this.base.changeColor("magenta");
    }
}
