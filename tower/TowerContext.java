package tower;

import java.util.List;

/**
 * Contrato que expone métodos seguros de la torre hacia los elementos.
 * Cumple con SRP aislando la manipulación del estado interno de la Torre.
 */
public interface TowerContext {
    
    /** Retorna una lista de solo lectura (o copia) de los elementos apilados actualmente. */
    List<TowerElement> getStackedElements();
    
    /** Permite a un elemento (como OpenerCup) eliminar todas las tapas de la torre. */
    void removeAllLids();
    
    /** Permite a un elemento enviar mensajes a la interfaz de usuario. */
    void reportError(String message);
    
    /** Consulta si existe una taza con un ID específico en la torre. */
    boolean hasCup(int id);
    
    /** Consulta si existe una tapa con un ID específico en la torre. */
    boolean hasLid(int id);
}