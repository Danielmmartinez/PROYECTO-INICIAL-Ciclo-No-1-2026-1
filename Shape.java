import java.awt.*;

/**
 * Clase abstracta que define el comportamiento común de todas las figuras.
 * Centraliza la comunicación con el Canvas y la lógica de movimiento.
 */
public abstract class Shape {
    
    // Constantes para evitar numeros magicos
    protected static final int DEFAULT_MOVE_STEP = 20;
    protected static final int ANIMATION_DELAY = 10;

    protected Canvas canvas; 
    protected int xPosition;
    protected int yPosition;
    protected String color;
    protected boolean isVisible;

    public Shape(int x, int y, String color) {
        this.xPosition = x;
        this.yPosition = y;
        this.color = color;
        this.isVisible = false;
        this.canvas = Canvas.getCanvas();
    }

    /**
     * METODO FABRICA: Crea una instancia de Rectangle por defecto.
     * Se usa el literal de clase para mayor seguridad en tiempo de compilación.
     */
    public static Shape createShape() {
        try {
            return (Shape) Class.forName("Rectangle").getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            System.err.println("Error: No se pudo encontrar la clase Rectangle.");
            return null;
        }
    }

    // --- Control de Visibilidad ---

    public void makeVisible() {
        isVisible = true;
        draw();
    }

    public void makeInvisible() {
        erase();
        isVisible = false;
    }

    // --- Movimiento Refactorizado ---

    public void moveHorizontal(int distance) {
        updatePosition(distance, 0);
    }

    public void moveVertical(int distance) {
        updatePosition(0, distance);
    }

    public void moveRight() {
        moveHorizontal(DEFAULT_MOVE_STEP); 
    }
    public void moveLeft()  {
        moveHorizontal(-DEFAULT_MOVE_STEP); 
    }
    public void moveUp()    {
        moveVertical(-DEFAULT_MOVE_STEP); 
    }
    public void moveDown()  {
        moveVertical(DEFAULT_MOVE_STEP); 
    }

    /**
     * Centraliza la logica de borrar, cambiar posicion y redibujar.
     */
    private void updatePosition(int dx, int dy) {
        erase();
        xPosition += dx;
        yPosition += dy;
        draw();
    }

    // --- Animaciones ---

    public void slowMoveHorizontal(int distance) {
        animate(distance, true);
    }

    public void slowMoveVertical(int distance) {
        animate(distance, false);
    }

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

    public void changeColor(String newColor) {
        this.color = newColor;
        draw();
    }

    protected void draw() {
        if (isVisible) {
            canvas.draw(this, color, getShapeGeometry());
            canvas.wait(ANIMATION_DELAY);
        }
    }

    protected void erase() {
        if (isVisible) {
            canvas.erase(this);
        }
    }

    // --- Metodos Abstractos ---

    public abstract void changeSize(int newHeight, int newWidth);
    protected abstract java.awt.Shape getShapeGeometry();
}