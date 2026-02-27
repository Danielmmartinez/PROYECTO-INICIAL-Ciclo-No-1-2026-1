import java.util.*;
import javax.swing.JOptionPane;

public class Tower {
    private int width, maxHeight;
    private boolean isVisible, lastOperationOk;
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
        if (cups.containsKey(i)) l.setColor(cups.get(i).getColor());
        
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
        for (int id : ids) {
            stackedElements.add(cups.get(id));
            if (lids.containsKey(id)) stackedElements.add(lids.get(id));
        }
        updateView();
        lastOperationOk = true;
    }

    public void reverseTower() {
        Collections.reverse(stackedElements);
        updateView();
        lastOperationOk = true;
    }

    public int height() {
        int currentTopY = 280, currentBaseY = 280, lastId = Integer.MAX_VALUE;
        for (Object obj : stackedElements) {
            if (obj instanceof Cup) {
                Cup c = (Cup) obj;
                int y = (c.getId() < lastId) ? currentBaseY - (c.getHeight() * 10) : currentTopY - (c.getHeight() * 10);
                currentTopY = Math.min(currentTopY, y);
                currentBaseY = y + (c.getHeight() * 10) - 10;
                lastId = c.getId();
            } else if (obj instanceof Lid) {
                currentTopY -= 10;
                currentBaseY = currentTopY;
                lastId = 0;
            }
        }
        return (280 - currentTopY) / 10;
    }

    private boolean checkHeight(Object potential) {
        ArrayList<Object> temp = new ArrayList<>(stackedElements);
        temp.add(potential);
        // Simulación rápida de altura
        int topY = 280, baseY = 280, lastId = Integer.MAX_VALUE;
        for (Object obj : temp) {
            if (obj instanceof Cup) {
                Cup c = (Cup) obj;
                int y = (c.getId() < lastId) ? baseY - (c.getHeight()*10) : topY - (c.getHeight()*10);
                topY = Math.min(topY, y);
                baseY = y + (c.getHeight()*10) - 10;
                lastId = c.getId();
            } else { topY -= 10; baseY = topY; lastId = 0; }
        }
        if ((280 - topY) / 10 > maxHeight) {
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
        int floorY = can.getHeight() - 50; 
        
        int currentBaseY = floorY; 
        int currentTopY = floorY;  // El punto más alto de TODA la torre
        int lastId = Integer.MAX_VALUE; 
    
        for (Object obj : stackedElements) {
            if (obj instanceof Cup) {
                Cup c = (Cup) obj;
                int currentId = c.getId();
                int cupHeightPx = c.getHeight() * 10;
                int xPos = centerX - (currentId * 10 / 2);
                int yPos;
    
                if (currentId < lastId) {
                    // ENCAJE INTERNO: Se apila sobre el fondo de la taza anterior
                    yPos = currentBaseY - cupHeightPx - 10; 
                    // El nuevo suelo para tazas MÁS pequeñas es el fondo de esta
                    currentBaseY = yPos + cupHeightPx; 
                } else {
                    // APILADO EXTERNO: Se apila sobre el borde más alto hasta ahora (TopY)
                    yPos = currentTopY - cupHeightPx;
                    // El nuevo suelo para tazas pequeñas es el fondo de esta nueva taza base
                    currentBaseY = yPos + cupHeightPx; 
                }
    
                c.setPosition(xPos, yPos);
                c.makeVisible();
    
                // Actualizamos el punto más alto global de la torre
                currentTopY = Math.min(currentTopY, yPos);
                lastId = currentId;
    
            } else if (obj instanceof Lid) {
                Lid l = (Lid) obj;
                int xPos = centerX - (l.getId() * 10 / 2);
                
                // La tapa se pone sobre el último objeto (sea taza o tapa)
                // Pero usamos una variable temporal para no arruinar el currentTopY de las tazas
                int yPosLid = currentTopY - 10;
                
                l.setPosition(xPos, yPosLid);
                l.makeVisible();
                
                // Actualizamos el tope global y el suelo para que lo que venga encima 
                // de la tapa descanse sobre ella
                currentTopY = yPosLid;
                currentBaseY = yPosLid;
                lastId = 0; // Una tapa bloquea el encaje interno, la siguiente taza irá arriba
            }
        }
    }

    public void makeVisible() { Canvas.getCanvas().setVisible(true); isVisible = true; drawRuler(); updateView(); }
    public void makeInvisible() { isVisible = false; } // Implementar borrado si es necesario
    public void exit() { System.exit(0); }
    public boolean ok() { return lastOperationOk; }

    private void drawRuler() {
        // 1. Limpiar marcas viejas
        for (Rectangle r : rulerMarks) {
            r.makeInvisible();
        }
        rulerMarks.clear();
    
        if (!isVisible) return;
    
        Canvas can = Canvas.getCanvas();
        int floorY = can.getHeight() - 50; // Suelo dinámico
        int rulerX = 30; // Un poco más a la izquierda
    
        // 2. Dibujar marcas según la altura máxima
        for (int i = 0; i <= maxHeight; i++) {
            Rectangle mark = new Rectangle();
            
            // Marca principal cada 5 unidades o más larga la de 10
            int markWidth = (i % 5 == 0) ? 15 : 8;
            mark.changeSize(1, markWidth); 
            mark.changeColor("black");
            
            // Calculamos la posición Y respecto al suelo actual
            int yPos = floorY - (i * 10);
            
            // Reset de posición inicial de Rectangle (70, 15) para evitar desfases
            mark.moveHorizontal(rulerX - 70);
            mark.moveVertical(yPos - 15);
            
            mark.makeVisible();
            rulerMarks.add(mark);
        }
    }

    private void reportError(String m) { if (isVisible) JOptionPane.showMessageDialog(null, m); }
}