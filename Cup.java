/**
 * Representa una taza hueca en la simulación, compuesta por una base y dos paredes laterales.
 * La taza escala sus dimensiones basándose en su identificador (id).
 * @version 2.1
 */
public class Cup {
    private int id, height;
    private String color;
    private Rectangle base, leftWall, rightWall;
    
    // Variables de rastreo para evitar errores de movimiento relativo
    private int xAct, yLeftAct, yBaseAct, xRightAct, yRightAct;

    /**
     * Crea una nueva taza con un identificador específico.
     * @param id El identificador único y tamaño base de la taza.
     */
    public Cup(int id) {
        this.id = id;
        this.height = (2 * id) - 1;
        this.color = pickColor(id);
        
        base = new Rectangle();
        leftWall = new Rectangle();
        rightWall = new Rectangle();
        
        // CORRECCIÓN GEOMÉTRICA: 
        // Las paredes ahora tienen 2 píxeles de grosor para que las tazas menores
        // quepan perfectamente en el espacio interior sin solaparse.
        base.changeSize(10, id * 10);
        leftWall.changeSize(height * 10, 2); 
        rightWall.changeSize(height * 10, 2); 
        
        base.changeColor(color);
        leftWall.changeColor(color);
        rightWall.changeColor(color);
        
        xAct = 70; xRightAct = 70;
        yLeftAct = 15; yBaseAct = 15; yRightAct = 15;
        
        setPosition(70, 15);
    }

    public void setPosition(int newX, int newY) {
        // Pared Izquierda
        leftWall.moveHorizontal(newX - xAct);
        leftWall.moveVertical(newY - yLeftAct);
        yLeftAct = newY;
        
        // Base
        int targetYBase = newY + (height * 10) - 10;
        base.moveHorizontal(newX - xAct);
        base.moveVertical(targetYBase - yBaseAct);
        yBaseAct = targetYBase;
        
        // Pared Derecha (Ajustado a los 2 píxeles de grosor)
        int targetXRight = newX + (id * 10) - 2;
        rightWall.moveHorizontal(targetXRight - xRightAct);
        rightWall.moveVertical(newY - yRightAct);
        xRightAct = targetXRight;
        yRightAct = newY;

        xAct = newX;
    }

    public void makeVisible() { base.makeVisible(); leftWall.makeVisible(); rightWall.makeVisible(); }
    public void makeInvisible() { base.makeInvisible(); leftWall.makeInvisible(); rightWall.makeInvisible(); }
    public int getId() { return id; }
    public int getHeight() { return height; }
    public String getColor() { return color; }

    private String pickColor(int i) {
        String[] colors = {"red", "blue", "green", "yellow", "magenta", "black"};
        return colors[i % colors.length];
    }
}