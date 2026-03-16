import java.awt.*;

/**
 * Representa un rectangulo que hereda de Shape.
 * Es la figura base utilizada por la fabrica de la clase madre.
 */
public class Rectangle extends Shape {
    
    public static final int EDGES = 4;
    private int height;
    private int width;

    /**
     * Constructor por defecto. 
     * Define la apariencia inicial del rectangulo.
     */
    public Rectangle() {
        super(70, 15, "magenta");
        this.height = 30;
        this.width = 40;
    }

    /**
     * Actualiza las dimensiones del rectangulo.
     * Implementa el contrato obligatorio de la clase Shape.
     */
    @Override
    public void changeSize(int newHeight, int newWidth) {
        erase();
        this.height = newHeight;
        this.width = newWidth;
        draw();
    }

    /**
     * Retorna la geometría específica para ser dibujada en el Canvas.
     */
    @Override
    protected java.awt.Shape getShapeGeometry() {
        return new java.awt.Rectangle(xPosition, yPosition, width, height);
    }
}
