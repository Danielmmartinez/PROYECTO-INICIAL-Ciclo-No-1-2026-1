package tower;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TowerPhysics {

    private static final int HEIGHT_MULT = 10;

    public int calculatePhysics(List<TowerElement> elements, int canvasWidth, int canvasHeight, boolean applyToView) {
        final int floorY = canvasHeight * 3 / 4;
        int highestY = floorY;

        Map<TowerElement, Integer> topYMap = new HashMap<>();
        Map<TowerElement, Integer> baseYMap = new HashMap<>();
        List<TowerElement> placed = new ArrayList<>();

        for (TowerElement obj : elements) {
            int[] dims = obj.getDimensions();
            int w = dims[0];
            int h = dims[1];

            if (applyToView) {
                // PURE OCP: El objeto se actualiza solo (ej. tapas activando perillas)
                obj.updateContextualState(placed);
            }

            int dropY = findDropY(w, placed, topYMap, baseYMap, floorY);
            int finalY = dropY - h;
            
            placed.add(obj);
            topYMap.put(obj, finalY);
            
            // Calculamos la base interna polimórficamente sin saber si es taza o tapa
            baseYMap.put(obj, finalY + h - obj.getWallThickness());

            if (finalY < highestY) {
                highestY = finalY;
            }
            
            if (applyToView) {
                int centerX = canvasWidth / 2;
                int xPos = centerX - (w / 2);
                obj.setPosition(xPos, finalY);
            }
        }
        
        return (floorY - highestY) / HEIGHT_MULT;
    }

    public boolean exceedsMaxHeight(List<TowerElement> elements, int maxHeight, int canvasWidth, int canvasHeight) {
        return calculatePhysics(elements, canvasWidth, canvasHeight, false) > maxHeight;
    }

    private int findDropY(int fallingWidth, List<TowerElement> placed, Map<TowerElement, Integer> topYMap, Map<TowerElement, Integer> baseYMap, int floorY) {
        int dropY = floorY;
        
        for (TowerElement p : placed) {
            // El objeto obstáculo determina polimórficamente dónde detener al objeto que cae
            int obstacleY = p.getDropObstacleY(fallingWidth, topYMap.get(p), baseYMap.get(p));
            if (obstacleY < dropY) {
                dropY = obstacleY;
            }
        }
        return dropY;
    }
}