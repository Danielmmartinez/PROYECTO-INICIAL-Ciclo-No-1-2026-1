package tower;

public class NormalLid extends Lid {
    public NormalLid(int id) {
        super(id, "normal");
        applyVisualDistinction();
    }
    
    @Override
    protected void applyVisualDistinction() {
    }
}