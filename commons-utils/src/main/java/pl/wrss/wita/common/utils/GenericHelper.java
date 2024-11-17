package pl.wrss.wita.common.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

public class GenericHelper {
    private GenericHelper() {
    }

    public static <T> Class<T> getArgumentType(Class subjectType, String typeName) {
        return getArgumentType(subjectType, subjectType.getSuperclass(), typeName, null);
    }

    public static <T> Class<T> getArgumentType(Class subjectType, Integer typeIndex) {
        return getArgumentType(subjectType, subjectType.getSuperclass(), null, typeIndex);
    }

    public static <T> Class<T> getArgumentType(Class subjectType, Class parentType, String typeName, Integer typeIndex) {
        ParameterizedType parameterizedType = null;
        Type[] types;
        if (parentType.isInterface()) {
            types = subjectType.getGenericInterfaces();
            int var5 = types.length;

            for(int var6 = 0; var6 < var5; ++var6) {
                Type interfaceType = types[var6];
                if (interfaceType instanceof ParameterizedType) {
                    ParameterizedType interfaceParameterizedType = (ParameterizedType)interfaceType;
                    if (interfaceParameterizedType.getRawType().equals(parentType)) {
                        parameterizedType = interfaceParameterizedType;
                        break;
                    }
                }
            }
        } else {
            Type superType = subjectType.getGenericSuperclass();
            if (superType instanceof ParameterizedType && ((ParameterizedType)superType).getRawType().equals(parentType)) {
                parameterizedType = (ParameterizedType)superType;
            }
        }

        if (parameterizedType == null) {
            throw new IllegalArgumentException(String.format("Could not find parameterized type information for parent type %s of type %s.", parentType, subjectType));
        } else {
            types = parameterizedType.getActualTypeArguments();
            Class rawType = (Class)parameterizedType.getRawType();
            TypeVariable<Class>[] typeVariables = rawType.getTypeParameters();

            for(int i = 0; i < typeVariables.length; ++i) {
                TypeVariable<Class> typeVariable = typeVariables[i];
                String tn = typeVariable.getName();
                if ((typeName == null || typeName.equals(tn)) && (typeIndex == null || typeIndex == i)) {
                    return (Class)types[i];
                }
            }

            throw new IllegalArgumentException(String.format("Type variable with name '%s' and/or index '%s' not found.", typeName, typeIndex));
        }
    }
}
