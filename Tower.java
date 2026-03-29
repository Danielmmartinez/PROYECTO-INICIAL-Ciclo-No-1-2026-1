package tower;
import shapes.*;

import java.util.*;
import javax.swing.JOptionPane;
/**
 * Gestiona la simulación física e interacción de una torre de tazas y tapas.
 * Implementa mecánicas de apilamiento estricto y renderizado visual.
 *
 * @author 
 * @version 
 */
public class Tower {

    // --- Constantes ---
    private static final int DEFAULT_SIZE = 100;
    private static final int WIDTH_MULT = 20;
    private static final int HEIGHT_MULT = 10;
    private static final int WALL_THICKNESS = 10;
    private static final int LID_HEIGHT = 6;
    private static final int RULER_MARK_GAP = 10;

    // --- Atributos de Estado ---
    private final Canvas simulationCanvas; 
    private final int width;
    private final int maxHeight;
    private boolean isVisible;
    private boolean lastOperationOk;

    // --- Colecciones de Datos ---
    private final Map<Integer, Cup> cups = new HashMap<>();
    private final Map<Integer, Lid> lids = new HashMap<>();
    private List<Object> stackedElements = new ArrayList<>();
    private final List<Shape> rulerMarks = new ArrayList<>();

    /** Crea una torre con dimensiones límite específicas. */
    public Tower(int width, int maxHeight) {
        this.simulationCanvas = Canvas.getCanvas(); 
        this.width = width;
        this.maxHeight = maxHeight;
        this.lastOperationOk = true;
    }

    /** Crea una torre estándar inicializada con n tazas. */
    public Tower(int numCups) {
        this(DEFAULT_SIZE, DEFAULT_SIZE);
        for (int i = 1; i <= numCups; i++) {
            pushCup(i);
        }
    }
    
    public void pushCup(int id) {
        pushCup(id, "normal");
    }
    
    public void pushCup(int id,String type) {
        if (cups.containsKey(id)) {
            reportError("La taza " + id + " ya existe.");
            return;
        }
        
        try {
            String className = type.substring(0,1).toUpperCase() + type.substring(1).toLowerCase() + "Cup";
            Cup newCup = (Cup) Class.forName("tower." + className).getDeclaredConstructor(int.class).newInstance(id);
            if (newCup instanceof OpenerCup) {
                List<Lid> lidsToRemove = new ArrayList<>(lids.values());
                for (Lid lid : lidsToRemove) {
                    removeFromCollections(lid);
                }
                processAddition(newCup, id, cups);
            } 
            else if (newCup instanceof HierarchicalCup) {
                int targetIndex = stackedElements.size();
                
                for (int i = stackedElements.size() - 1; i >= 0; i--) {
                    Object obj = stackedElements.get(i);
                    int objSize = (obj instanceof Cup) ? ((Cup)obj).getId() : ((Lid)obj).getId();
                    
                    if (obj instanceof AnchorCup) {
                        break;   
                    } 
                    
                    if (objSize < id) {
                        targetIndex = i; 
                    } else {
                        break; 
                    }
                }
        
                stackedElements.add(targetIndex, newCup);
                if (calculatePhysics(stackedElements, false) > maxHeight) {
                    stackedElements.remove(targetIndex); 
                    reportError("Excede la altura máxima permitida.");
                    lastOperationOk = false;
                } else {
                    cups.put(id, newCup);
                    lastOperationOk = true;
                    updateView();
                }
            }
            else {
                processAddition(newCup,id,cups);
            }
            
        } catch (Exception e) {
            reportError("Error: No se pudo crear la taza de tipo '" + type + "'.");
        }
    }
    
    public void pushLid(int id) {
        pushLid(id, "normal");
    }

    public void pushLid(int id, String type) {
        if (lids.containsKey(id)) {
            lastOperationOk = false;
            return;
        }
        try {
            String className = type.substring(0, 1).toUpperCase() + type.substring(1).toLowerCase() + "Lid";
            Lid newLid = (Lid) Class.forName("tower." + className).getDeclaredConstructor(int.class).newInstance(id);
            if (newLid instanceof FearfulLid) {
                if (!cups.containsKey(id)) {
                    reportError("La tapa miedosa " + id + " no entra porque su taza compañera no esta.");
                    lastOperationOk = false;
                    return; 
                }
            }
            if (cups.containsKey(id)) {
                newLid.setColor(cups.get(id).getColor());
            }
            if (newLid instanceof CrazyLid) {
                if (checkHeight(newLid)) {
                    lids.put(id, newLid);
                    stackedElements.add(0, newLid); 
                    lastOperationOk = true;
                    updateView();
                }
            } else {
              processAddition(newLid, id, lids);  
            }
        } catch (Exception e) {
            lastOperationOk = false;
        }
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
                if (obj instanceof FearfulLid && cups.containsKey(((FearfulLid)obj).getId())) {
                    reportError("La tapa miedosa no quiere salir, protege a su taza.");
                    lastOperationOk = false; return;
                }
                if (obj instanceof HierarchicalCup && stackedElements.indexOf(obj) == 0) {
                    reportError("La taza jerarquica esta en el fondo y no se deja quitar.");
                    lastOperationOk = false; return;
                }
                if (obj instanceof AnchorCup) {
                    reportError("¡La taza Ancla es demasiado pesada para quitarla!");
                    lastOperationOk = false; 
                    return;
                }
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
            if (item instanceof FearfulLid && cups.containsKey(id)) {
                reportError("La tapa miedosa no quiere salir, protege a su taza.");
                lastOperationOk = false; return;
            }
            if (item instanceof HierarchicalCup && stackedElements.indexOf(item) == 0) {
                reportError("La taza jerárquica está en el fondo y no se deja quitar.");
                lastOperationOk = false; return;
            }
            if (item instanceof AnchorCup) {
                reportError("¡La taza Ancla es inamovible!");
                lastOperationOk = false; return;
            }
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
                Lid newLid = Lid.createDefault(cup.getId());
                newLid.setColor(cup.getColor());
                lids.put(cup.getId(), newLid);
            }
        }
        rebuildStackWithLids();
    }

    private void rebuildStackWithLids() {
        List<Object> newStack = new ArrayList<>();
        Set<Lid> usedLids = new HashSet<>();

        for (Object obj : stackedElements) {
            newStack.add(obj);
            if (obj instanceof Cup) {
                Lid match = lids.get(((Cup) obj).getId());
                if (match != null) {
                    newStack.add(match);
                    usedLids.add(match);
                }
            }
        }
        
        for (Lid l : lids.values()) {
            if (!usedLids.contains(l) && !newStack.contains(l)) newStack.add(l);
        }

        stackedElements = newStack;
        lastOperationOk = true;
        updateView();
    }

    public void swap(String[] desc1, String[] desc2) {
        Object obj1 = findObject(desc1);
        Object obj2 = findObject(desc2);

        if (obj1 != null && obj2 != null) {
            Collections.swap(stackedElements, stackedElements.indexOf(obj1), stackedElements.indexOf(obj2));
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

    /**
     * Orquestador principal de las físicas.
     */
    private int calculatePhysics(List<Object> elements, boolean applyToView) {
        final int floorY = simulationCanvas.getHeight() * 3 / 4;
        int highestY = floorY;

        Map<Object, Integer> topYMap = new HashMap<>();
        Map<Object, Integer> baseYMap = new HashMap<>();
        List<Object> placed = new ArrayList<>();

        for (Object obj : elements) {
            int[] dims = calculateElementDimensions(obj, placed, applyToView);
            int w = dims[0], h = dims[1];

            int dropY = findDropY(w, placed, topYMap, baseYMap, floorY);
            int finalY = dropY - h;
            
            placed.add(obj);
            topYMap.put(obj, finalY);
            if (obj instanceof Cup) baseYMap.put(obj, finalY + h - WALL_THICKNESS);

            highestY = Math.min(highestY, finalY);
            applyVisualPosition(obj, w, finalY, applyToView);
        }
        return (floorY - highestY) / HEIGHT_MULT;
    }

    /**
     * Método Auxiliar 1
     */
    private int[] calculateElementDimensions(Object obj, List<Object> placed, boolean applyToView) {
        if (obj instanceof Cup) {
            Cup c = (Cup) obj;
            return new int[]{ c.getId() * WIDTH_MULT, c.getHeight() * HEIGHT_MULT };
        } else {
            Lid l = (Lid) obj;
            boolean isCovering = placed.stream().anyMatch(p -> p instanceof Cup && ((Cup) p).getId() == l.getId());
            if (applyToView) l.setCovering(isCovering);
            return new int[]{ l.getId() * WIDTH_MULT, LID_HEIGHT };
        }
    }

    /**
     *  Método Auxiliar 2
     */
    private int findDropY(int width, List<Object> placed, Map<Object, Integer> topYMap, Map<Object, Integer> baseYMap, int currentDropY) {
        int dropY = currentDropY;
        for (Object p : placed) {
            int obstacleY;
            if (p instanceof Cup) {
                int innerGap = (((Cup) p).getId() * WIDTH_MULT) - (WALL_THICKNESS * 2);
                obstacleY = (width <= innerGap) ? baseYMap.get(p) : topYMap.get(p);
            } else {
                obstacleY = topYMap.get(p); 
            }
            if (obstacleY < dropY) dropY = obstacleY;
        }
        return dropY;
    }

    /**
     * Método Auxiliar 3
     */
    private void applyVisualPosition(Object obj, int width, int finalY, boolean applyToView) {
        if (applyToView && isVisible) {
            int centerX = simulationCanvas.getWidth() / 2;
            int xPos = centerX - (width / 2);
            if (obj instanceof Cup) ((Cup) obj).setPosition(xPos, finalY);
            else ((Lid) obj).setPosition(xPos, finalY);
        }
    }

    public String[][] swapToReduce() {
        int currentHeight = height();
        int n = stackedElements.size();

        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                List<Object> temp = new ArrayList<>(stackedElements);
                Collections.swap(temp, i, j);

                if (calculatePhysics(temp, false) < currentHeight) {
                    lastOperationOk = true;
                    return formatSwapResult(stackedElements.get(i), stackedElements.get(j));
                }
            }
        }
        lastOperationOk = true;
        return new String[0][0]; 
    }

    private String[][] formatSwapResult(Object o1, Object o2) {
        return new String[][]{
            { o1 instanceof Cup ? "cup" : "lid", String.valueOf(o1 instanceof Cup ? ((Cup) o1).getId() : ((Lid) o1).getId()) },
            { o2 instanceof Cup ? "cup" : "lid", String.valueOf(o2 instanceof Cup ? ((Cup) o2).getId() : ((Lid) o2).getId()) }
        };
    }

    private void updateView() {
        if (!isVisible) return;
        lids.values().forEach(l -> { if (cups.containsKey(l.getId())) l.setColor(cups.get(l.getId()).getColor()); });
        calculatePhysics(stackedElements, true);
        
        stackedElements.forEach(o -> { if(o instanceof Cup) ((Cup)o).makeInvisible(); else ((Lid)o).makeInvisible(); });
        stackedElements.forEach(o -> { if(o instanceof Cup) ((Cup)o).makeVisible(); });
        stackedElements.forEach(o -> { if(o instanceof Lid) ((Lid)o).makeVisible(); });
    }

    public void makeVisible() {
        simulationCanvas.setVisible(true); 
        isVisible = true;
        drawRuler();
        updateView();
    }

    public void makeInvisible() {
        isVisible = false; 
        cups.values().forEach(Cup::makeInvisible);
        lids.values().forEach(Lid::makeInvisible);
        rulerMarks.forEach(Shape::makeInvisible);
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
            mark.moveVertical(floorY - (i * RULER_MARK_GAP) - 15);
            mark.makeVisible();
            rulerMarks.add(mark);
        }
    }

    private Object findObject(String[] desc) {
        try {
            int id = Integer.parseInt(desc[1]);
            return desc[0].equalsIgnoreCase("cup") ? cups.get(id) : lids.get(id);
        } catch (Exception e) { return null; }
    }

    public int[] lidedCups() {
        return cups.keySet().stream().filter(lids::containsKey).sorted().mapToInt(i -> i).toArray();
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
