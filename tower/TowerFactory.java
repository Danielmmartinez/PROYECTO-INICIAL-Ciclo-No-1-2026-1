package tower;

import java.util.Locale;

public class TowerFactory {
    
    private TowerFactory() { }
    
    public static TowerElement createCup(int id, String type) throws Exception {
        String className = type.substring(0, 1).toUpperCase(Locale.ROOT) + type.substring(1).toLowerCase(Locale.ROOT) + "Cup";
        return (TowerElement) Class.forName("tower." + className)
                                         .getDeclaredConstructor(int.class)
                                         .newInstance(id);
    }

    public static TowerElement createLid(int id, String type) throws Exception {
        String className = type.substring(0, 1).toUpperCase(Locale.ROOT) + type.substring(1).toLowerCase(Locale.ROOT) + "Lid";
        return (TowerElement) Class.forName("tower." + className)
                                         .getDeclaredConstructor(int.class)
                                         .newInstance(id);
    }
}