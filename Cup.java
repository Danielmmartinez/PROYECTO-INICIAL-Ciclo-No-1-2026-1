/**
 * Representa una taza hueca en la simulación, compuesta por tres figuras.
 */
public class Cup {

    private final int id;
    private final int height;
    private final String color;
    
    // Componentes de la taza 
    private final Shape base;
    private final Shape leftWall;
    private final Shape rightWall;

    // Estado de posicion actual
    private int xCurrent;
    private int yCurrent;

    /**
     * Constructor que inicializa las partes de la taza basandose en su ID.
     * @param id El identificador que determina el ancho y la altura.
     */
    public Cup(int id) {
        this.id = id;
        this.height = (2 * id) - 1;
        this.color = pickColor(id);

        // Inicializamos las piezas usando la fabrica de la clase padre
        this.base = Shape.createShape();
        this.leftWall = Shape.createShape();
        this.rightWall = Shape.createShape();

        // Configuracion inicial de dimensiones 
        setupDimensions();
        
        this.base.moveVertical((height * 10) - 10);
        this.rightWall.moveHorizontal((id * 10) - 10);
        
        this.xCurrent = 70;
        this.yCurrent = 15;
    }

    /**
     * Define el tamaño y color de cada parte de la taza.
     */
    private void setupDimensions() {
        base.changeSize(10, id * 10);
        leftWall.changeSize(height * 10, 10);
        rightWall.changeSize(height * 10, 10);

        base.changeColor(color);
        leftWall.changeColor(color);
        rightWall.changeColor(color);
    }

    /**
     * Mueve todas las partes de la taza a una nueva coordenada de forma coordinada.
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

    public void makeVisible() {
        base.makeVisible();
        leftWall.makeVisible();
        rightWall.makeVisible();
    }

    public void makeInvisible() {
        base.makeInvisible();
        leftWall.makeInvisible();
        rightWall.makeInvisible();
    }

    public int getId() {
        return id; 
    }
    
    public int getHeight() {
        return height; 
    }
    
    public String getColor() {
        return color; 
    }

    private String pickColor(int i) {
        String[] colors = {"red", "blue", "green", "yellow", "magenta", "black"};
        return colors[i % colors.length];
    }
}