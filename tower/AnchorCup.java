package tower;

public class AnchorCup extends Cup {
    public AnchorCup(int id) {
        super(id, "anchor");
        applyVisualDistinction();
    }

    @Override
    protected void applyVisualDistinction() {
        this.base.changeColor("black");
        this.leftWall.changeColor("red");
        this.rightWall.changeColor("red");
    }

    // --- MAGIA OCP ---
    @Override
    public boolean canBeRemoved(TowerContext context) {
        context.reportError("¡La taza Ancla es inamovible!");
        return false; // Bloquea la eliminación
    }
}