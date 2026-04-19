package tower;

import java.util.*;
import javax.swing.JOptionPane;

public class Tower implements TowerContext {

    // --- Atributos de Estado ---
    private final int maxHeight; // Eliminado 'width' (PMD)
    private boolean isVisible;
    private boolean lastOperationOk;

    // --- Colecciones de Datos ---
    private final Map<Integer, TowerElement> cups = new HashMap<>();
    private final Map<Integer, TowerElement> lids = new HashMap<>();
    private List<TowerElement> stackedElements = new ArrayList<>();

    // --- Motores Especializados ---
    private final TowerPhysics physicsEngine;
    private final TowerRenderer renderer;

    public Tower(int width, int maxHeight) {
        // Se ignora el width internamente ya que no se usaba
        this.maxHeight = maxHeight;
        this.lastOperationOk = true;
        this.physicsEngine = new TowerPhysics();
        this.renderer = new TowerRenderer(maxHeight);
    }

    public Tower(int numCups) {
        this(100, 100);
        for (int i = 1; i <= numCups; i++) {
            pushCup("normal", i);
        }
    }

    public void pushCup(int id) {
        pushCup("normal", id);
    }

    public void pushCup(String type, int id) {
        if (cups.containsKey(id)) {
            reportError("La taza " + id + " ya existe.");
            return;
        }
        try {
            TowerElement newCup = TowerFactory.createCup(id, type);
            processAddition(newCup, cups);
        } catch (Exception e) {
            reportError("Error: No se pudo crear la taza de tipo '" + type + "'.");
            lastOperationOk = false;
        }
    }

    public void pushLid(int id) {
        pushLid("normal", id);
    }

    public void pushLid(String type, int id) {
        if (lids.containsKey(id)) {
            lastOperationOk = false;
            return;
        }
        try {
            TowerElement newLid = TowerFactory.createLid(id, type);
            TowerElement partnerCup = cups.get(id);
            
            if (partnerCup != null) {
                newLid.setColor(partnerCup.getColor());
            }
            processAddition(newLid, lids);
        } catch (Exception e) {
            lastOperationOk = false;
        }
    }

    private void processAddition(TowerElement element, Map<Integer, TowerElement> storage) {
        if (!element.canBePushed(this)) {
            lastOperationOk = false;
            return;
        }

        int targetIndex = element.getInsertionIndex(this);
        stackedElements.add(targetIndex, element);

        if (physicsEngine.exceedsMaxHeight(stackedElements, maxHeight, renderer.getCanvasWidth(), renderer.getCanvasHeight())) {
            stackedElements.remove(targetIndex);
            reportError("Excede la altura máxima permitida.");
            lastOperationOk = false;
        } else {
            storage.put(element.getId(), element);
            element.onPushed(this); 
            lastOperationOk = true;
            updateView();
        }
    }

    public void popCup() {
        removeLastInstanceOfCategory("cup");
    }

    public void popLid() {
        removeLastInstanceOfCategory("lid");
    }

    private void removeLastInstanceOfCategory(String category) {
        for (int i = stackedElements.size() - 1; i >= 0; i--) {
            TowerElement obj = stackedElements.get(i);
            if (category.equals(obj.getCategory())) {
                if (removeSpecificElement(obj)) {
                    return;
                }
            }
        }
        lastOperationOk = false;
    }

    public void removeCup(int id) {
        removeSpecificElement(cups.get(id));
    }

    public void removeLid(int id) {
        removeSpecificElement(lids.get(id));
    }

    private boolean removeSpecificElement(TowerElement element) {
        if (element != null && element.canBeRemoved(this)) {
            element.onRemoved(this);
            stackedElements.remove(element);
            if ("cup".equals(element.getCategory())) {
                cups.remove(element.getId());
            } else {
                lids.remove(element.getId());
            }
            
            element.makeInvisible();
            lastOperationOk = true;
            updateView();
            return true;
        }
        lastOperationOk = false;
        return false;
    }

    public void orderTower() {
        List<Integer> ids = new ArrayList<>(cups.keySet());
        ids.sort(Collections.reverseOrder());
        
        List<TowerElement> newOrder = new ArrayList<>();
        for (int id : ids) { // PMD: Llaves agregadas
            newOrder.add(cups.get(id));
        }

        Collections.sort(ids);
        for (int id : ids) { // PMD: Llaves agregadas
            if (lids.containsKey(id)) {
                newOrder.add(lids.get(id));
            }
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
        for (TowerElement cupElement : cups.values()) {
            if (!lids.containsKey(cupElement.getId())) {
                try {
                    TowerElement newLid = TowerFactory.createLid(cupElement.getId(), "normal");
                    newLid.setColor(cupElement.getColor());
                    lids.put(cupElement.getId(), newLid);
                } catch (Exception e) {
                    lastOperationOk = false;
                }
            }
        }
        
        List<TowerElement> newStack = new ArrayList<>();
        Set<TowerElement> usedLids = new HashSet<>();

        for (TowerElement obj : stackedElements) {
            newStack.add(obj);
            if ("cup".equals(obj.getCategory())) {
                TowerElement match = lids.get(obj.getId());
                if (match != null) {
                    newStack.add(match);
                    usedLids.add(match);
                }
            }
        }
        
        for (TowerElement l : lids.values()) {
            if (!usedLids.contains(l) && !newStack.contains(l)) {
                newStack.add(l);
            }
        }

        stackedElements = newStack;
        lastOperationOk = true;
        updateView();
    }

    public void swap(String[] o1, String[] o2) {
        TowerElement obj1 = findObject(o1);
        TowerElement obj2 = findObject(o2);

        if (obj1 != null && obj2 != null) {
            Collections.swap(stackedElements, stackedElements.indexOf(obj1), stackedElements.indexOf(obj2));
            lastOperationOk = true;
            updateView();
        } else {
            lastOperationOk = false;
        }
    }

    public String[][] swapToReduce() {
        int currentHeight = height();
        int n = stackedElements.size();

        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                List<TowerElement> temp = new ArrayList<>(stackedElements);
                Collections.swap(temp, i, j);

                if (!physicsEngine.exceedsMaxHeight(temp, currentHeight - 1, renderer.getCanvasWidth(), renderer.getCanvasHeight())) {
                    lastOperationOk = true;
                    return formatSwapResult(stackedElements.get(i), stackedElements.get(j));
                }
            }
        }
        lastOperationOk = true;
        return new String[0][0]; 
    }

    public int height() {
        return physicsEngine.calculatePhysics(stackedElements, renderer.getCanvasWidth(), renderer.getCanvasHeight(), false); 
    }

    public int[] lidedCups() {
        return cups.keySet().stream().filter(lids::containsKey).sorted().mapToInt(i -> i).toArray();
    }

    public String[][] stackingItems() {
        String[][] items = new String[stackedElements.size()][2];
        for (int i = 0; i < stackedElements.size(); i++) {
            TowerElement o = stackedElements.get(i);
            items[i][0] = o.getCategory(); // Ahora es dinámico
            items[i][1] = String.valueOf(o.getId());
        }
        return items;
    }

    public void makeVisible() {
        isVisible = true;
        renderer.makeVisible(stackedElements);
        updateView();
    }

    public void makeInvisible() {
        isVisible = false;
        renderer.makeInvisible(stackedElements);
    }

    public boolean ok() {
        return lastOperationOk; 
    }

    public void exit() {
        System.exit(0); 
    }

    @Override
    public List<TowerElement> getStackedElements() {
        return Collections.unmodifiableList(stackedElements);
    }

    @Override
    public void removeAllLids() {
        List<TowerElement> lidsToRemove = new ArrayList<>(lids.values());
        for (TowerElement lid : lidsToRemove) {
            stackedElements.remove(lid);
            lids.remove(lid.getId());
            lid.makeInvisible();
        }
    }

    @Override
    public void reportError(String message) {
        lastOperationOk = false;
        if (isVisible) { // PMD: Llaves agregadas
            JOptionPane.showMessageDialog(null, message);
        }
    }

    @Override
    public boolean hasCup(int id) {
        return cups.containsKey(id);
    }

    @Override
    public boolean hasLid(int id) {
        return lids.containsKey(id);
    }

    private void updateView() {
        if (!isVisible) {
            return;
        }
        
        lids.values().forEach(l -> { 
            if (cups.containsKey(l.getId())) {
                l.setColor(cups.get(l.getId()).getColor()); 
            }
        });
        
        physicsEngine.calculatePhysics(stackedElements, renderer.getCanvasWidth(), renderer.getCanvasHeight(), true);
        renderer.updateView(stackedElements, isVisible);
    }

    private TowerElement findObject(String[] desc) {
        try {
            int id = Integer.parseInt(desc[1]);
            // PMD: Literal de String primero
            return "cup".equalsIgnoreCase(desc[0]) ? cups.get(id) : lids.get(id);
        } catch (Exception e) { 
            return null; 
        }
    }

    private String[][] formatSwapResult(TowerElement o1, TowerElement o2) {
        return new String[][]{
            { o1.getCategory(), String.valueOf(o1.getId()) },
            { o2.getCategory(), String.valueOf(o2.getId()) }
        };
    }
}