package com.mikerusoft.euroleague.modelToEntityConvertor;

import com.mikerusoft.euroleague.utils.Utils;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

@Service
public class ConverterService implements ConverterI {

    private Map<String, Method> converters;

    public ConverterService() {
        this.converters = new ConcurrentHashMap<>();
    }

    @Override
    public <F, T> T convert(F obj, Class<T> classTo) {
        if (obj == null)
            return null;
        try {
            @SuppressWarnings("unchecked")
            Class<F> classFrom = (Class<F>)obj.getClass();
            @SuppressWarnings("unchecked")
            T result = (T) getMethod(classFrom, classTo).invoke(null, obj);
            return result;
        } catch (Exception e) {
            return Utils.rethrowRuntime(e);
        }
    }

    private <F, T> Method getMethod(Class<F> classFrom, Class<T> classTo) {
        String key = classFrom.getName() + "_" + classTo.getName();
        return converters.computeIfAbsent(key, s -> findReflectionMethod(classTo, classFrom));
    }

    private static <F, T> Method findReflectionMethod(Class<T> classTo, Class<F> classFrom) {
        return Stream.of(Converter.class.getMethods()).filter(m -> Modifier.isStatic(m.getModifiers()))
                .filter(m -> m.getParameterTypes().length == 1)
                .filter(m -> m.getReturnType().isAssignableFrom(classTo))
                .filter(m -> isAssignableFrom(classFrom, m.getParameterTypes()[0]))
                .findFirst()
            .orElseThrow(() -> new RuntimeException(new NoSuchMethodException("Not found method to convert from " + classFrom.getName() +
                                                                                    " to " + classTo.getSimpleName())));
    }

    private static final Set<String> map;
    static {
        Map<String, Boolean> initialMap = new ConcurrentHashMap<>();
        initialMap.put(createKey(java.lang.Byte.class, java.lang.Byte.TYPE), true);
        initialMap.put(createKey(java.lang.Byte.class, java.lang.Byte.TYPE), true);
        initialMap.put(createKey(java.lang.Short.class, java.lang.Short.TYPE), true);
        initialMap.put(createKey(java.lang.Integer.class, java.lang.Integer.TYPE), true);
        initialMap.put(createKey(java.lang.Long.class, java.lang.Long.TYPE), true);
        initialMap.put(createKey(java.lang.Float.class, java.lang.Float.TYPE), true);
        initialMap.put(createKey(java.lang.Double.class, java.lang.Double.TYPE), true);
        initialMap.put(createKey(java.lang.Character.class, java.lang.Character.TYPE), true);
        initialMap.put(createKey(java.lang.Boolean.class, java.lang.Boolean.TYPE), true);

        map = Collections.unmodifiableSet(initialMap.keySet());
    }

    private static String createKey(Class<?> cl1, Class<?> cl2) {
        return cl1.getName() + "_" + cl2.getName();
    }

    private static boolean isAssignableFrom(Class<?> from, Class<?> tis) {
        if (from.isPrimitive() || tis.isPrimitive()) {
            return map.contains(createKey(from, tis)) || map.contains(createKey(tis, from));
        }
        return tis.isAssignableFrom(from);
    }
}
