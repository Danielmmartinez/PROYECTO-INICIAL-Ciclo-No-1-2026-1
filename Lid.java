/**
 * Representa la tapa de una taza en la simulación.
 */
public class Lid {

    private int id;
    private int xAct;
    private int yAct;
    private Rectangle visual;

    public Lid(int id) {
        this.id = id;
        visual = new Rectangle();

        // Dimensiones según requerimientos de usabilidad
        visual.changeSize(12, (id * 10) + 14);
        visual.changeColor("black");

        xAct = 70;
        yAct = 15;
    }

    public void setPosition(int newX, int newY) {
        visual.moveHorizontal(newX - xAct);
        visual.moveVertical(newY - yAct);
        xAct = newX;
        yAct = newY;
    }

    public void setColor(String c) {
        visual.changeColor(c);
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