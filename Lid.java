/**
 * Representa la tapa de una taza en la simulación.
 * Visualmente se dibuja más ancha que su taza correspondiente y de color negro
 * para cumplir con los requisitos de usabilidad (que luzca diferente).
 * @version 2.0
 */
public class Lid {
    private int id, xAct, yAct;
    private Rectangle visual;

    /**
     * Crea una nueva tapa asociada a un identificador de taza.
     * @param id El identificador de la taza a la que pertenece esta tapa.
     */
    public Lid(int id) {
        this.id = id;
        visual = new Rectangle();
        
        // La hacemos más alta (12) y que sobresalga bastante por los lados (+14)
        visual.changeSize(12, (id * 10) + 14);
        visual.changeColor("black"); // La forzamos a negro para que resalte siempre
        
        xAct = 70; yAct = 15;
    }

    /**
     * Mueve la tapa a una nueva posición en el Canvas.
     * @param newX La nueva coordenada X.
     * @param newY La nueva coordenada Y.
     */
    public void setPosition(int newX, int newY) {
        visual.moveHorizontal(newX - xAct);
        visual.moveVertical(newY - yAct);
        xAct = newX; 
        yAct = newY;
    }

    /**
     * Cambia el color de la tapa.
     * @param c El nombre del nuevo color (ej. "red", "blue").
     */
    public void setColor(String c) { visual.changeColor(c); }
    
    /** Hace visible la tapa en el Canvas. */
    public void makeVisible() { visual.makeVisible(); }
    
    /** Oculta la tapa en el Canvas. */
    public void makeInvisible() { visual.makeInvisible(); }
    
    /** @return El identificador de la tapa. */
    public int getId() { return id; }
}