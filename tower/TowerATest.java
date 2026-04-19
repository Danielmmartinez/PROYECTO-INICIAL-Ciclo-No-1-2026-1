package tower;

import static org.junit.Assert.*;
import org.junit.Test;
import javax.swing.JOptionPane;

public class TowerATest {

    @Test
    public void shouldOpenerDestroysLid() {
        Tower t = new Tower(200, 200);
        t.makeVisible();
        
        t.pushCup("normal", 3);
        esperar(1000); 
        t.pushLid("normal", 3);
        esperar(1000);
        
        JOptionPane.showMessageDialog(null, "Atención: A continuación caerá una taza Opener y destruirá la tapa.");
        t.pushCup("opener", 5);
        esperar(1000);
        
        int respuesta = JOptionPane.showConfirmDialog(null, 
            "¿Viste que la taza Opener (rosa en la base) eliminó la tapa negra?", 
            "Validación Visual 1", JOptionPane.YES_NO_OPTION);
            
        assertTrue("El usuario rechazó la prueba visual.", respuesta == JOptionPane.YES_OPTION);
        t.makeInvisible();
    }

    @Test
    public void shouldCrazyLidAndHierarchical() {
        Tower t = new Tower(200, 200);
        t.makeVisible();
        
        t.pushCup("normal", 4);
        esperar(1000);
        
        JOptionPane.showMessageDialog(null, "Atención: Entrará una Crazy Lid (amarilla). Debería irse a la base.");
        t.pushLid("crazy", 4);
        esperar(1000);
        
        JOptionPane.showMessageDialog(null, "Atención: Entrará una taza Hierarchical (negra) de tamaño 6. Debería empujar a todos hacia arriba.");
        t.pushCup("hierarchical", 6);
        esperar(1500);
        
        int respuesta = JOptionPane.showConfirmDialog(null, 
            "¿Aceptas que la Crazy Lid se fue al fondo, y luego la taza Hierarchical desplazó a los demás?", 
            "Validación Visual 2", JOptionPane.YES_NO_OPTION);
            
        assertTrue("El usuario rechazó la prueba visual.", respuesta == JOptionPane.YES_OPTION);
        t.makeInvisible();
    }
    
    private void esperar(int milisegundos) {
        try {
            Thread.sleep(milisegundos);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}