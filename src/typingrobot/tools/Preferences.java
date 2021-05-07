package typingrobot.tools;

/**
 *
 * @author Miodrag Spasic
 */
public class Preferences {
    
    private final java.util.prefs.Preferences preferences;

    public Preferences() {
        preferences = java.util.prefs.Preferences.userRoot();
    }
    
    public void put(String key, String value){
        preferences.put(key, value);
    }
    
    public String getString(String key, String def){
        return preferences.get(key, def);
    }

    public void putBoolean(String key, boolean value) {
        preferences.putBoolean(key, value);
    }
    
    public boolean getBoolean(String key, boolean def){
        return preferences.getBoolean(key, def);
    }
    
    
    
}
