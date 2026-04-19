package tower;

import java.util.List;

public class HierarchicalCup extends Cup {
    public HierarchicalCup (int id) {
        super(id, "hierarchical");
        applyVisualDistinction();
    }
    
    @Override
    protected void applyVisualDistinction(){
        this.leftWall.changeColor("black");
        this.rightWall.changeColor("black");
    }

    // --- MAGIA OCP ---
    @Override
    public int getInsertionIndex(TowerContext context) {
        List<TowerElement> elements = context.getStackedElements();
        int targetIndex = elements.size();
        
        for (int i = elements.size() - 1; i >= 0; i--) {
            TowerElement obj = elements.get(i);
            
            if ("anchor".equals(obj.getType())) {
                break;   
            } 
            if (obj.getId() < this.id) {
                targetIndex = i; 
            } else {
                break; 
            }
        }
        return targetIndex;
    }

    @Override
    public boolean canBeRemoved(TowerContext context) {
        List<TowerElement> elements = context.getStackedElements();
        // Si es el primer elemento (índice 0, el fondo), no se puede quitar
        if (!elements.isEmpty() && elements.indexOf(this) == 0) {
            context.reportError("La taza jerárquica está en el fondo y no se deja quitar.");
            return false;
        }
        return true;
    }
}