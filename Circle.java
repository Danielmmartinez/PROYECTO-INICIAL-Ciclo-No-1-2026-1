import java.awt.geom.*;

/**
 * Representa un circulo que hereda de Shape.
 * Se adapta para que el diámetro sea controlado de forma consistente.
 */
public class Circle extends Shape {
    
    private int diameter;

    public Circle() {
        super(20, 15, "blue");
        this.diameter = 30;
    }

    /**
     * Sobrecarga de conveniencia para cuando solo se necesita el diámetro.
     */
    public void changeSize(int newDiameter) {
        changeSize(newDiameter, newDiameter);
    }

    /**
     * Implementación del metodo abstracto de Shape.
     * En un círculo, el diametro se define por el ancho.
     */
    @Override
    public void changeSize(int newHeight, int newWidth) {
        erase();
        this.diameter = newWidth; 
        draw();
    }

    /**
     * Genera la geometria circular para el Canvas.
     */
    @Override
    protected java.awt.Shape getShapeGeometry() {
        return new Ellipse2D.Double(xPosition, yPosition, diameter, diameter);
    }
}