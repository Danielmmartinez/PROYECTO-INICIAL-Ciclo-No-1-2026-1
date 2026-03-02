/**
 * Representa una taza hueca en la simulación, compuesta por una base y dos paredes laterales.
 * La taza escala sus dimensiones basándose en su identificador (id).
 */
public class Cup {

    private int id;
    private int height;
    private String color;
    private Rectangle base;
    private Rectangle leftWall;
    private Rectangle rightWall;

    // Variables de rastreo para evitar errores de movimiento relativo
    private int xAct;
    private int yLeftAct;
    private int yBaseAct;
    private int xRightAct;
    private int yRightAct;

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

        // 1cm = 10 pixeles. Las paredes conservan su grosor original de 10.
        base.changeSize(10, id * 10);
        leftWall.changeSize(height * 10, 10);
        rightWall.changeSize(height * 10, 10);

        base.changeColor(color);
        leftWall.changeColor(color);
        rightWall.changeColor(color);

        // Estado inicial
        xAct = 70;
        xRightAct = 70;
        yLeftAct = 15;
        yBaseAct = 15;
        yRightAct = 15;

        setPosition(70, 15);
    }

    /**
     * Mueve la taza manteniendo unidas sus tres partes.
     */
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