package tower;

public class FearfulLid extends Lid {
    public FearfulLid(int id) {
        super(id, "fearful");
        applyVisualDistinction();
    }

    @Override
    protected void applyVisualDistinction() {
        this.knob.changeColor("white");
    }

    @Override
    public boolean canBePushed(TowerContext context) {
        if (!context.hasCup(this.id)) {
            context.reportError("La tapa miedosa " + id + " no entra porque su taza compañera no está.");
            return false;
        }
        return true;
    }

    @Override
    public boolean canBeRemoved(TowerContext context) {
        if (context.hasCup(this.id)) {
            context.reportError("La tapa miedosa no quiere salir, protege a su taza.");
            return false;
        }
        return true;
    }
}