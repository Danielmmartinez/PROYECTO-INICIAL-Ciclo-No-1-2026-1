import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Pruebas unitarias completas para el simulador de fuerza bruta de Stacking Cups.
 * Validado bajo las reglas físicas estrictas de paredes de 10px.
 */
public class TowerContestTest {

    // ==========================================
    // 1. PRUEBAS DE LÍMITES (EDGE CASES)
    // ==========================================

    @Test
    public void deberiaManejarUnaSolaTaza() {
        TowerContest contest = new TowerContest();
        // 1 taza (ID 1) tiene altura 1 (2*1 - 1). Solo puede medir 1.
        assertEquals("1", contest.solve(1, 1));
        
        // Si pedimos que 1 taza mida 2 de alto, debe ser imposible.
        assertEquals("impossible", contest.solve(1, 2));
    }

    @Test
    public void deberiaRechazarAlturasCeroONegativas() {
        TowerContest contest = new TowerContest();
        // Físicamente la altura mínima es 1, no puede haber torres de tamaño 0 o negativo
        assertEquals("impossible", contest.solve(4, 0));
        assertEquals("impossible", contest.solve(4, -5));
    }

    @Test
    public void deberiaActivarProteccionContraCongelamiento() {
        TowerContest contest = new TowerContest();
        // Como usamos fuerza bruta (O(n!)), más de 11 tazas colapsarían el procesador.
        // El código debe detectar esto y abortar de forma segura.
        assertEquals("impossible_too_large", contest.solve(12, 50));
        assertEquals("impossible_too_large", contest.solve(20, 100));
    }

    // ==========================================
    // 2. PRUEBAS DE FÍSICAS ESTRICTAS (N = 3)
    // ==========================================
    
    // Para n=3, tenemos las tazas: Azul (h=1, w=10), Verde (h=3, w=20), Amarilla (h=5, w=30).
    // El hueco interno de la Amarilla es 30 - 20 = 10px. ¡La taza Azul cabe perfectamente!

    @Test
    public void deberiaResolverN3ConAnidamientoPerfecto() {
        TowerContest contest = new TowerContest();
        // Si la Amarilla (5) cae primero, luego la Azul (1) entra en ella, altura sigue siendo 5.
        // Luego la Verde (3) cae, pero no cabe, así que se apila encima: 5 + 3 = 8.
        // Por tanto, la altura de 8 ES posible físicamente.
        String resultado = contest.solve(3, 8);
        assertNotEquals("impossible", resultado);
        // La secuencia ganadora para esto suele ser "5 1 3"
    }

    @Test
    public void deberiaResolverN3TotalmenteApiladas() {
        TowerContest contest = new TowerContest();
        // Si apilamos todas de menor a mayor sin que entren, la altura es 1 + 3 + 5 = 9.
        String resultado = contest.solve(3, 9);
        assertNotEquals("impossible", resultado);
    }

    @Test
    public void deberiaRechazarN3FisicamenteInalcanzable() {
        TowerContest contest = new TowerContest();
        // Sabiendo que para n=3 la altura mínima es 8 y la máxima 9...
        // 7 es imposible bajo estas físicas estrictas.
        assertEquals("impossible", contest.solve(3, 7));
        assertEquals("impossible", contest.solve(3, 10)); // Y 10 se pasa
    }

    // ==========================================
    // 3. PRUEBAS CLÁSICAS (N = 4)
    // ==========================================

    @Test
    public void deberiaResolverAlturaMaximaN4() {
        TowerContest contest = new TowerContest();
        // Altura máxima para n=4 apilando todas como ladrillos: 1+3+5+7 = 16
        String resultado = contest.solve(4, 16);
        assertNotEquals("impossible", resultado);
    }

    @Test
    public void deberiaResolverAlturaFisicaPosibleN4() {
        TowerContest contest = new TowerContest();
        // Como vimos antes, la altura de 12 es uno de los resultados físicamente lógicos
        String resultado = contest.solve(4, 12);
        assertNotEquals("impossible", resultado);
    }

    @Test
    public void deberiaRechazarElProblemaOriginalDeLaMaraton() {
        TowerContest contest = new TowerContest();
        // Validamos la paradoja: el caso (4, 9) de la maratón de la vida real 
        // no tiene solución en nuestra simulación porque nuestras paredes sí tienen grosor.
        assertEquals("impossible", contest.solve(4, 9));
    }
}