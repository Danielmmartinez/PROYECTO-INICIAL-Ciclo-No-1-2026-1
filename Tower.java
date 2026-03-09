import java.util.*;
import javax.swing.JOptionPane;

/**
 * Gestiona la simulacion de una torre de tazas y tapas apilables.
 * Refactorizada para centralizar la logica de fisica, gestion de colecciones y manejo de errores.
 */
public class Tower {

    private final Canvas simulationCanvas; 
    private final int width;
    private final int maxHeight;
    private boolean isVisible;
    private boolean lastOperationOk;

    // Colecciones para gestion de datos y renderizado
    private final Map<Integer, Cup> cups = new HashMap<>();
    private final Map<Integer, Lid> lids = new HashMap<>();
    private List<Object> stackedElements = new ArrayList<>();
    private final List<Shape> rulerMarks = new ArrayList<>();

    public Tower(int width, int maxHeight) {
        this.simulationCanvas = Canvas.getCanvas(); 
        this.width = width;
        this.maxHeight = maxHeight;
        this.lastOperationOk = true;
    }

    public Tower(int numCups) {
        this(100, 1000);
        for (int i = 1; i <= numCups; i++) {
            pushCup((2 * i) - 1);
        }
    }

    public void pushCup(int id) {
        if (cups.containsKey(id)) {
            reportError("La taza " + id + " ya existe.");
            return;
        }
        processAddition(new Cup(id), id, cups);
    }

    public void pushLid(int id) {
        if (lids.containsKey(id)) {
            lastOperationOk = false;
            return;
        }
        Lid lid = new Lid(id);
        if (cups.containsKey(id)) {
            lid.setColor(cups.get(id).getColor());
        }
        processAddition(lid, id, lids);
    }
    
    private <T> void processAddition(T element, int id, Map<Integer, T> storage) {
        if (checkHeight(element)) {
            storage.put(id, element);
            stackedElements.add(element);
            lastOperationOk = true;
            updateView();
        } else {
            lastOperationOk = false;
        }
    }

    public void popCup() {
        removeLastInstanceOf(Cup.class);
    }

    public void popLid() {
        removeLastInstanceOf(Lid.class);
    }

    private void removeLastInstanceOf(Class<?> clazz) {
        lastOperationOk = false;
        for (int i = stackedElements.size() - 1; i >= 0; i--) {
            Object obj = stackedElements.get(i);
            if (clazz.isInstance(obj)) {
                removeFromCollections(obj);
                lastOperationOk = true;
                updateView();
                break;
            }
        }
    }

    public void removeCup(int id) {
        removeSpecificItem(id, cups);
    }

    public void removeLid(int id) {
        removeSpecificItem(id, lids);
    }

    private void removeSpecificItem(int id, Map<Integer, ?> storage) {
        Object item = storage.get(id);
        if (item != null) {
            removeFromCollections(item);
            lastOperationOk = true;
            updateView();
        } else {
            lastOperationOk = false;
        }
    }

    private void removeFromCollections(Object obj) {
        stackedElements.remove(obj);
        if (obj instanceof Cup) {
            Cup c = (Cup) obj;
            cups.remove(c.getId());
            c.makeInvisible();
        } else if (obj instanceof Lid) {
            Lid l = (Lid) obj;
            lids.remove(l.getId());
            l.makeInvisible();
        }
    }

    public void orderTower() {
        List<Integer> ids = new ArrayList<>(cups.keySet());
        ids.sort(Collections.reverseOrder());
        
        List<Object> newOrder = new ArrayList<>();
        for (int id : ids) newOrder.add(cups.get(id));

        Collections.sort(ids);
        for (int id : ids) {
            if (lids.containsKey(id)) newOrder.add(lids.get(id));
        }

        stackedElements = newOrder;
        lastOperationOk = true;
        updateView();
    }

    public void reverseTower() {
        Collections.reverse(stackedElements);
        lastOperationOk = true;
        updateView();
    }

    public void cover() {
        for (Cup cup : cups.values()) {
            if (!lids.containsKey(cup.getId())) {
                Lid newLid = new Lid(cup.getId());
                newLid.setColor(cup.getColor());
                lids.put(cup.getId(), newLid);
            }
        }

        List<Object> newStack = new ArrayList<>();
        Set<Lid> usedLids = new HashSet<>();

        for (Object obj : stackedElements) {
            newStack.add(obj);
            if (obj instanceof Cup) {
                int id = ((Cup) obj).getId();
                Lid match = lids.get(id);
                if (match != null) {
                    newStack.add(match);
                    usedLids.add(match);
                }
            }
        }
        
        for (Lid l : lids.values()) {
            if (!usedLids.contains(l) && !newStack.contains(l)) {
                newStack.add(l);
            }
        }

        stackedElements = newStack;
        lastOperationOk = true;
        updateView();
    }

    public void swap(String[] desc1, String[] desc2) {
        Object obj1 = findObject(desc1);
        Object obj2 = findObject(desc2);

        if (obj1 != null && obj2 != null) {
            int idx1 = stackedElements.indexOf(obj1);
            int idx2 = stackedElements.indexOf(obj2);
            Collections.swap(stackedElements, idx1, idx2);
            lastOperationOk = true;
            updateView();
        } else {
            lastOperationOk = false;
        }
    }

    public int height() {
        return calculatePhysics(stackedElements, false);
    }

    private boolean checkHeight(Object potential) {
        List<Object> temp = new ArrayList<>(stackedElements);
        temp.add(potential);
        if (calculatePhysics(temp, false) > maxHeight) {
            reportError("Excede la altura máxima permitida.");
            return false;
        }
        return true;
    }

    private int calculatePhysics(List<Object> elements, boolean applyToView) {
        final int FLOOR = simulationCanvas.getHeight() * 3 / 4;
        final int CENTER_X = simulationCanvas.getWidth() / 2;
        int highestY = FLOOR;

        Map<Object, Integer> topYMap = new HashMap<>();
        Map<Object, Integer> baseYMap = new HashMap<>();
        List<Object> placed = new ArrayList<>();

        for (Object obj : elements) {
            int w, h;
            boolean isCup = obj instanceof Cup;

            if (isCup) {
                Cup c = (Cup) obj;
                w = c.getId() * 10;
                h = c.getHeight() * 10;
            } else {
                Lid l = (Lid) obj;
                boolean isCovering = false;
                if (!placed.isEmpty()) {
                    Object topItem = placed.get(placed.size() - 1);
                    isCovering = (topItem instanceof Cup && ((Cup) topItem).getId() == l.getId());
                }
                
                if (applyToView) l.setCovering(isCovering);
                w = isCovering ? (l.getId() * 10) + 14 : (l.getId() * 10) + 4;
                h = isCovering ? 12 : 6;
            }

            // --- LÓGICA DE COLISIÓN ESTRICTAMENTE FÍSICA ---
            int dropY = FLOOR;
            for (Object p : placed) {
                int obstacleY;
                
                if (p instanceof Cup) {
                    Cup pCup = (Cup) p;
                    // Calculamos el hueco real que queda entre las dos paredes de 10px
                    int pInnerW = (pCup.getId() * 10) - 20; 
                    
                    // Si la figura que cae cabe COMPLETAMENTE sin tocar las paredes
                    if (w <= pInnerW) {
                        obstacleY = baseYMap.get(p);
                    } else {
                        // Si es más ancha que el hueco (ej. la Amarilla sobre la Magenta), choca arriba
                        obstacleY = topYMap.get(p);
                    }
                } else {
                    // Las tapas siempre son un techo sólido
                    obstacleY = topYMap.get(p); 
                }
                
                if (obstacleY < dropY) {
                    dropY = obstacleY;
                }
            }

            int finalY = dropY - h;
            placed.add(obj);
            topYMap.put(obj, finalY);
            if (isCup) baseYMap.put(obj, finalY + h - 10);

            highestY = Math.min(highestY, finalY);

            if (applyToView && isVisible) {
                int xPos = CENTER_X - (w / 2);
                if (isCup) ((Cup) obj).setPosition(xPos, finalY);
                else ((Lid) obj).setPosition(xPos, finalY);
            }
        }
        return (FLOOR - highestY) / 10;
    }

    private void updateView() {
        if (!isVisible) return;
        
        for (Lid l : lids.values()) {
            if (cups.containsKey(l.getId())) {
                l.setColor(cups.get(l.getId()).getColor());
            }
        }
        
        calculatePhysics(stackedElements, true);
        
        stackedElements.forEach(o -> { if(o instanceof Cup) ((Cup)o).makeInvisible(); else ((Lid)o).makeInvisible(); });
        stackedElements.forEach(o -> { if(o instanceof Cup) ((Cup)o).makeVisible(); });
        stackedElements.forEach(o -> { if(o instanceof Lid) ((Lid)o).makeVisible(); });
    }

    private Object findObject(String[] desc) {
        try {
            int id = Integer.parseInt(desc[1]);
            return desc[0].equalsIgnoreCase("cup") ? cups.get(id) : lids.get(id);
        } catch (Exception e) { return null; }
    }

    public int[] lidedCups() {
        List<Integer> res = new ArrayList<>();
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

    public String[][] swapToReduce() {
        int currentHeight = height();
        int n = stackedElements.size();

        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                List<Object> tempStack = new ArrayList<>(stackedElements);
                Object obj1 = tempStack.get(i);
                Object obj2 = tempStack.get(j);
                tempStack.set(i, obj2);
                tempStack.set(j, obj1);

                // Usamos nuestro motor físico ICPC para ver si el cambio reduce la altura
                if (calculatePhysics(tempStack, false) < currentHeight) {
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
        return new String[0][0]; // Si no hay reducción, devuelve matriz vacía
    }
    
    public void makeVisible() {
        simulationCanvas.setVisible(true); 
        isVisible = true;
        drawRuler();
        updateView();
    }

    public void makeInvisible() {
        isVisible = false; 
    }

    private void drawRuler() {
        rulerMarks.forEach(Shape::makeInvisible);
        rulerMarks.clear();
        if (!isVisible) return;

        int floorY = simulationCanvas.getHeight() * 3 / 4;
        for (int i = 0; i <= maxHeight; i++) {
            Shape mark = Shape.createShape();
            mark.changeSize(1, (i % 5 == 0) ? 15 : 8);
            mark.changeColor("black");
            mark.moveHorizontal(-40);
            mark.moveVertical(floorY - (i * 10) - 15);
            mark.makeVisible();
            rulerMarks.add(mark);
        }
    }

    private void reportError(String m) {
        lastOperationOk = false;
        if (isVisible) JOptionPane.showMessageDialog(null, m);
    }

    public boolean ok() {
        return lastOperationOk; 
    }
    
    public void exit() {
        System.exit(0); 
    }
}