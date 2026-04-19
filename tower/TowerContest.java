package tower;

import javax.swing.JOptionPane;

public class TowerContest {

    private static final int MAX_BRUTE_FORCE_CUPS = 11;
    private static final int SIMULATION_DELAY_MS = 500;
    private static final int CUP_BASE_HEIGHT = 1;
    private static final int CANVAS_DEFAULT_WIDTH = 100;
    private static final int CANVAS_HEIGHT_PADDING = 5;

    private static Tower lastSimulatedTower;

    private TowerContest() { }

    public static String solve(int n, int h) {
        if (n > MAX_BRUTE_FORCE_CUPS) { 
            return "impossible_too_large";
        }

        int[] currentPerm = new int[n];
        int[] bestResult = new int[n];
        boolean[] used = new boolean[n + 1];

        if (solveDFS(n, h, 0, currentPerm, used, bestResult)) {
            return formatSolution(bestResult);
        }
        return "impossible";
    }

    private static boolean solveDFS(int n, int targetH, int depth, int[] perm, boolean[] used, int[] result) {
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
                if (solveDFS(n, targetH, depth + 1, perm, used, result)) { 
                    return true;
                }
                used[i] = false;
            }
        }
        return false;
    }

    private static String formatSolution(int[] result) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < result.length; i++) {
            long height = 2L * result[i] - 1;
            sb.append(height).append(i == result.length - 1 ? "" : " ");
        }
        return sb.toString();
    }

    private static int calculateStrictPhysicsHeight(int[] perm) {
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

            if (topY[i] > maxHeight) { 
                maxHeight = topY[i];
            }
        }
        return maxHeight;
    }

    private static int calculateDropY(int currentIndex, int currentId, int[] perm, int[] baseY, int[] topY) {
        int dropY = 0;
        for (int j = 0; j < currentIndex; j++) {
            int placedId = perm[j];
            int obstacleY = (currentId < placedId) ? baseY[j] + CUP_BASE_HEIGHT : topY[j];
            if (obstacleY > dropY) {
                dropY = obstacleY;
            }
        }
        return dropY;
    }

    public static void simulate(int n, int h) {
        String solution = solve(n, h);

        if (handleSimulationErrors(solution, n, h)) { 
            return;
        }

        int[] idsToDrop = parseHeightsToIds(solution);
        animateTowerCreation(idsToDrop, h, solution);
    }

    private static boolean handleSimulationErrors(String solution, int n, int h) {
        // PMD: Literales de String primero
        if ("impossible_too_large".equals(solution)) {
            JOptionPane.showMessageDialog(null, "El número de tazas (n=" + n + ") es muy grande para fuerza bruta.");
            return true;
        }
        if ("impossible".equals(solution)) {
            JOptionPane.showMessageDialog(null, "¡Imposible!\nNo hay combinación para " + h + " cm.");
            return true;
        }
        return false;
    }

    private static int[] parseHeightsToIds(String solution) {
        String[] heightsStr = solution.split(" ");
        int[] idsToDrop = new int[heightsStr.length];
        for (int i = 0; i < heightsStr.length; i++) {
            long height = Long.parseLong(heightsStr[i]);
            idsToDrop[i] = (int)((height + 1) / 2);
        }
        return idsToDrop;
    }

    private static void animateTowerCreation(int[] idsToDrop, int targetHeight, String solution) {
        if (lastSimulatedTower != null) {
            lastSimulatedTower.makeInvisible();
        }

        lastSimulatedTower = new Tower(CANVAS_DEFAULT_WIDTH, targetHeight + CANVAS_HEIGHT_PADDING);
        lastSimulatedTower.makeVisible();

        for (int id : idsToDrop) {
            lastSimulatedTower.pushCup(id);
            try { 
                Thread.sleep(SIMULATION_DELAY_MS); 
            } catch (InterruptedException ignored) {
                // Ignore exception
            }
        }

        JOptionPane.showMessageDialog(null, "Secuencia: " + solution + "\nAltura lograda: " + lastSimulatedTower.height());
    }
}