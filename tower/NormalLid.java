package tower;

/**
 * Tapa estándar sin comportamientos especiales.
 * Hereda toda la lógica OCP por defecto de la clase Lid.
 */
public class NormalLid extends Lid {
    
    public NormalLid(int id) {
        super(id, "normal");
        applyVisualDistinction();
    }
    
    @Override
    protected void applyVisualDistinction() {
        // La tapa normal no tiene ningún cambio visual extra
    }
}