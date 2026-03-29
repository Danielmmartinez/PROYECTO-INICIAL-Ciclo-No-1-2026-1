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
    protected void updateKnobVisibility() {
        if (isVisible) {
            knob.makeVisible();
        } else {
            knob.makeInvisible();
        }
    }
}