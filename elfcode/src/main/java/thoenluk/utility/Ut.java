package thoenluk.utility;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 *
 * @author Lukas Th�ni lukas.thoeni@gmx.ch
 */
public class Ut {
    private static final HashMap<String, Integer> numberCache = new HashMap<>();

    public static int cachedParseInt(String stringRepresentation) throws NumberFormatException {
        if(!numberCache.containsKey(stringRepresentation)) {
            numberCache.put(stringRepresentation, Integer.parseInt(stringRepresentation));
        }
        return numberCache.get(stringRepresentation);
    }

    public static char betterCharAt(String s, int index) {
        index %= s.length();
        if(index < 0) {
            return s.charAt(s.length() + index);
        } else {
            return s.charAt(index);
        }
    }

    public static <K, V> V getOrDefault(Map<K, V> map, K key, Supplier<V> provider) {
        if(!map.containsKey(key)) {
            map.put(key, provider.get());
        }
        return map.get(key);
    }
}