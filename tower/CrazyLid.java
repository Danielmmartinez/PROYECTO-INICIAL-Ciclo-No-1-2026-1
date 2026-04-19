package tower;

public class CrazyLid extends Lid {
    public CrazyLid(int id) {
        super(id, "crazy");
        applyVisualDistinction();
    }

    @Override
    protected void applyVisualDistinction() {
        this.knob.changeColor("yellow");
    }

    @Override
    protected void updateKnobVisibility() {
        if (isVisible) {
            knob.makeVisible();
        } else {
            knob.makeInvisible();
        }
    }

    // --- MAGIA OCP ---
    @Override
    public int getInsertionIndex(TowerContext context) {
        return 0; // Siempre fuerza su inserción en el índice 0
    }
}