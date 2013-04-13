package utils;

import java.util.Map;
import java.util.EnumMap;
import java.util.HashMap;

public class EnumBaseMapper<E extends Enum<E>> {
    private final Map<String, E> fromBase;
    private final Map<E, String> toBase;

    public EnumBaseMapper(Class<E> ec) {
	fromBase = new HashMap<String, E>();
	toBase = new EnumMap<E, String>(ec);
	for (E e : ec.getEnumConstants()) {
	    map(e, e.name());
	}
    }
    public void map(E e, String base) {
	fromBase.put(base, e);
	toBase.put(e, base);
    }
    public E fromBase(String base) {
	return fromBase.get(base);
    }
    public String toBase(E e) {
	return toBase.get(e);
    }
}
