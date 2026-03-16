/**
 * Representa una taza hueca con geometria perfecta para anidamiento.
 * Gestiona su propia representacion visual mediante tres rectangulos.
 * 
 * @author
 * version
 */
public class Cup {
    //------Constantes------------
    
    private static final int WALL_THICKNESS = 10;
    private static final int WIDTH_MULTIPLIER = 20;
    private static final int INITIAL_X = 70;
    private static final int INITIAL_Y = 15;
    
    //-----Colores----------------
    
    private static final String[] COLORS = {
        "red", "blue", "green", "yellow", "magenta", "black"
    };
    
    // --- Atributos de estado ---
        private final int id;
        private final int height;
        private final String color;
        
        // --- Atributos visuales ---
        private final Shape base;
        private final Shape leftWall;
        private final Shape rightWall;
    
        private int xCurrent;
        private int yCurrent;
    /**
     * Constructor que crea una nueva taza con un identificador unico.
     * Su altura y anchura se calculan matematicamente en funcion del ID.
     *
     * @param id Identificador de la taza (determina su tamaño).
     */
    public Cup(int id) {
        this.id = id;
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
    private void setupDimensions() {
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
     * Selecciona un color de la paleta basándose en el ID.
     * @param i El identificador usado como índice.
     * @return El nombre del color en inglés válido para el Canvas.
     */
    private String pickColor(int i) {
        return COLORS[i % COLORS.length];
    }
}