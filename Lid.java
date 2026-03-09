/**
 * Representa la tapa de una taza usando la abstracción Shape.
 * Se ha simplificado la gestión de posición y dimensiones.
 */
public class Lid {

    private final int id;
    private final Shape visual;
    
    // Posicion actual para calcular desplazamientos relativos
    private int xCurrent;
    private int yCurrent;

    /**
     * Crea una tapa asociada a un ID de taza.
     * @param id Identificador que define el ancho base de la tapa.
     */
    public Lid(int id) {
        this.id = id;
        this.visual = Shape.createShape();
        
        // Estado inicial
        this.xCurrent = 70;
        this.yCurrent = 15;

        // Configuracion visual por defecto
        visual.changeColor("black");
        setCovering(false); 
    }

    /**
     * Ajusta el tamaño de la tapa. 
     * Si esta cubriendo, se alarga para sellar la taza.
     * @param isCovering true si la tapa está sobre su taza correspondiente.
     */
    public void setCovering(boolean isCovering) {
        int width = (id * 10) + (isCovering ? 14 : 4);
        int height = isCovering ? 12 : 6;
        visual.changeSize(height, width);
    }

    /**
     * Mueve la tapa a una nueva posicion coordinada.
     */
    public void setPosition(int newX, int newY) {
        visual.moveHorizontal(newX - xCurrent);
        visual.moveVertical(newY - yCurrent);
        
        this.xCurrent = newX;
        this.yCurrent = newY;
    }

    public void setColor(String color) {
        visual.changeColor(color);
    }

    public void makeVisible() {
        visual.makeVisible();
    }

    public void makeInvisible() {
        visual.makeInvisible();
    }

    public int getId() {
        return id;
    }
}