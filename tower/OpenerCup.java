package tower;

public class OpenerCup extends Cup {
    public OpenerCup(int id) {
        super(id, "opener");
        applyVisualDistinction();
    }
    
    @Override
    protected void applyVisualDistinction() {
        this.base.changeColor("magenta");
    }

    @Override
    public void onPushed(TowerContext context) {
        
        context.removeAllLids();
    }
}