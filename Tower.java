import java.util.*;
import java.awt.Point;
import javax.swing.JOptionPane;

/**
 * Gestiona la simulación de una torre de tazas apilables.
 * Controla las reglas de encaje, alturas máximas y renderizado de tazas y tapas.
 * @version 2.4
 */
public class Tower {
    private int width, maxHeight;
    private boolean isVisible, lastOperationOk;
    private HashMap<Integer, Cup> cups;
    private HashMap<Integer, Lid> lids;
    private ArrayList<Object> stackedElements;
    private ArrayList<Rectangle> rulerMarks;

    /**
     * Constructor principal que define los límites de la torre.
     * @param width Ancho del área de la torre.
     * @param maxHeight Altura máxima permitida para apilar en la torre.
     */
    public Tower(int width, int maxHeight) {
        this.width = width;
        this.maxHeight = maxHeight;
        this.cups = new HashMap<>();
        this.lids = new HashMap<>();
        this.stackedElements = new ArrayList<>();
        this.rulerMarks = new ArrayList<>();
        this.lastOperationOk = true;
    }

    /**
     * Constructor de extensión (Requisito 10) que crea una torre y genera 
     * automáticamente un número específico de tazas.
     * @param cups La cantidad de tazas a generar (de 1 hasta 'cups').
     */
    public Tower(int cups) {
        this(100, 1000); // Valores por defecto para que quepan las tazas
        for (int i = 1; i <= cups; i++) {
            pushCup(i);
        }
    }

    // --- MÉTODOS DE TAZAS ---
    
    public void pushCup(int i) {
        if (cups.containsKey(i)) {
            lastOperationOk = false;
            reportError("La taza " + i + " ya existe.");
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
        } else { lastOperationOk = false; }
    }

    // --- MÉTODOS DE TAPAS ---
    
    public void pushLid(int i) {
        if (lids.containsKey(i)) { lastOperationOk = false; return; }
        
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
        } else { lastOperationOk = false; }
    }

    // --- LÓGICA DE TORRE ---
    
    public void orderTower() {
        List<Integer> ids = new ArrayList<>(cups.keySet());
        Collections.sort(ids, Collections.reverseOrder());
        stackedElements.clear();
        for (int id : ids) { stackedElements.add(cups.get(id)); }
        Collections.sort(ids);
        for (int id : ids) { if (lids.containsKey(id)) stackedElements.add(lids.get(id)); }
        updateView();
        lastOperationOk = true;
    }

    public void reverseTower() {
        Collections.reverse(stackedElements);
        updateView();
        lastOperationOk = true;
    }

    /**
     * Intercambia la posición de dos objetos específicos en la torre (Requisito 11).
     */
    public void swap(String[] o1, String[] o2) {
        if (o1 == null || o2 == null || o1.length < 2 || o2.length < 2) {
            lastOperationOk = false;
            reportError("Parámetros de intercambio inválidos.");
            return;
        }

        Object obj1 = findObject(o1);
        Object obj2 = findObject(o2);

        if (obj1 == null || obj2 == null) {
            lastOperationOk = false;
            reportError("Uno o ambos objetos no se encuentran en la torre.");
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

    private Object findObject(String[] desc) {
        try {
            String type = desc[0].toLowerCase();
            int id = Integer.parseInt(desc[1]);
            
            if (type.equals("cup")) {
                return cups.get(id);
            } else if (type.equals("lid")) {
                return lids.get(id);
            }
        } catch (Exception e) {}
        return null;
    }

    public int height() {
        return simulateHeight(stackedElements);
    }

    private int simulateHeight(ArrayList<Object> elements) {
        final int FLOOR = Canvas.getCanvas().getHeight() * 3 / 4;
        int topY = FLOOR, baseY = FLOOR, columnTopY = FLOOR, lastCupId = Integer.MAX_VALUE;
        ArrayList<Integer> nestingChain = new ArrayList<>();
        HashSet<Integer> blockedCupIds = new HashSet<>();
        for (Object obj : elements) {
            if (obj instanceof Cup) {
                Cup c = (Cup) obj;
                int currentId = c.getId();
                int cupHeightPx = c.getHeight() * 10;
                boolean chainBlocked = false;
                for (int chainId : nestingChain) {
                    if (blockedCupIds.contains(chainId)) { chainBlocked = true; break; }
                }
                boolean canNest = (lastCupId != Integer.MAX_VALUE)
                                  && (currentId < lastCupId) && !chainBlocked
                                  && !blockedCupIds.contains(lastCupId);
                int y;
                if (canNest) {
                    y = baseY - cupHeightPx;
                    nestingChain.add(lastCupId);
                } else {
                    y = columnTopY - cupHeightPx;
                    nestingChain.clear();
                    columnTopY = y;
                }
                topY = Math.min(topY, y);
                baseY = y + cupHeightPx - 10;
                lastCupId = currentId;
            } else if (obj instanceof Lid) {
                Lid l = (Lid) obj;
                blockedCupIds.add(l.getId());
                topY -= 10;
            }
        }
        return (FLOOR - topY) / 10;
    }

    private boolean checkHeight(Object potential) {
        ArrayList<Object> temp = new ArrayList<>(stackedElements);
        temp.add(potential);
        if (simulateHeight(temp) > maxHeight) {
            reportError("Excede la altura máxima de " + maxHeight + " cm");
            lastOperationOk = false;
            return false;
        }
        return true;
    }

    public int[] lidedCups() {
        ArrayList<Integer> res = new ArrayList<>();
        for (Integer id : cups.keySet()) if (lids.containsKey(id)) res.add(id);
        Collections.sort(res);
        return res.stream().mapToInt(i -> i).toArray();
    }

    public String[][] stackingItems() {
        String[][] items = new String[stackedElements.size()][2];
        for (int i = 0; i < stackedElements.size(); i++) {
            Object o = stackedElements.get(i);
            items[i][0] = (o instanceof Cup) ? "cup" : "lid";
            items[i][1] = String.valueOf((o instanceof Cup) ? ((Cup)o).getId() : ((Lid)o).getId());
        }
        return items;
    }

    // --- VISIBILIDAD Y SISTEMA ---
    
    private void updateView() {
        if (!isVisible) return;

        Canvas can = Canvas.getCanvas();
        int centerX = can.getWidth() / 2;
        final int FLOOR = can.getHeight() * 3 / 4;
        int floorY = FLOOR;

        // Estructuras para guardar coordenadas antes de dibujar
        HashMap<Object, Point> positions = new HashMap<>();
        HashMap<Integer, Integer> cupTopYById = new HashMap<>();

        int columnTopY   = floorY;
        int stackTopY    = floorY;
        int currentBaseY = floorY;
        int lastCupId    = Integer.MAX_VALUE;
        int columnCupId  = Integer.MAX_VALUE;

        ArrayList<Integer> nestingChain = new ArrayList<>();
        HashSet<Integer> blockedCupIds = new HashSet<>();

        // PASO 1: Calcular posiciones lógicas (idéntico a la física de simulateHeight)
        for (Object obj : stackedElements) {
            if (obj instanceof Cup) {
                Cup c = (Cup) obj;
                int currentId   = c.getId();
                int cupHeightPx = c.getHeight() * 10;
                int xPos = centerX - (currentId * 10 / 2);
                int yPos;

                boolean chainBlocked = false;
                for (int chainId : nestingChain) {
                    if (blockedCupIds.contains(chainId)) { chainBlocked = true; break; }
                }
                boolean canNest = (lastCupId != Integer.MAX_VALUE)
                                  && (currentId < lastCupId)
                                  && !chainBlocked
                                  && !blockedCupIds.contains(lastCupId);

                if (canNest) {
                    yPos = currentBaseY - cupHeightPx;
                    nestingChain.add(lastCupId);
                } else {
                    yPos = columnTopY - cupHeightPx;
                    nestingChain.clear();
                    columnCupId = currentId;
                    columnTopY  = yPos;
                }

                positions.put(c, new Point(xPos, yPos));
                cupTopYById.put(currentId, yPos);
                
                stackTopY    = Math.min(stackTopY, yPos);
                currentBaseY = yPos + cupHeightPx - 10;
                lastCupId    = currentId;

            } else if (obj instanceof Lid) {
                Lid l = (Lid) obj;
                int lidId = l.getId();
                int xPos  = centerX - (lidId * 10 / 2) - 7;
                int yPosLid = cupTopYById.containsKey(lidId) ? cupTopYById.get(lidId) : stackTopY;

                positions.put(l, new Point(xPos, yPosLid));
                blockedCupIds.add(lidId);
                
                if (yPosLid < stackTopY) stackTopY = yPosLid;
                if (lidId == columnCupId && yPosLid < columnTopY) columnTopY = yPosLid;
            }
        }

        // PASO 2: Dibujar primero las tazas y luego las tapas para garantizar el orden visual
        for (Object obj : stackedElements) {
            if (obj instanceof Cup) {
                Cup c = (Cup) obj;
                Point p = positions.get(c);
                c.setPosition(p.x, p.y);
                c.makeVisible();
            }
        }
        for (Object obj : stackedElements) {
            if (obj instanceof Lid) {
                Lid l = (Lid) obj;
                Point p = positions.get(l);
                l.setPosition(p.x, p.y);
                l.makeVisible();
            }
        }
    }

    public void makeVisible() { Canvas.getCanvas().setVisible(true); isVisible = true; drawRuler(); updateView(); }
    public void makeInvisible() { isVisible = false; }
    public void exit() { System.exit(0); }
    public boolean ok() { return lastOperationOk; }

    private void drawRuler() {
        for (Rectangle r : rulerMarks) { r.makeInvisible(); }
        rulerMarks.clear();
    
        if (!isVisible) return;
    
        Canvas can = Canvas.getCanvas();
        int floorY = can.getHeight() * 3 / 4;
        int rulerX = 30;
    
        for (int i = 0; i <= maxHeight; i++) {
            Rectangle mark = new Rectangle();
            int markWidth = (i % 5 == 0) ? 15 : 8;
            mark.changeSize(1, markWidth); 
            mark.changeColor("black");
            
            int yPos = floorY - (i * 10);
            mark.moveHorizontal(rulerX - 70);
            mark.moveVertical(yPos - 15);
            
            mark.makeVisible();
            rulerMarks.add(mark);
        }
    }

    private void reportError(String m) { if (isVisible) JOptionPane.showMessageDialog(null, m); }
}