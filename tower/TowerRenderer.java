package tower;

import shapes.Canvas;
import shapes.Shape;
import java.util.ArrayList;
import java.util.List;
import shapes.Rectangle;

/**
 * Motor encargado exclusivamente de la representación gráfica.
 * Cumple con SRP aislando la lógica visual de la lógica de negocio.
 */
public class TowerRenderer {
    
    private final Canvas simulationCanvas;
    private final List<Shape> rulerMarks = new ArrayList<>();
    private final int maxHeight;
    private static final int RULER_MARK_GAP = 10;

    public TowerRenderer(int maxHeight) {
        this.simulationCanvas = Canvas.getCanvas();
        this.maxHeight = maxHeight;
    }

    /**
     * Actualiza la visibilidad de los elementos apilados manteniendo el orden Z (capas).
     */
    public void updateView(List<TowerElement> elements, boolean isVisible) {
        if (!isVisible) {
            return;
        }
        elements.forEach(TowerElement::makeInvisible);
        
        // PURE OCP: Ordenamos por capa de renderizado (Fondo primero, Frente después)
        elements.stream()
                .sorted(java.util.Comparator.comparingInt(TowerElement::getRenderLayer))
                .forEach(TowerElement::makeVisible);
    }

    public void makeVisible(List<TowerElement> elements) {
        simulationCanvas.setVisible(true);
        drawRuler();
        updateView(elements, true);
    }

    public void makeInvisible(List<TowerElement> elements) {
        elements.forEach(TowerElement::makeInvisible);
        rulerMarks.forEach(Shape::makeInvisible);
    }

    /**
     * Dibuja la regla de medición a la izquierda de la torre.
     */
    private void drawRuler() {
        rulerMarks.forEach(Shape::makeInvisible);
        rulerMarks.clear();

        int floorY = simulationCanvas.getHeight() * 3 / 4;
        for (int i = 0; i <= maxHeight; i++) {
            Shape mark = new Rectangle();
            mark.changeSize(1, (i % 5 == 0) ? 15 : 8);
            mark.changeColor("black");
            mark.moveHorizontal(-40);
            mark.moveVertical(floorY - (i * RULER_MARK_GAP) - 15);
            mark.makeVisible();
            rulerMarks.add(mark);
        }
    }
    
    // Métodos para que las físicas sepan las dimensiones del mundo
    public int getCanvasWidth() { return simulationCanvas.getWidth(); }
    public int getCanvasHeight() { return simulationCanvas.getHeight(); }
}