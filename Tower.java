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
        // Primero todas las tazas de mayor a menor (encajan correctamente)
        for (int id : ids) {
            stackedElements.add(cups.get(id));
        }
        // Luego las tapas de menor a mayor (se colocan sobre sus tazas ya encajadas)
        Collections.sort(ids);
        for (int id : ids) {
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
        final int FLOOR = Canvas.getCanvas().getHeight() * 3 / 4;
        int floorY = FLOOR;

        // cupTopY:      borde superior visual más alto hasta ahora (para apilado)
        // currentBaseY: fondo interno de la última taza contenedora (para encaje)
        // lastCupId:    id de la última taza colocada
        // blockedIds:   conjunto de ids de tazas cuya tapa ya aparece en el stack;
        //               si la taza contenedora (o cualquier taza que la contiene) está
        //               bloqueada, el encaje no es posible.
        // cupTopYById:  yPos del borde superior de cada taza, para que tapas sepan dónde posicionarse
        // stackTopY:    borde superior de la columna actual (lo que va ENCIMA se apoya aquí)
        //               se actualiza tanto con tazas apiladas como con tapas sobre la última taza
        // currentBaseY: fondo INTERIOR de la última taza contenedora (para encaje)
        // nestingChain: ids de las tazas contenedoras activas en la cadena de encaje actual
        // blockedCupIds: ids de tazas que ya tienen tapa → bloquean encaje a través de ellas
        // Construir orden de renderizado: igual que stackedElements pero garantizando
        // que cada tapa aparezca DESPUÉS de su taza, sin importar si está invertido.
        // Esto preserva el orden relativo entre tazas y entre tapas.
        ArrayList<Object> renderOrder = new ArrayList<>();
        ArrayList<Object> pendingLids = new ArrayList<>();
        // Primero recorremos en orden buscando tazas; las tapas que aparezcan
        // antes de su taza las diferimos al final.
        HashSet<Integer> cupsAdded = new HashSet<>();
        for (Object obj : stackedElements) {
            if (obj instanceof Cup) {
                // Antes de añadir esta taza, vaciar tapas pendientes que ya tienen su taza
                ArrayList<Object> stillPending = new ArrayList<>();
                for (Object lid : pendingLids) {
                    int lidId = ((Lid) lid).getId();
                    if (cupsAdded.contains(lidId)) { renderOrder.add(lid); }
                    else { stillPending.add(lid); }
                }
                pendingLids = stillPending;
                renderOrder.add(obj);
                cupsAdded.add(((Cup) obj).getId());
            } else if (obj instanceof Lid) {
                int lidId = ((Lid) obj).getId();
                if (cupsAdded.contains(lidId)) { renderOrder.add(obj); }
                else { pendingLids.add(obj); }
            }
        }
        // Tapas que quedaron pendientes (su taza nunca apareció) van al final
        renderOrder.addAll(pendingLids);

        HashMap<Integer, Integer> cupTopYById = new HashMap<>();
        ArrayList<Integer> nestingChain = new ArrayList<>();
        HashSet<Integer> blockedCupIds = new HashSet<>();

        int columnTopY   = floorY;
        int stackTopY    = floorY;
        int currentBaseY = floorY;
        int lastCupId    = Integer.MAX_VALUE;
        int columnCupId  = Integer.MAX_VALUE;

        for (Object obj : renderOrder) {
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
                    yPos = stackTopY - cupHeightPx;
                    nestingChain.clear();
                    columnCupId = currentId;
                    columnTopY  = yPos;
                }

                c.setPosition(xPos, yPos);
                c.makeVisible();

                cupTopYById.put(currentId, yPos);
                stackTopY    = Math.min(stackTopY, yPos);
                currentBaseY = yPos + cupHeightPx - 10;
                lastCupId    = currentId;

            } else if (obj instanceof Lid) {
                Lid l = (Lid) obj;
                int lidId = l.getId();
                int xPos  = centerX - (lidId * 10 / 2);

                int yPosLid = cupTopYById.containsKey(lidId) ? cupTopYById.get(lidId) : stackTopY;

                l.setPosition(xPos, yPosLid);
                l.makeVisible();

                blockedCupIds.add(lidId);
                if (yPosLid < stackTopY) stackTopY = yPosLid;
                if (lidId == columnCupId && yPosLid < columnTopY) columnTopY = yPosLid;
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
        int floorY = can.getHeight() * 3 / 4;
        int rulerX = 30;
    
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