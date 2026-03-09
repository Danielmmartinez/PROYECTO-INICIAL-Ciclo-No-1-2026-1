import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Pruebas unitarias para el Tercer Ciclo (TowerContest).
 * Valida la correcta resolución del simulador basado en las magnitudes acordadas.
 */
public class TowerContestCTest {

    @Test
    public void deberiaResolverCasoOficialDelDocumento() {
        TowerContest contest = new TowerContest();
        
        // Basado en el análisis con tu profesor y el documento, 
        // validamos que para n=4 y h=15 el simulador encuentre una respuesta válida.
        String resultado = contest.solve(4, 15);
        
        // Verificamos que el resultado NO sea "impossible"
        assertNotEquals("impossible", resultado);
        assertNotEquals("impossible_too_large", resultado);
    }

    @Test
    public void deberiaRechazarAlturaDemasiadoAlta() {
        TowerContest contest = new TowerContest();
        
        // Con 4 tazas, es físicamente imposible alcanzar una altura exagerada como 50.
        String resultado = contest.solve(4, 50);
        assertEquals("impossible", resultado);
    }

    @Test
    public void deberiaRechazarAlturasCeroONegativas() {
        TowerContest contest = new TowerContest();
        
        // El simulador debe ser robusto ante datos de entrada absurdos (0 o negativos).
        assertEquals("impossible", contest.solve(4, 0));
        assertEquals("impossible", contest.solve(4, -5));
    }

    @Test
    public void deberiaManejarElCasoMinimoDeUnaTaza() {
        TowerContest contest = new TowerContest();
        
        // Si n=1, la única taza tiene altura 1 (2*1 - 1 = 1).
        // Por lo tanto, pedir altura 1 debe dar como resultado la taza "1".
        assertEquals("1", contest.solve(1, 1));
        
        // Pedirle altura 2 a una sola taza que mide 1 debe ser imposible.
        assertEquals("impossible", contest.solve(1, 2));
    }

    @Test
    public void deberiaActivarProteccionContraCongelamiento() {
        TowerContest contest = new TowerContest();
        
        // Para evitar que la fuerza bruta congele BlueJ, si n > 11 debe abortar.
        assertEquals("impossible_too_large", contest.solve(15, 30));
    }
}