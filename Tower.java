import java.util.*;
import javax.swing.JOptionPane;

/**
 * Gestiona la simulación de una torre de tazas apilables.
 * @version 3.1 (Diseño Limpio)
 */
public class Tower {

    private int width;
    private int maxHeight;
    private boolean isVisible;
    private boolean lastOperationOk;
    private HashMap<Integer, Cup> cups;
    private HashMap<Integer, Lid> lids;
    private ArrayList<Object> stackedElements;
    private ArrayList<Rectangle> rulerMarks;

    public Tower(int width, int maxHeight) {
        this.width = width;
        this.maxHeight = maxHeight;
        this.cups = new HashMap<>();
        this.lids = new HashMap<>();
        this.stackedElements = new ArrayList<>();
        this.rulerMarks = new ArrayList<>();
        this.lastOperationOk = true;
    }

    public Tower(int cups) {
        this(100, 1000);
        for (int i = 1; i <= cups; i++) {
            pushCup((2 * i) - 1);
        }
    }

    /** Verifica si añadir un elemento nuevo excedería la altura máxima. */
    private boolean checkHeight(Object potential) {
        ArrayList<Object> temp = new ArrayList<>(stackedElements);
        temp.add(potential);
        if (simulateHeight(temp) > maxHeight) {
            lastOperationOk = false;
            reportError("Excede la altura máxima permitida.");
            return false;
        }
        return true;
    }

    // --- MÉTODOS DE TAZAS ---

    public void pushCup(int i) {
        if (cups.containsKey(i)) {
            lastOperationOk = false;
            reportError("La taza ya existe.");
            return;
        }
        Cup c = new Cup(i);
        if (checkHeight(c)) {
            cups.put(i, c);
            stackedElements.add(c);
            lastOperationOk = true;
            updateView();
        }
    }

    public void popCup() {
        lastOperationOk = false;
        for (int j = stackedElements.size() - 1; j >= 0; j--) {
            if (stackedElements.get(j) instanceof Cup) {
                Cup c = (Cup) stackedElements.remove(j);
                cups.remove(c.getId());
                c.makeInvisible();
                lastOperationOk = true;
                updateView();
                break;
            }
        }
    }

    public void removeCup(int i) {
        if (cups.containsKey(i)) {
            Cup c = cups.remove(i);
            c.makeInvisible();
            stackedElements.remove(c);
            lastOperationOk = true;
            updateView();
        } else {
            lastOperationOk = false;
        }
    }

    // --- MÉTODOS DE TAPAS ---

    public void pushLid(int i) {
        if (lids.containsKey(i)) {
            lastOperationOk = false;
            return;
        }
        Lid l = new Lid(i);
        if (cups.containsKey(i)) {
            l.setColor(cups.get(i).getColor());
        }
        if (checkHeight(l)) {
            lids.put(i, l);
            stackedElements.add(l);
            lastOperationOk = true;
            updateView();
        }
    }

    public void popLid() {
        lastOperationOk = false;
        for (int j = stackedElements.size() - 1; j >= 0; j--) {
            if (stackedElements.get(j) instanceof Lid) {
                Lid l = (Lid) stackedElements.remove(j);
                lids.remove(l.getId());
                l.makeInvisible();
                lastOperationOk = true;
                updateView();
                break;
            }
        }
    }

    public void removeLid(int i) {
        if (lids.containsKey(i)) {
            Lid l = lids.remove(i);
            l.makeInvisible();
            stackedElements.remove(l);
            lastOperationOk = true;
            updateView();
        } else {
            lastOperationOk = false;
        }
    }

    // --- LÓGICA DE TORRE ---

    public void orderTower() {
        List<Integer> ids = new ArrayList<>(cups.keySet());
        Collections.sort(ids, Collections.reverseOrder());
        stackedElements.clear();

        for (int id : ids) {
            stackedElements.add(cups.get(id));
        }

        Collections.sort(ids);
        for (int id : ids) {
            if (lids.containsKey(id)) {
                stackedElements.add(lids.get(id));
            }
        }
        updateView();
        lastOperationOk = true;
    }

    public void reverseTower() {
        Collections.reverse(stackedElements);
        updateView();
        lastOperationOk = true;
    }

    public void swap(String[] o1, String[] o2) {
        if (o1 == null || o2 == null || o1.length < 2 || o2.length < 2) {
            lastOperationOk = false;
            return;
        }
        Object obj1 = findObject(o1);
        Object obj2 = findObject(o2);

        if (obj1 == null || obj2 == null) {
            lastOperationOk = false;
            return;
        }

        int index1 = stackedElements.indexOf(obj1);
        int index2 = stackedElements.indexOf(obj2);

        if (index1 != -1 && index2 != -1) {
            stackedElements.set(index1, obj2);
            stackedElements.set(index2, obj1);
            lastOperationOk = true;
            updateView();
        } else {
            lastOperationOk = false;
        }
    }

    public void cover() {
        ArrayList<Lid> presentLids = new ArrayList<>();
        for (Object obj : stackedElements) {
            if (obj instanceof Lid) {
                presentLids.add((Lid) obj);
            }
        }

        ArrayList<Object> newStack = new ArrayList<>();
        for (Object obj : stackedElements) {
            if (obj instanceof Cup) {
                newStack.add(obj);
                int id = ((Cup) obj).getId();
                Lid match = null;
                for (Lid l : presentLids) {
                    if (l.getId() == id) {
                        match = l;
                        break;
                    }
                }
                if (match != null) {
                    newStack.add(match);
                    presentLids.remove(match);
                }
            } else if (!(obj instanceof Lid)) {
                newStack.add(obj);
            }
        }
        newStack.addAll(presentLids); // Tapas sin taza al tope

        stackedElements = newStack;
        lastOperationOk = true;
        updateView();
    }

    public String[][] swapToReduce() {
        int currentHeight = height();
        int n = stackedElements.size();

        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                ArrayList<Object> tempStack = new ArrayList<>(stackedElements);
                Object obj1 = tempStack.get(i);
                Object obj2 = tempStack.get(j);
                tempStack.set(i, obj2);
                tempStack.set(j, obj1);

                if (simulateHeight(tempStack) < currentHeight) {
                    String[][] result = new String[2][2];
                    result[0][0] = (obj1 instanceof Cup) ? "cup" : "lid";
                    result[0][1] = String.valueOf((obj1 instanceof Cup) ? ((Cup) obj1).getId() : ((Lid) obj1).getId());
                    result[1][0] = (obj2 instanceof Cup) ? "cup" : "lid";
                    result[1][1] = String.valueOf((obj2 instanceof Cup) ? ((Cup) obj2).getId() : ((Lid) obj2).getId());
                    lastOperationOk = true;
                    return result;
                }
            }
        }
        lastOperationOk = true;
        return new String[0][0];
    }

    private Object findObject(String[] desc) {
        try {
            String type = desc[0].toLowerCase();
            int id = Integer.parseInt(desc[1]);
            if (type.equals("cup")) return cups.get(id);
            else if (type.equals("lid")) return lids.get(id);
        } catch (Exception e) {
            // Error en parsing o ID inexistente
        }
        return null;
    }

    // --- MOTOR DE FÍSICAS ---

    public int height() {
        return calculatePhysics(stackedElements, false);
    }

    private int simulateHeight(ArrayList<Object> elements) {
        return calculatePhysics(elements, false);
    }

    private int calculatePhysics(ArrayList<Object> elements, boolean applyToView) {
        Canvas can = Canvas.getCanvas();
        final int FLOOR = can.getHeight() * 3 / 4;
        int centerX = can.getWidth() / 2;
        int highestY = FLOOR;

        HashMap<Object, Integer> topYMap = new HashMap<>();
        HashMap<Object, Integer> baseYMap = new HashMap<>();
        ArrayList<Object> placed = new ArrayList<>();

        for (Object obj : elements) {
            int w, h;
            boolean isCup = obj instanceof Cup;

            if (isCup) {
                w = ((Cup) obj).getId() * 10;
                h = ((Cup) obj).getHeight() * 10;
            } else {
                w = ((Lid) obj).getId() * 10 + 14;
                h = 12;
            }

            int dropY = FLOOR;
            for (Object p : placed) {
                int pTopY = topYMap.get(p);
                if (p instanceof Cup) {
                    int pW = ((Cup) p).getId() * 10;
                    // Si cabe adentro, cae hasta la base interna, si no choca con el borde superior
                    dropY = Math.min(dropY, (w < pW) ? baseYMap.get(p) : pTopY);
                } else {
                    dropY = Math.min(dropY, pTopY);
                }
            }

            int finalY = dropY - h;
            int baseY = isCup ? finalY + h - 10 : finalY;

            placed.add(obj);
            topYMap.put(obj, finalY);
            if (isCup) baseYMap.put(obj, baseY);

            highestY = Math.min(highestY, finalY);

            if (applyToView && isVisible) {
                int xPos = centerX - (w / 2);
                if (isCup) ((Cup) obj).setPosition(xPos, finalY);
                else ((Lid) obj).setPosition(xPos, finalY);
            }
        }
        return (FLOOR - highestY) / 10;
    }

    private void updateView() {
        if (!isVisible) return;

        calculatePhysics(stackedElements, true);

        // Render visual: primero tazas y luego tapas para que queden encima
        for (Object obj : stackedElements) {
            if (obj instanceof Cup) ((Cup) obj).makeVisible();
        }
        for (Object obj : stackedElements) {
            if (obj instanceof Lid) ((Lid) obj).makeVisible();
        }
    }

    public int[] lidedCups() {
        ArrayList<Integer> res = new ArrayList<>();
        for (Integer id : cups.keySet()) {
            if (lids.containsKey(id)) res.add(id);
        }
        Collections.sort(res);
        return res.stream().mapToInt(i -> i).toArray();
    }

    public String[][] stackingItems() {
        String[][] items = new String[stackedElements.size()][2];
        for (int i = 0; i < stackedElements.size(); i++) {
            Object o = stackedElements.get(i);
            items[i][0] = (o instanceof Cup) ? "cup" : "lid";
            items[i][1] = String.valueOf((o instanceof Cup) ? ((Cup) o).getId() : ((Lid) o).getId());
        }
        return items;
    }

    public void makeVisible() {
        Canvas.getCanvas().setVisible(true);
        isVisible = true;
        drawRuler();
        updateView();
    }

    public void makeInvisible() {
        isVisible = false;
    }

    public void exit() {
        System.exit(0);
    }

    public boolean ok() {
        return lastOperationOk;
    }

    private void drawRuler() {
        for (Rectangle r : rulerMarks) r.makeInvisible();
        rulerMarks.clear();
        if (!isVisible) return;

        Canvas can = Canvas.getCanvas();
        int floorY = can.getHeight() * 3 / 4;

        for (int i = 0; i <= maxHeight; i++) {
            Rectangle mark = new Rectangle();
            mark.changeSize(1, (i % 5 == 0) ? 15 : 8);
            mark.changeColor("black");
            mark.moveHorizontal(30 - 70);
            mark.moveVertical((floorY - (i * 10)) - 15);
            mark.makeVisible();
            rulerMarks.add(mark);
        }
    }

    private void reportError(String m) {
        if (isVisible) JOptionPane.showMessageDialog(null, m);
    }
}