package tower;
import shapes.Shape;
import shapes.Rectangle;
import java.util.List;

/**
 * Representa la tapa de una taza con un indicador visual (perilla) 
 * cuando está cubriendo.
 * Ahora implementa TowerElement para cumplir con el contrato OCP.
 */
public abstract class Lid implements TowerElement {
    
    // --- Constantes ---
    protected static final int INITIAL_X = 70;
    protected static final int INITIAL_Y = 15;
    protected static final int LID_HEIGHT = 6; // Altura física de la tapa
    protected static final int WIDTH_MULTIPLIER = 20;
    protected static final int KNOB_WIDTH = 10;
    protected static final int KNOB_HEIGHT = 6;
    protected static final String DEFAULT_COLOR = "black";
    
    // --- Atributos de estado e identidad ---
    protected final int id;
    protected final String type;
    protected int xCurrent;
    protected int yCurrent;
    protected boolean isVisible;
    protected boolean isCoveringState;
    
    // --- Atributos visuales ---
    protected final Shape visual;
    protected final Shape knob;
    
    public Lid(int id, String type) {
        this.id = id;
        this.type = type;
        this.visual = new Rectangle();
        this.knob = new Rectangle();
        
        this.xCurrent = INITIAL_X;
        this.yCurrent = INITIAL_Y;

        visual.changeColor(DEFAULT_COLOR);
        knob.changeColor(DEFAULT_COLOR); 
        
        visual.changeSize(LID_HEIGHT, id * WIDTH_MULTIPLIER);
        knob.changeSize(KNOB_HEIGHT, KNOB_WIDTH); 
        
        int halfTapaWidth = id * (WIDTH_MULTIPLIER / 2);
        int halfKnobWidth = KNOB_WIDTH / 2;
        
        knob.moveHorizontal(halfTapaWidth - halfKnobWidth);
        knob.moveVertical(-KNOB_HEIGHT); 

        this.isCoveringState = false;
        updateKnobVisibility();
    }
    
    // --- IMPLEMENTACIÓN DE TowerElement (COMPORTAMIENTO POR DEFECTO) ---

    @Override
    public int getId() { return id; }
    
    @Override
    public String getType() { return type; }

    @Override
    public boolean canBePushed(TowerContext context) {
        return true; // Por defecto todas las tapas pueden entrar
    }

    @Override
    public boolean canBeRemoved(TowerContext context) {
        return true; // Por defecto todas las tapas se pueden quitar
    }

    @Override
    public int getInsertionIndex(TowerContext context) {
        // Por defecto va al final de la torre
        return context.getStackedElements().size();
    }

    @Override
    public void onPushed(TowerContext context) {}

    @Override
    public void onRemoved(TowerContext context) {}

    @Override
    public int[] getDimensions() {
        return new int[]{ id * WIDTH_MULTIPLIER, LID_HEIGHT };
    }
    
    @Override
    public String getColor() { return DEFAULT_COLOR; } 
    
    @Override
    public void setColor(String color) { 
        visual.changeColor(color); 
        knob.changeColor(color);
        applyVisualDistinction();
    }

    // --- MÉTODOS VISUALES ORIGINALES ---

    @Override
    public void setPosition(int newX, int newY) {
        int dx = newX - xCurrent;
        int dy = newY - yCurrent;
        visual.moveHorizontal(dx);
        visual.moveVertical(dy);
        knob.moveHorizontal(dx);
        knob.moveVertical(dy);
        this.xCurrent = newX;
        this.yCurrent = newY;
    }
    
    @Override
    public void makeVisible() { 
        isVisible = true;
        visual.makeVisible(); 
        updateKnobVisibility(); 
    }
    
    @Override
    public void makeInvisible() { 
        isVisible = false;
        visual.makeInvisible(); 
        knob.makeInvisible();
    }
    
    
    // --- POLIMORFISMO PURO  ---

    @Override
    public String getCategory() { return "lid"; }

    @Override
    public int getRenderLayer() { return 1; } 

    @Override
    public int getWallThickness() { return 0; } 

    @Override
    public void updateContextualState(List<TowerElement> belowElements) {
        boolean covering = belowElements.stream()
                .anyMatch(e -> e.getId() == this.id && "cup".equals(e.getCategory()));
        this.isCoveringState = covering;
        updateKnobVisibility();
    }

    @Override
    public int getDropObstacleY(int fallingObjWidth, int myTopY, int myBaseY) {
        return myTopY;
    }
    
    protected void updateKnobVisibility() {
        if (isVisible && isCoveringState) {
            knob.makeVisible();
        } else {
            knob.makeInvisible();
        }
    }
    
    protected abstract void applyVisualDistinction();
}