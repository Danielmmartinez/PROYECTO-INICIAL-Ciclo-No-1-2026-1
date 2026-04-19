package tower;

import java.util.List;

public interface TowerElement {
    int getId();
    String getType();
    String getColor();
    void setColor(String color);
    
    // --- Identidad Universal ---
    String getCategory(); 
    int getRenderLayer(); 
    
    // --- Comportamiento Polimórfico ---
    boolean canBePushed(TowerContext context);
    boolean canBeRemoved(TowerContext context);
    int getInsertionIndex(TowerContext context);
    void onPushed(TowerContext context);
    void onRemoved(TowerContext context);
    
    // --- Físicas y Estado (Sin atajos) ---
    void makeVisible();
    void makeInvisible();
    void setPosition(int x, int y);
    int[] getDimensions();
    
    /** Retorna el grosor de las paredes (0 para tapas, 10 para tazas) */
    int getWallThickness();
    
    /** Calcula dinámicamente dónde debe chocar un objeto que cae sobre este */
    int getDropObstacleY(int fallingObjWidth, int myTopY, int myBaseY);
    
    /** El elemento evalúa su entorno y decide su estado (ej: la tapa decide si mostrar la perilla) */
    void updateContextualState(List<TowerElement> belowElements);
}