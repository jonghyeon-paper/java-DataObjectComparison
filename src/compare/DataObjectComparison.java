package compare;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DataObjectComparison {
    public static final int NOT_EQUAL = 0;
    public static final int PERFECT_EQUAL = 1;
    public static final int SUB_EQUAL = 2;
    public static final int PART_EQUAL = 3;

    public static <E1, E2> int compare(E1 source, E2 target, boolean includeSuperClass) {
        if (source == null || target == null) {
            throw new RuntimeException("Passed parameter is null.");
        }

        Method[] sourceMethods = null;
        Method[] targetMethods = null;
        String[] sameGetMethods = null;
        if (includeSuperClass) {
            sourceMethods = findAllMethod(source.getClass());
            targetMethods = findAllMethod(target.getClass());
        } else {
            sourceMethods = source.getClass().getDeclaredMethods();
            targetMethods = target.getClass().getDeclaredMethods();
        }
        sourceMethods = findGetMethod(sourceMethods);
        targetMethods = findGetMethod(targetMethods);
        sameGetMethods = findSameMethod(sourceMethods, targetMethods);

        if (sameGetMethods == null || sameGetMethods.length == 0) {
            return NOT_EQUAL;
        }

        for (String methodName : sameGetMethods) {
            Object sourceValue = null;
            Object targetValue = null;
            try {
                sourceValue = source.getClass().getMethod(methodName).invoke(source);
                targetValue = target.getClass().getMethod(methodName).invoke(target);
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException e) {
                throw new RuntimeException("Cannot found method");
            }

            if (sourceValue == null && targetValue == null) {
                continue;
            } else if (sourceValue == null || targetValue == null) {
                return NOT_EQUAL;
            } else if (!targetValue.toString().equals(sourceValue.toString())) {
                return NOT_EQUAL;
            }
        }

        if (sameGetMethods.length == sourceMethods.length && sameGetMethods.length == targetMethods.length) {
            return PERFECT_EQUAL;
        } else if (sameGetMethods.length == sourceMethods.length || sameGetMethods.length == targetMethods.length) {
            return SUB_EQUAL;
        } else {
            return PART_EQUAL;
        }
    }

    public static <E1, E2> int compare(List<E1> sourceList, List<E2> targetList, boolean includeSuperClass) {
        if (sourceList == null || targetList == null) {
            throw new RuntimeException("Passed parameter is null.");
        }
        if (sourceList.isEmpty() && targetList.isEmpty()) {
            return PERFECT_EQUAL;
        }
        if (sourceList.isEmpty() || targetList.isEmpty()) {
            return NOT_EQUAL;
        }
        if (sourceList.size() != targetList.size()) {
            return NOT_EQUAL;
        }

        Method[] sourceMethods = null;
        Method[] targetMethods = null;
        String[] sameGetMethods = null;
        if (includeSuperClass) {
            sourceMethods = findAllMethod(sourceList.get(0).getClass());
            targetMethods = findAllMethod(targetList.get(0).getClass());
        } else {
            sourceMethods = sourceList.get(0).getClass().getDeclaredMethods();
            targetMethods = targetList.get(0).getClass().getDeclaredMethods();
        }
        sourceMethods = findGetMethod(sourceMethods);
        targetMethods = findGetMethod(targetMethods);
        sameGetMethods = findSameMethod(sourceMethods, targetMethods);

        if (sameGetMethods == null || sameGetMethods.length == 0) {
            return NOT_EQUAL;
        }

        List<String> sourceStringList = new ArrayList<>();
        List<String> targetStringList = new ArrayList<>();
        for (Object item : sourceList) {
            sourceStringList.add(toSerializeString(item, sameGetMethods));
        }
        for (Object item : targetList) {
            targetStringList.add(toSerializeString(item, sameGetMethods));
        }

        if (compare(sourceStringList, targetStringList) == NOT_EQUAL) {
            return NOT_EQUAL;
        } else if (sameGetMethods.length == sourceMethods.length && sameGetMethods.length == targetMethods.length) {
            return PERFECT_EQUAL;
        } else if (sameGetMethods.length == sourceMethods.length || sameGetMethods.length == targetMethods.length) {
            return SUB_EQUAL;
        } else {
            return PART_EQUAL;
        }
    }

    public static int compare(List<String> sourceList, List<String> targetList) {
        if (sourceList == null || targetList == null) {
            throw new RuntimeException("Passed parameter is null.");
        }
        if (sourceList.isEmpty() && targetList.isEmpty()) {
            return PERFECT_EQUAL;
        }
        if (sourceList.isEmpty() || targetList.isEmpty()) {
            return NOT_EQUAL;
        }
        if (sourceList.size() != targetList.size()) {
            return NOT_EQUAL;
        }

        Collections.sort(sourceList);
        Collections.sort(targetList);
        for (int i = 0; i < sourceList.size(); i++) {
            String sourceStringItem = sourceList.get(i);
            String targetStringItem = targetList.get(i);
            if (!sourceStringItem.equals(targetStringItem)) {
                return NOT_EQUAL;
            }
        }
        return PERFECT_EQUAL;
    }

    private static Method[] findAllMethod(Class<?> clazz) {
        if (clazz == null) {
            return new Method[] {};
        }
        Method[] thisMethods = clazz.getDeclaredMethods();
        if (clazz.getSuperclass() == null) {
            return thisMethods;
        }
        Method[] superMethods = findAllMethod(clazz.getSuperclass());
        if (superMethods.length == 0) {
            return thisMethods;
        }
        Method[] sumMethods = Arrays.copyOf(thisMethods, thisMethods.length + superMethods.length);
        System.arraycopy(superMethods, 0, sumMethods, thisMethods.length, superMethods.length);
        return sumMethods;
    }

    private static Method[] findGetMethod(Method[] source) {
        List<Method> methodList = new ArrayList<>();
        for (Method item : source) {
            if (item.getName().startsWith("get") && item.getParameterCount() == 0) {
                methodList.add(item);
            }
        }
        return methodList.toArray(new Method[methodList.size()]);
    }

    private static String[] findSameMethod(Method[] source, Method[] target) {
        List<String> methodList = new ArrayList<>();
        for (Method sourceItem : source) {
            for (Method targetItem : target) {
                if (sourceItem.getName().equals(targetItem.getName())) {
                    methodList.add(sourceItem.getName());
                    break;
                }
            }
        }
        return methodList.toArray(new String[methodList.size()]);
    }

    private static String toSerializeString(Object object, String[] methods) {
        StringBuilder sb = new StringBuilder();
        try {
            for (String item : methods) {
                sb.append(object.getClass().getMethod(item).invoke(object));
            }
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            throw new RuntimeException("Cannot found method");
        }
        return sb.toString();
    }

}
