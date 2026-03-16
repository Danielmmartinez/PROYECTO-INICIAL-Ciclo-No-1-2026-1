import java.awt.*;

/**
 * Representa un triangulo isosceles que hereda de Shape.
 * La geometria se calcula dinamicamente basándose en la posicion y dimensiones.
 */
public class Triangle extends Shape {
    
    public static final int VERTICES = 3;
    private int height;
    private int width;

    public Triangle() {
        super(140, 15, "green");
        this.height = 30;
        this.width = 40;
    }

    /**
     * Actualiza las dimensiones del triangulo y redibuja.
     */
    @Override
    public void changeSize(int newHeight, int newWidth) {
        erase();
        this.height = newHeight;
        this.width = newWidth;
        draw();
    }

    /**
     * Define la forma del triangulo creando un poligono de 3 puntos.
     * El punto superior se centra respecto al ancho.
     */
    @Override
    protected java.awt.Shape getShapeGeometry() {
        int[] xPoints = { 
            xPosition, 
            xPosition + (width / 2), 
            xPosition - (width / 2) 
        };
        int[] yPoints = { 
            yPosition, 
            yPosition + height, 
            yPosition + height 
        };
        
        return new Polygon(xPoints, yPoints, VERTICES);
    }
}