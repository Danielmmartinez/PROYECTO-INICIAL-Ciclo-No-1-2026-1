import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Pruebas unitarias para el Segundo Ciclo de Tower.
 * Se ejecutan en modo invisible según los requerimientos.
 */
public class TowerC2Test {

    // --- PRUEBAS: ¿QUÉ DEBERÍA HACER? ---

    @Test
    public void shouldCreateTowerWithNCups() {
        // Debería crear 4 tazas con tamaños 1, 3, 5, 7
        Tower t = new Tower(4);
        
        assertTrue(t.ok());
        assertEquals(4, t.stackingItems().length);
    }

    @Test
    public void shouldSwapTwoExistingObjects() {
        Tower t = new Tower(2); // Crea tazas 1 y 3
        
        // Intercambiamos la taza 1 con la 3
        t.swap(
            new String[]{"cup", "1"}, 
            new String[]{"cup", "3"}
        );
        
        assertTrue(t.ok());
        
        // Verificamos que el orden interno haya cambiado
        String[][] items = t.stackingItems();
        assertEquals("3", items[0][1]); 
        assertEquals("1", items[1][1]);
    }

    @Test
    public void shouldCoverCupsWithMatchingLids() {
        Tower t = new Tower(100, 100);
        // Metemos 2 tazas
        t.pushCup(1);
        t.pushCup(2);
        
        // Metemos solo 1 tapa manual
        t.pushLid(1);
        
        t.cover();
        
        // ESPERAMOS que la torre ahora tenga 2 tazas tapadas (usó la que había y creó la otra)
        assertEquals(2, t.lidedCups().length);
        assertTrue(t.ok());
    }

    @Test
    public void shouldFindSwapToReduceHeight() {
        Tower t = new Tower(1000, 1000);
        
        // Apilamos mal a propósito: Taza pequeña (1) primero, Taza grande (5) encima.
        t.pushCup(1);
        t.pushCup(5);
        
        // Al consultar, debería recomendarnos intercambiar la 1 y la 5
        String[][] recommendation = t.swapToReduce();
        
        assertEquals(2, recommendation.length); // Retornó un par válido
        assertTrue(t.ok());
    }

    // --- PRUEBAS: ¿QUÉ NO DEBERÍA HACER? ---

    @Test
    public void shouldNotSwapNonExistentObjects() {
        Tower t = new Tower(2); // Solo tiene tazas 1 y 3
        
        // Intentamos intercambiar con una taza que no existe (99)
        t.swap(
            new String[]{"cup", "1"}, 
            new String[]{"cup", "99"}
        );
        
        // La operación debe fallar pacíficamente
        assertFalse(t.ok());
    }

    @Test
    public void shouldNotFailWhenCoveringWithoutLids() {
        Tower t = new Tower(100, 100);
        // Metemos 3 tazas sin tapas
        t.pushCup(1);
        t.pushCup(2);
        t.pushCup(3);
        
        // Llamamos al método mágico
        t.cover();
        
        // Ahora ESPERAMOS que haya 3 tazas tapadas (fabricó las 3 tapas)
        assertEquals(3, t.lidedCups().length);
        assertTrue(t.ok());
    }

}