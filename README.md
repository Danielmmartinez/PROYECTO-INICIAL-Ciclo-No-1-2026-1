**1. ¿Cuáles fueron los mini-ciclos definidos? Justifíquenlos.**
El proyecto se dividió en cuatro mini-ciclos estratégicos para garantizar calidad y mantenibilidad:
* **Mini-ciclo 1: Implementación Core y Pruebas Base.** Consistió en lograr que la lógica básica de apilamiento funcionara y pasara las pruebas iniciales, entendiendo el dominio del problema.
* **Mini-ciclo 2: Refactorización Arquitectónica (SOLID).** Justificado por la necesidad de eliminar el alto acoplamiento. Se implementaron los patrones *Facade* (`Tower`), *Factory* (`TowerFactory`) y se extrajeron interfaces (`TowerElement`, `TowerContext`).
* **Mini-ciclo 3: Desacoplamiento de Motores.** Justificado por el Principio de Responsabilidad Única (SRP). Se separó la matemática de colisiones (`TowerPhysics`) y el dibujado (`TowerRenderer`) del estado de la torre.
* **Mini-ciclo 4: Análisis Estático y Refinamiento (PMD).** Ciclo final dedicado a limpiar *code smells*, asegurar el encapsulamiento de clases utilitarias (`final`, constructores privados) y arreglar problemas de la librería gráfica heredada (Singleton Thread-safe en `Canvas`).

**2. ¿Cuál es el estado actual del proyecto en términos de mini-ciclos? ¿por qué?**
El proyecto se encuentra con el **100% de los mini-ciclos completados**. La aplicación es completamente funcional, pasa todas las pruebas unitarias y de integración (JUnit 4) con éxito, y el código está libre de advertencias críticas de análisis estático (PMD). La arquitectura final es altamente cohesiva, está desacoplada y lista para escalar.

**3. ¿Cuál fue el tiempo total invertido por cada uno de ustedes? (Horas/Hombre)**
* **[Daniel]:** ~[16] horas.
*Gran parte de este tiempo se invirtió en refactorización profunda y diseño de software, no solo en escribir código crudo*

**4. ¿Cuál consideran fue el mayor logro? ¿Por qué?**
El mayor logro fue **alcanzar una arquitectura puramente polimórfica y 100% fiel a los principios SOLID**. Logramos erradicar el uso de "atajos" como el operador `instanceof` o métodos condicionales como `isCup()`. Al delegar el comportamiento a cada objeto.

**5. ¿Cuál consideran que fue el mayor problema técnico? ¿Qué hicieron para resolverlo?**
El mayor problema fue el **acoplamiento bidireccional y las dependencias circulares**, especialmente entre la torre, las interfaces y las clases heredadas de la interfaz gráfica (`Canvas` / `Shape`). 
* **Solución:** Aplicamos el **Principio de Inversión de Dependencias (DIP)**. Creamos la interfaz `TowerContext` para que las piezas se comunicaran de forma segura con la torre sin conocer su implementación concreta. Además, solucionamos el redibujado "fantasma" de las pruebas automatizadas implementando un método `reset()` en el Singleton del `Canvas`, garantizando aislamiento entre los tests.

**6. ¿Qué hicieron bien como equipo? ¿Qué se comprometen a hacer para mejorar los resultados?**
* **Lo que hicimos bien:** Tuvimos un enfoque obsesivo con la calidad del diseño (Clean Code). No nos conformamos con que el código "funcionara", sino que iteramos hasta que la arquitectura fuera la correcta.
* **Compromiso de mejora:** Nos comprometemos a realizar un diseño UML mucho más exhaustivo *antes* de la primera línea de código en futuros proyectos. Gran parte de la refactorización pudo haberse evitado con una mejor planificación temprana de los contratos (interfaces).

**7. Considerando las prácticas XP incluidas en los laboratorios. ¿cuál fue la más útil? ¿por qué?**
La práctica más útil fue **Refactoring constante apoyado por Pruebas Automatizadas (Continuous Testing)**. Al hacer cambios estructurales masivos (como extraer toda la física de apilamiento a una clase nueva), la única forma de tener confianza de no haber roto el sistema fue ejecutar la suite de JUnit repetidamente. Las pruebas actuaron como nuestra red de seguridad.

**8. ¿Qué referencias usaron? ¿Cuál fue la más útil? Incluyan citas con estándares adecuados.**
La referencia más útil fue el catálogo de patrones de diseño, ya que nos dio la guía exacta de cómo separar responsabilidades usando *Facade* y *Factory Method*.
* Freeman, E., Robson, E., Bates, B., & Sierra, K. (2004). *Head First Design Patterns*. O'Reilly Media.
* Martin, R. C. (2008). *Clean Code: A Handbook of Agile Software Craftsmanship*. Prentice Hall.
* Shvets, A. (2018). *Dive Into Design Patterns*. Refactoring.Guru. https://refactoring.guru/design-patterns 
* Documentación oficial de JUnit 4. (s.f.). *JUnit 4 Framework*. https://junit.org/junit4/
