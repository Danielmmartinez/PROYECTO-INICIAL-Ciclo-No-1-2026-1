/**
 * Taza hueca compuesta por una base y dos paredes laterales.
 */
public class Cup {
    private int id, height;
    private String color;
    private Rectangle base, leftWall, rightWall;
    
    // Variables de rastreo para evitar errores de movimiento relativo
    private int xAct, yLeftAct, yBaseAct, xRightAct, yRightAct;

    public Cup(int id) {
        this.id = id;
        this.height = (2 * id) - 1;
        this.color = pickColor(id);
        
        base = new Rectangle();
        leftWall = new Rectangle();
        rightWall = new Rectangle();
        
        // 1cm = 10 pixeles
        base.changeSize(10, id * 10);
        leftWall.changeSize(height * 10, 10);
        rightWall.changeSize(height * 10, 10);
        
        base.changeColor(color);
        leftWall.changeColor(color);
        rightWall.changeColor(color);
        
        // Estado inicial de Rectangle.java (70, 15)
        xAct = 70; xRightAct = 70;
        yLeftAct = 15; yBaseAct = 15; yRightAct = 15;
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
        
        // Pared Derecha
        int targetXRight = newX + (id * 10) - 10;
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