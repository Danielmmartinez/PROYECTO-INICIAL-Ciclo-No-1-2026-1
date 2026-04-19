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

    // --- MAGIA OCP ---
    @Override
    public void onPushed(TowerContext context) {
        // La taza misma le pide a la torre que quite las tapas
        context.removeAllLids();
    }
}