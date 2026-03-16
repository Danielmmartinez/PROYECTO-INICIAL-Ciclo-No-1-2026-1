import java.awt.*;

/**
 * Clase abstracta que define el estado y comportamiento comun de todas las figuras.
 * Centraliza la comunicación con el Canvas, la logica de movimiento y las animaciones.
 *
 * @author
 * @version
 */
public abstract class Shape {
    
    // --- Constantes ---
    protected static final int DEFAULT_MOVE_STEP = 20;
    protected static final int ANIMATION_DELAY = 10;
    private static final String DEFAULT_SHAPE_CLASS = "Rectangle";

    // --- Atributos de Estado ---
    protected Canvas canvas; 
    protected int xPosition;
    protected int yPosition;
    protected String color;
    protected boolean isVisible;

    /**
     * Constructor base para todas las figuras.
     * @param x Posición inicial en el eje X.
     * @param y Posición inicial en el eje Y.
     * @param color Color inicial de la figura.
     */
    public Shape(int x, int y, String color) {
        this.xPosition = x;
        this.yPosition = y;
        this.color = color;
        this.isVisible = false;
        this.canvas = Canvas.getCanvas();
    }

    /**
     * Crea una instancia por defecto.
     * Instancia dinámicamente la clase definida en DEFAULT_SHAPE_CLASS.
     * @return Una nueva instancia de Shape.
     */
    public static Shape createShape() {
        try {
            return (Shape) Class.forName(DEFAULT_SHAPE_CLASS).getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            System.err.println("Error: No se pudo encontrar la clase base visual " + DEFAULT_SHAPE_CLASS);
            return null;
        }
    }

    /**
     * Hace visible la figura en el lienzo.
     */
    public void makeVisible() {
        isVisible = true;
        draw();
    }

    /**
     * Oculta la figura del lienzo.
     */
    public void makeInvisible() {
        erase();
        isVisible = false;
    }

    // --- Movimiento Refactorizado ---

    /**
     * @param distance Pixeles a mover en el eje X.
     */
    public void moveHorizontal(int distance) {
        updatePosition(distance, 0);
    }

    /**
     * @param distance Pixeles a mover en el eje Y.
     */
    public void moveVertical(int distance) {
        updatePosition(0, distance);
    }

    /**
     * Mueve la figura a la derecha usando el paso por defecto.
     */
    public void moveRight() {
        moveHorizontal(DEFAULT_MOVE_STEP); 
    }
    
    /**
     * Mueve la figura a la izquierda usando el paso por defecto.
     */
    public void moveLeft()  { 
        moveHorizontal(-DEFAULT_MOVE_STEP); 
    }
    
    /**
     * Mueve la figura hacia arriba usando el paso por defecto.
     */
    public void moveUp()    { 
        moveVertical(-DEFAULT_MOVE_STEP); 
    }
    
    /**
     * Mueve la figura hacia abajo usando el paso por defecto.
     */
    public void moveDown()  { 
        moveVertical(DEFAULT_MOVE_STEP);
    }

    /**
     * Método auxiliar que centraliza la logica de borrar, cambiar posición y redibujar.
     * @param dx Cambio en el eje X.
     * @param dy Cambio en el eje Y.
     */
    private void updatePosition(int dx, int dy) {
        erase();
        xPosition += dx;
        yPosition += dy;
        draw();
    }

    // --- Animaciones ---

    /** Mueve la figura horizontalmente de forma animada (píxel por píxel).
     * @param distance Distancia total a recorrer.
     */
    public void slowMoveHorizontal(int distance) {
        animate(distance, true);
    }

    /** Mueve la figura verticalmente de forma animada (píxel por píxel).
     * @param distance Distancia total a recorrer.
     */
    public void slowMoveVertical(int distance) {
        animate(distance, false);
    }

    /**
     * Logica interna para ejecutar la animacion paso a paso.
     * @param distance Distancia a mover.
     * @param isHorizontal true para eje X, false para eje Y.
     */
    private void animate(int distance, boolean isHorizontal) {
        int delta = (distance < 0) ? -1 : 1;
        int steps = Math.abs(distance);
        
        for (int i = 0; i < steps; i++) {
            if (isHorizontal) xPosition += delta;
            else yPosition += delta;
            draw();
        }
    }

    // --- Atributos Visuales ---

    /**
     * Cambia el color de la figura.
     * @param newColor Nuevo color en formato String soportado por el Canvas.
     */
    public void changeColor(String newColor) {
        this.color = newColor;
        draw();
    }

    /**
     * Dibuja la figura en el Canvas si esta marcada como visible.
     */
    protected void draw() {
        if (isVisible) {
            canvas.draw(this, color, getShapeGeometry());
            canvas.wait(ANIMATION_DELAY);
        }
    }

    /**
     * Borra la figura del Canvas si esta marcada como visible.
     */
    protected void erase() {
        if (isVisible) {
            canvas.erase(this);
        }
    }

    // --- Metodos Abstractos ---

    /**
     * Actualiza las dimensiones de la figura.
     * @param newHeight Nueva altura en pixeles.
     * @param newWidth Nuevo ancho en pixeles.
     */
    public abstract void changeSize(int newHeight, int newWidth);
    
    /**
     * Proporciona el objeto geometrico exacto de Java AWT para ser dibujado.
     * @return Objeto que implementa java.awt.Shape.
     */
    protected abstract java.awt.Shape getShapeGeometry();
}