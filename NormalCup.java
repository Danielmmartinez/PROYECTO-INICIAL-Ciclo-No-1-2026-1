package tower;


/**
 * Make a Normal cup without any distinction
 */
public class NormalCup extends Cup {
    
    public NormalCup(int id) {
        super(id, "normal");
        applyVisualDistinction();
    }
    
    @Override
    protected void applyVisualDistinction(){
        
    }
}