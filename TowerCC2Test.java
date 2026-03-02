import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Propuesta de prueba colectiva para TowerCC2Test.
 */
public class TowerCC2Test {

    /**
     * Prueba propuesta para el repositorio colectivo.
     * Verifica que si la torre ya está en su altura mínima óptima,
     * swapToReduce() no recomiende ningún movimiento inválido.
     * * IMPORTANTE: Cambia "AB" por tus iniciales.
     */
    @Test
    public void accordingABShouldReturnEmptyIfHeightIsOptimal() {
        Tower t = new Tower(100, 1000);
        
        // Creamos una torre perfectamente ordenada donde la pequeña ya está dentro de la grande
        t.pushCup(5);
        t.pushCup(1);
        
        // Consultamos un movimiento de reducción
        String[][] recommendation = t.swapToReduce();
        
        // Como ya está óptima, no debería haber intercambios que reduzcan más la altura
        assertEquals(0, recommendation.length);
        assertTrue(t.ok());
    }
}