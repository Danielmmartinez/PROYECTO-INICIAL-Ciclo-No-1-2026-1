package tower;


/**
 * New proposed option by, us?, the anchor Cup, a Heavy heavy Cup. If you create it in the canvas
 * the Anchor Cup will go to the bottom of the Canvas and it won't move by any chance...
 */
public class AnchorCup extends Cup
{

    /**
     * Constructor for objects of class AnchorCup
     */
    public AnchorCup(int id){
        super(id,"anchor");
        applyVisualDistinction();
    }

    @Override
    protected void applyVisualDistinction() {
        this.base.changeColor("black");
        this.leftWall.changeColor("red");
        this.rightWall.changeColor("red");
        
    }
}