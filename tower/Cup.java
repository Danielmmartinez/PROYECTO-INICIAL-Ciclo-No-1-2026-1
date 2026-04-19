package tower;
import shapes.Shape;
import shapes.Rectangle;
import java.util.List;

/**
 * Representa una taza hueca con geometría perfecta para anidamiento.
 * Gestiona su propia representación visual mediante tres rectángulos.
 * Ahora implementa TowerElement para cumplir con el contrato OCP.
 */
public abstract class Cup implements TowerElement {
    
    //------Constantes------------
    protected static final int WALL_THICKNESS = 10;
    protected static final int WIDTH_MULTIPLIER = 20;
    protected static final int HEIGHT_MULTIPLIER = 10;
    protected static final int INITIAL_X = 70;
    protected static final int INITIAL_Y = 15;
    
    //-----Colores----------------
    protected static final String[] COLORS = {
        "red", "blue", "green", "yellow", "magenta", "black"
    };
    
    // --- Atributos de estado ---
    protected final int id;
    protected final int height;
    protected String color;
    protected final String type;
    
    // --- Atributos visuales ---
    protected final Shape base;
    protected final Shape leftWall;
    protected final Shape rightWall;
    protected int xCurrent;
    protected int yCurrent;

    public Cup(int id, String type) {
        this.id = id;
        this.type = type;
        this.height = (2 * id) - 1;
        this.color = pickColor(id);

        this.base = new Rectangle();
        this.leftWall = new Rectangle();
        this.rightWall = new Rectangle();

        setupDimensions();
        
        this.base.moveVertical((this.height * WALL_THICKNESS) - WALL_THICKNESS);
        this.rightWall.moveHorizontal((id * WIDTH_MULTIPLIER) - WALL_THICKNESS);
        
        this.xCurrent = INITIAL_X; 
        this.yCurrent = INITIAL_Y; 
    }
    
    protected void setupDimensions() {
        int currentWidth = id * WIDTH_MULTIPLIER;
        int currentHeightPx = height * WALL_THICKNESS;

        base.changeSize(WALL_THICKNESS, currentWidth); 
        leftWall.changeSize(currentHeightPx, WALL_THICKNESS);
        rightWall.changeSize(currentHeightPx, WALL_THICKNESS);

        base.changeColor(color);
        leftWall.changeColor(color);
        rightWall.changeColor(color);
    }

    // --- IMPLEMENTACIÓN DE TowerElement (COMPORTAMIENTO POR DEFECTO) ---

    @Override
    public int getId() { return id; }
    
    @Override
    public String getType() { return type; }

    @Override
    public boolean canBePushed(TowerContext context) { 
        return true; 
    }

    @Override
    public boolean canBeRemoved(TowerContext context) { 
        return true; 
    }

    @Override
    public int getInsertionIndex(TowerContext context) {
        return context.getStackedElements().size();
    }

    @Override
    public void onPushed(TowerContext context) {}

    @Override
    public void onRemoved(TowerContext context) {}

    @Override
    public int[] getDimensions() {
        return new int[]{ id * WIDTH_MULTIPLIER, height * HEIGHT_MULTIPLIER };
    }
    
    @Override
    public String getColor() { return color; }
    
    @Override
    public void setColor(String color) {
        this.color = color;
        base.changeColor(color);
        leftWall.changeColor(color);
        rightWall.changeColor(color);
    }

    // --- MÉTODOS VISUALES ORIGINALES ---

    @Override
    public void setPosition(int newX, int newY) {
        int dx = newX - xCurrent;
        int dy = newY - yCurrent;
        leftWall.moveHorizontal(dx);
        leftWall.moveVertical(dy);
        base.moveHorizontal(dx);
        base.moveVertical(dy);
        rightWall.moveHorizontal(dx);
        rightWall.moveVertical(dy);
        this.xCurrent = newX;
        this.yCurrent = newY;
    }

    @Override
    public void makeVisible() {
        base.makeVisible();
        leftWall.makeVisible();
        rightWall.makeVisible();
    }

    @Override
    public void makeInvisible() {
        base.makeInvisible();
        leftWall.makeInvisible();
        rightWall.makeInvisible();
    }
    
    
    // --- POLIMORFISMO PURO  ---

    @Override
    public String getCategory() { return "cup"; }

    @Override
    public int getRenderLayer() { return 0; } 

    @Override
    public int getWallThickness() { return WALL_THICKNESS; }

    @Override
    public void updateContextualState(List<TowerElement> belowElements) {
    }

    @Override
    public int getDropObstacleY(int fallingObjWidth, int myTopY, int myBaseY) {
        int innerGap = (id * WIDTH_MULTIPLIER) - (WALL_THICKNESS * 2);
        return (fallingObjWidth <= innerGap) ? myBaseY : myTopY;
    }
    
    public int getHeight() { return height; }

    protected String pickColor(int i) { return COLORS[i % COLORS.length]; }
    
    protected abstract void applyVisualDistinction();
}