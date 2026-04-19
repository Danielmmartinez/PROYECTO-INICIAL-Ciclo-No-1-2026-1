package tower;

/**
 * Taza estándar sin comportamientos especiales.
 * Hereda toda la lógica OCP por defecto de la clase Cup.
 */
public class NormalCup extends Cup {
    
    public NormalCup(int id) {
        super(id, "normal");
        applyVisualDistinction();
    }
    
    @Override
    protected void applyVisualDistinction() {
    }
}