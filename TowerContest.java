import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 * Clase encargada de resolver y simular el Problema de Stacking Cups.
 * ADAPTADA PARA LAS FÍSICAS ESTRICTAS DE PAREDES GRUESAS (10px).
 */
public class TowerContest {

    /**
     * Resuelve el problema utilizando un motor de físicas interno (Fuerza Bruta)
     * para encontrar qué permutación logra exactamente la altura 'h' bajo las 
     * reglas reales de colisión de tu clase Tower.
     */
    public String solve(int n, int h) {
        // Límite de seguridad para evitar que el PC se congele calculando millones de ramas
        if (n > 11) {
            return "impossible_too_large"; 
        }
        
        int[] currentPerm = new int[n];
        int[] bestResult = new int[n];
        boolean[] used = new boolean[n + 1];
        
        boolean found = solveDFS(n, h, 0, currentPerm, used, bestResult);
        
        if (found) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < n; i++) {
                long height = 2L * bestResult[i] - 1;
                sb.append(height).append(i == n - 1 ? "" : " ");
            }
            return sb.toString();
        }
        
        return "impossible";
    }

    private boolean solveDFS(int n, int targetH, int depth, int[] perm, boolean[] used, int[] result) {
        if (depth == n) {
            // Evaluamos la permutación usando TUS físicas estrictas
            if (calculateStrictPhysicsHeight(perm) == targetH) {
                System.arraycopy(perm, 0, result, 0, n);
                return true;
            }
            return false;
        }
        
        // Exploramos todas las combinaciones posibles
        for (int i = n; i >= 1; i--) {
            if (!used[i]) {
                used[i] = true;
                perm[depth] = i;
                if (solveDFS(n, targetH, depth + 1, perm, used, result)) {
                    return true;
                }
                used[i] = false;
            }
        }
        return false;
    }

    /**
     * Este es un "cerebro" matemático que simula exactamente las mismas 
     * físicas de colisión de Tower.java sin tener que abrir la ventana.
     */
    private int calculateStrictPhysicsHeight(int[] perm) {
        int n = perm.length;
        int[] top = new int[n];
        int[] base = new int[n];
        int maxHeight = 0;
        
        for (int i = 0; i < n; i++) {
            int cupId = perm[i];
            int w = cupId * 10;
            int h = (2 * cupId - 1) * 10;
            int dropY = 0; // El suelo
            
            for (int j = 0; j < i; j++) {
                int pCup = perm[j];
                int pInnerW = (pCup * 10) - 20; // Cálculo del hueco real (-20 por las paredes)
                
                int obstacleY;
                if (w <= pInnerW) {
                    obstacleY = base[j]; // Si cabe, cae hasta la base interior
                } else {
                    obstacleY = top[j];  // Si no cabe, choca con el techo
                }
                
                if (obstacleY > dropY) {
                    dropY = obstacleY;
                }
            }
            
            base[i] = dropY + 10;
            top[i] = dropY + h;
            
            if (top[i] > maxHeight) {
                maxHeight = top[i];
            }
        }
        
        return maxHeight / 10; 
    }

    public void simulate(int n, int h) {
        String solution = solve(n, h);
        
        if (solution.equals("impossible_too_large")) {
            JOptionPane.showMessageDialog(null, "El número de tazas (n=" + n + ") es muy grande para fuerza bruta.");
            return;
        }
        
        if (solution.equals("impossible")) {
            JOptionPane.showMessageDialog(null, 
                "¡Imposible Físicamente!\nBajo las leyes de colisión con paredes de 10px, no existe NINGUNA " +
                "combinación de " + n + " tazas que logre exactamente " + h + " cm de altura."
            );
            return;
        }
        
        String[] heightsStr = solution.split(" ");
        int[] idsToDrop = new int[heightsStr.length];
        for (int i = 0; i < heightsStr.length; i++) {
            long height = Long.parseLong(heightsStr[i]);
            idsToDrop[i] = (int)((height + 1) / 2);
        }
        
        Tower simTower = new Tower(100, h + 5);
        simTower.makeVisible();
        
        for (int id : idsToDrop) {
            simTower.pushCup(id);
            try {
                Thread.sleep(500); 
            } catch (InterruptedException e) {}
        }
        
        JOptionPane.showMessageDialog(null, "¡Simulación Perfecta!\nSecuencia encontrada: " + solution + "\nAltura validada por físicas: " + simTower.height());
    }
}