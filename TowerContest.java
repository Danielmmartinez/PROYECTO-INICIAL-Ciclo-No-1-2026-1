import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 * Resuelve y simula el Problema de Stacking Cups .
 *
 * @author 
 * @version 
 */
public class TowerContest {

    // --- Constantes---
    private static final int MAX_BRUTE_FORCE_CUPS = 11;
    private static final int SIMULATION_DELAY_MS = 500;
    private static final int CUP_BASE_HEIGHT = 1;
    private static final int CANVAS_DEFAULT_WIDTH = 100;
    private static final int CANVAS_HEIGHT_PADDING = 5;

    // --- Atributos de Estado ---
    private Tower lastSimulatedTower;

    /**
     * Encuentra la secuencia de tazas que logra la altura deseada.
     *
     * @param n Cantidad total de tazas disponibles.
     * @param h Altura objetivo en centímetros.
     * @return Un String con las alturas de las tazas en orden, o un mensaje de error.
     */
    public String solve(int n, int h) {
        if (n > MAX_BRUTE_FORCE_CUPS) return "impossible_too_large";

        int[] currentPerm = new int[n];
        int[] bestResult = new int[n];
        boolean[] used = new boolean[n + 1];

        if (solveDFS(n, h, 0, currentPerm, used, bestResult)) {
            return formatSolution(bestResult);
        }
        return "impossible";
    }

    /**
     * Algoritmo recursivo para generar permutaciones.
     */  
    private boolean solveDFS(int n, int targetH, int depth, int[] perm, boolean[] used, int[] result) {
        if (depth == n) {
            if (calculateStrictPhysicsHeight(perm) == targetH) {
                System.arraycopy(perm, 0, result, 0, n);
                return true;
            }
            return false;
        }

        for (int i = n; i >= 1; i--) {
            if (!used[i]) {
                used[i] = true;
                perm[depth] = i;
                if (solveDFS(n, targetH, depth + 1, perm, used, result)) return true;
                used[i] = false;
            }
        }
        return false;
    }

    /**
     * Convierte el arreglo de IDs ganadores en un String de alturas.
     */
    private String formatSolution(int[] result) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < result.length; i++) {
            long height = 2L * result[i] - 1;
            sb.append(height).append(i == result.length - 1 ? "" : " ");
        }
        return sb.toString();
    }

    /**
     * Motor matemático 
     */
    private int calculateStrictPhysicsHeight(int[] perm) {
        int n = perm.length;
        int[] topY = new int[n];
        int[] baseY = new int[n];
        int maxHeight = 0;

        for (int i = 0; i < n; i++) {
            int currentId = perm[i];
            int currentHeight = (2 * currentId) - 1;
            int dropY = calculateDropY(i, currentId, perm, baseY, topY);

            baseY[i] = dropY;
            topY[i] = dropY + currentHeight;

            if (topY[i] > maxHeight) maxHeight = topY[i];
        }
        return maxHeight;
    }

    /**
     * Calcula la altura base donde aterriza una taza específica.
     */
    private int calculateDropY(int currentIndex, int currentId, int[] perm, int[] baseY, int[] topY) {
        int dropY = 0;
        for (int j = 0; j < currentIndex; j++) {
            int placedId = perm[j];
            int obstacleY = (currentId < placedId) ? baseY[j] + CUP_BASE_HEIGHT : topY[j];
            if (obstacleY > dropY) dropY = obstacleY;
        }
        return dropY;
    }


    /**
     * Ejecuta la simulación visual interactiva del resultado.
     *
     * @param n Número de tazas.
     * @param h Altura objetivo.
     */
    public void simulate(int n, int h) {
        String solution = solve(n, h);

        if (handleSimulationErrors(solution, n, h)) return;

        int[] idsToDrop = parseHeightsToIds(solution);
        animateTowerCreation(idsToDrop, h, solution);
    }

    /**
     * Muestra alertas gráficas si el problema es imposible o muy grande.
     */
    private boolean handleSimulationErrors(String solution, int n, int h) {
        if (solution.equals("impossible_too_large")) {
            JOptionPane.showMessageDialog(null, "El número de tazas (n=" + n + ") es muy grande para fuerza bruta.");
            return true;
        }
        if (solution.equals("impossible")) {
            JOptionPane.showMessageDialog(null, "¡Imposible!\nNo hay combinación para " + h + " cm.");
            return true;
        }
        return false;
    }

    /**
     * Convierte el String de alturas a los IDs de tazas originales.
     */
    private int[] parseHeightsToIds(String solution) {
        String[] heightsStr = solution.split(" ");
        int[] idsToDrop = new int[heightsStr.length];
        for (int i = 0; i < heightsStr.length; i++) {
            long height = Long.parseLong(heightsStr[i]);
            idsToDrop[i] = (int)((height + 1) / 2);
        }
        return idsToDrop;
    }

    /**
     * Gestiona la instancia gráfica de la torre y anima la caída paso a paso.
     */
    private void animateTowerCreation(int[] idsToDrop, int targetHeight, String solution) {
        if (lastSimulatedTower != null) lastSimulatedTower.makeInvisible();

        lastSimulatedTower = new Tower(CANVAS_DEFAULT_WIDTH, targetHeight + CANVAS_HEIGHT_PADDING);
        lastSimulatedTower.makeVisible();

        for (int id : idsToDrop) {
            lastSimulatedTower.pushCup(id);
            try { Thread.sleep(SIMULATION_DELAY_MS); } catch (InterruptedException ignored) {}
        }

        JOptionPane.showMessageDialog(null, "Secuencia: " + solution + "\nAltura lograda: " + lastSimulatedTower.height());
    }
}