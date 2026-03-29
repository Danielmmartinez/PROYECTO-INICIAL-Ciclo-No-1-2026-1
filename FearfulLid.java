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
}