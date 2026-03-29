package tower;
import shapes.*;

/**
 * Representa una taza hueca con geometria perfecta para anidamiento.
 * Gestiona su propia representacion visual mediante tres rectangulos.
 * 
 * @author
 * version
 */
public abstract class Cup {
    //------Constantes------------
    
    protected static final int WALL_THICKNESS = 10;
    protected static final int WIDTH_MULTIPLIER = 20;
    protected static final int INITIAL_X = 70;
    protected static final int INITIAL_Y = 15;
    
    //-----Colores----------------
    
    protected static final String[] COLORS = {
        "red", "blue", "green", "yellow", "magenta", "black"
    };
    
    // --- Atributos de estado ---
        protected final int id;
        protected final int height;
        protected String color;
        protected final String type;
        
        // --- Atributos visuales ---
        protected final Shape base;
        protected final Shape leftWall;
        protected final Shape rightWall;
    
        protected int xCurrent;
        protected int yCurrent;
    /**
     * Constructor que crea una nueva taza con un identificador unico.
     * Su altura y anchura se calculan matematicamente en funcion del ID.
     *
     * @param id Identificador de la taza (determina su tamaño).
     */
    public Cup(int id, String type) {
        this.id = id;
        this.type = type;
        this.height = (2 * id) - 1;
        this.color = pickColor(id);

        this.base = Shape.createShape();
        this.leftWall = Shape.createShape();
        this.rightWall = Shape.createShape();

        setupDimensions();
        
        
        this.base.moveVertical((this.height * WALL_THICKNESS) - WALL_THICKNESS);
        this.rightWall.moveHorizontal((id * WIDTH_MULTIPLIER) - WALL_THICKNESS);
        
        this.xCurrent = INITIAL_X; 
        this.yCurrent = INITIAL_Y; 
    }
    
    /**
     * Configura el tamaño y color inicial de las figuras geometricas
     * que componen la taza basándose en el grosor de pared y multiplicadores.
     */
    protected void setupDimensions() {
        int currentWidth = id * WIDTH_MULTIPLIER;
        int currentHeightPx = height * WALL_THICKNESS;

        base.changeSize(WALL_THICKNESS, currentWidth); 
        leftWall.changeSize(currentHeightPx, WALL_THICKNESS);
        rightWall.changeSize(currentHeightPx, WALL_THICKNESS);

        base.changeColor(color);
        leftWall.changeColor(color);
        rightWall.changeColor(color);
    }

    /**
     * Desplaza todas las partes de la taza a una nueva coordenada absoluta.
     * @param newX Nueva posicion en el eje X.
     * @param newY Nueva posicion en el eje Y.
     */
    public void setPosition(int newX, int newY) {
        int dx = newX - xCurrent;
        int dy = newY - yCurrent;

        leftWall.moveHorizontal(dx);
        leftWall.moveVertical(dy);

        base.moveHorizontal(dx);
        base.moveVertical(dy);
        
        rightWall.moveHorizontal(dx);
        rightWall.moveVertical(dy);

        this.xCurrent = newX;
        this.yCurrent = newY;
    }

    /**
     * Hace visibles todas las partes de la taza en el canvas
     */
    public void makeVisible() {
        base.makeVisible();
        leftWall.makeVisible();
        rightWall.makeVisible();
    }

    /**
     * Oculta todas las partes de la taza en el Canvas
     */
    public void makeInvisible() {
        base.makeInvisible();
        leftWall.makeInvisible();
        rightWall.makeInvisible();
    }
    
    // Getters
    /**
     * @return el id de la taza
     */
    public int getId() {
        return id; 
    }
    
    /**
     * @return la altura de la taza
     */
    public int getHeight() {
        return height; 
    }
    
    /**
     * @return el color de la taza
     */
    public String getColor() {
        return color; 
    }
    
    /**
     * @return el tipo de la taza
     */
    public String getType() {
        return type;
    }

    /**
     * Selecciona un color de la paleta basándose en el ID.
     * @param i El identificador usado como índice.
     * @return El nombre del color en inglés válido para el Canvas.
     */
    protected String pickColor(int i) {
        return COLORS[i % COLORS.length];
    }
    
    /**
     * Metodo que permite que las subclases apliquen detalles unicos.
     */
    protected abstract void applyVisualDistinction();
}
