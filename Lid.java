/**
 * Representa la tapa de una taza con un indicador visual (perilla) 
 * cuando esta cubriendo.
 * 
 * @author
 * version
 */
public class Lid {
    
    // --- Constantes ---
    private static final int INITIAL_X = 70;
    private static final int INITIAL_Y = 15;
    private static final int LID_HEIGHT = 6;
    private static final int WIDTH_MULTIPLIER = 20;
    private static final int KNOB_WIDTH = 10;
    private static final int KNOB_HEIGHT = 6;
    private static final String DEFAULT_COLOR = "black";
    
    // --- Atributos de estado e identidad ---
    private final int id;
    private int xCurrent;
    private int yCurrent;
    private boolean isVisible;
    private boolean isCoveringState;
    
    // --- Atributos visuales ---
    private final Shape visual;
    private final Shape knob;
    
    /**
     * Constructor que crea una nueva tapa con su perilla centrada.
     * @param id Identificador de la tapa.
     */
    public Lid(int id) {
        this.id = id;
        this.visual = Shape.createShape();
        this.knob = Shape.createShape(); 
        
        this.xCurrent = INITIAL_X;
        this.yCurrent = INITIAL_Y;

        visual.changeColor(DEFAULT_COLOR);
        knob.changeColor(DEFAULT_COLOR); 
        
        visual.changeSize(LID_HEIGHT, id * WIDTH_MULTIPLIER);
        knob.changeSize(KNOB_HEIGHT, KNOB_WIDTH); 
        
        int halfTapaWidth = id * (WIDTH_MULTIPLIER / 2);
        int halfKnobWidth = KNOB_WIDTH / 2;
        
        knob.moveHorizontal(halfTapaWidth - halfKnobWidth);
        knob.moveVertical(-KNOB_HEIGHT); 

        setCovering(false); 
    }

    /**
     * Define si la tapa esta actualmente cubriendo su taza correspondiente.
     * Este estado determina si la perilla se muestra o se oculta.
     * @param isCovering true si esta cubriendo su taza, false en caso contrario.
     */
    public void setCovering(boolean isCovering) {
        this.isCoveringState = isCovering;
        updateKnobVisibility();
    }

    /**
     * Desplaza tanto la tapa como su perilla a una nueva coordenada absoluta.
     * @param newX Nueva posicion en el eje X.
     * @param newY Nueva posicion en el eje Y.
     */
    public void setPosition(int newX, int newY) {
        int dx = newX - xCurrent;
        int dy = newY - yCurrent;

        visual.moveHorizontal(dx);
        visual.moveVertical(dy);
        
        knob.moveHorizontal(dx);
        knob.moveVertical(dy);

        this.xCurrent = newX;
        this.yCurrent = newY;
    }

    /**
     * Cambia el color de la tapa y su perilla.
     * @param color Nombre del color en inglés soportado por el Canvas.
     */
    public void setColor(String color) { 
        visual.changeColor(color); 
        knob.changeColor(color);
    }
    
    /**
     * Hace visible la tapa en el canvas y revisa si debe mostrar la perilla.
     */
    public void makeVisible() { 
        isVisible = true;
        visual.makeVisible(); 
        updateKnobVisibility(); 
    }
    
    /**
     * Oculta completamente la tapa y su perilla del canvas.
     */
    public void makeInvisible() { 
        isVisible = false;
        visual.makeInvisible(); 
        knob.makeInvisible();
    }
    
    /**
     * Metodo auxiliar privado para gestionar la visibilidad de la perilla
     * basado en el estado general de visibilidad y el estado de cobertura.
     */
    private void updateKnobVisibility() {
        if (isVisible && isCoveringState) {
            knob.makeVisible();
        } else {
            knob.makeInvisible();
        }
    }
    //Getter

    /**
     *  @return El identificador de la tapa.
     */
    public int getId() { 
        return id; 
    }
}