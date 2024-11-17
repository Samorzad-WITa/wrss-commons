package pl.wrss.wita.common.mapping;

import javassist.*;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.text.WordUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MappingPropertiesAccessorBuilder {

    private static final Map<String, Class> CLASS_CACHE = new HashMap<>();
    private final static Map<Class, Method> unboxTypeMethodMap = new HashMap<>();

    private static final Lock buildLock = new ReentrantLock();

    private final ClassPool classPool;

    static {
        try {
            unboxTypeMethodMap.put(Boolean.TYPE, Boolean.class.getMethod("booleanValue"));
            unboxTypeMethodMap.put(Byte.TYPE, Byte.class.getMethod("byteValue"));
            unboxTypeMethodMap.put(Short.TYPE, Short.class.getMethod("shortValue"));
            unboxTypeMethodMap.put(Integer.TYPE, Integer.class.getMethod("intValue"));
            unboxTypeMethodMap.put(Long.TYPE, Long.class.getMethod("longValue"));
            unboxTypeMethodMap.put(Float.TYPE, Float.class.getMethod("floatValue"));
            unboxTypeMethodMap.put(Double.TYPE, Double.class.getMethod("doubleValue"));
        } catch (NoSuchMethodException | SecurityException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public MappingPropertiesAccessorBuilder() {
        this(new ClassPool(ClassPool.getDefault()));
    }

    public MappingPropertiesAccessorBuilder(ClassPool classPool) {
        this.classPool = classPool;
    }

    protected String getImplementationName(Class<?> accessorType) {
        return accessorType.getName() + "Impl";
    }

    protected CtClass resolveClass(Class<?> clazz) throws Exception {
        return classPool.getCtClass(clazz.getName());
    }

    public <T> Class<? extends T> buildClass(Class<T> accessorType) throws Exception {
        buildLock.lock();
        try {
            var implementationName = getImplementationName(accessorType);
            if (CLASS_CACHE.containsKey(implementationName)) {
                return CLASS_CACHE.get(implementationName);
            }
            CtClass ctClass = classPool.getOrNull(implementationName);
            if (ctClass == null) {
                ctClass = classPool.makeClass(implementationName);
                ctClass.setSuperclass(resolveClass(accessorType));
            }
            createPropertiesField(ctClass);
            createConstructor(accessorType, ctClass);
            createGetMethod(ctClass);
            createSetMethod(ctClass);
            createHasMethod(ctClass);
            createAsAccessorMethod(ctClass);
            createWrapMethod(ctClass);
            createUnwrapMethod(ctClass);
            implementProperties(accessorType, ctClass);
            Class implementationType = ctClass.toClass(accessorType);
            CLASS_CACHE.put(implementationName, implementationType);
            return (Class<T>)implementationType;
        } finally {
            buildLock.unlock();
        }
    }

    protected void implementProperties(Class<?> accessorType, CtClass ctClass) throws Exception {
        for (var field : accessorType.getDeclaredFields()) {
            implementProperty(accessorType, field, ctClass);
        }
    }

    protected void implementProperty(Class<?> accessorType, Field field, CtClass ctClass) throws Exception {
        overrideGetter(accessorType, field, ctClass);
        overrideSetter(accessorType, field, ctClass);
    }

    protected String getPropertyName(Field field) {
        return field.getDeclaringClass().getCanonicalName() + "." + field.getName();
    }

    protected CtMethod createSetMethod(CtClass ctClass) throws Exception {
        CtMethod ctMethod = CtMethod.make("public void set(java.lang.String property, java.lang.Object value) { this._properties.set(property, value); }", ctClass);
        ctClass.addMethod(ctMethod);
        return ctMethod;
    }

    protected CtMethod createGetMethod(CtClass ctClass) throws Exception {
        CtMethod ctMethod = CtMethod.make("public java.lang.Object get(java.lang.String property) { return this._properties.get(property); }", ctClass);
        ctClass.addMethod(ctMethod);
        return ctMethod;
    }

    protected CtMethod createHasMethod(CtClass ctClass) throws Exception {
        CtMethod ctMethod = CtMethod.make("public boolean has(java.lang.String property) { return this._properties.has(property); }", ctClass);
        ctClass.addMethod(ctMethod);
        return ctMethod;
    }

    protected CtMethod createWrapMethod(CtClass ctClass) throws Exception {
        CtMethod ctMethod = CtMethod.make("public " + MappingProperties.class.getCanonicalName() + " wrap() { return this._properties.wrap(); }", ctClass);
        ctClass.addMethod(ctMethod);
        return ctMethod;
    }

    protected CtMethod createUnwrapMethod(CtClass ctClass) throws Exception {
        CtMethod ctMethod = CtMethod.make("public " + MappingProperties.class.getCanonicalName() + " unwrap() { return this._properties.unwrap(); }", ctClass);
        ctClass.addMethod(ctMethod);
        return ctMethod;
    }

    protected CtMethod createAsAccessorMethod(CtClass ctClass) throws Exception {
        CtMethod ctMethod = CtMethod.make("public " + MappingProperties.class.getCanonicalName() + " as(java.lang.Class accessorType) { return this._properties.as(accessorType); }", ctClass);
        ctClass.addMethod(ctMethod);
        return ctMethod;
    }

    protected CtField createPropertiesField(CtClass ctClass) {
        var builder = new StringBuilder();
        builder.append("private final ")
                .append(MappingProperties.class.getCanonicalName())
                .append(" _properties;");
        CtField ctField;
        try {
            ctField = CtField.make(builder.toString(), ctClass);
            ctClass.addField(ctField);
        } catch (CannotCompileException ex) {
            throw new IllegalStateException(ex);
        }
        return ctField;
    }

    protected CtConstructor createConstructor(Class<?> accessorType, CtClass ctClass) throws Exception {
        var builder = new StringBuilder();
        builder.append("public ")
                .append(accessorType.getSimpleName())
                .append("Impl(")
                .append(MappingProperties.class.getCanonicalName())
                .append(" properties) {\n")
                .append("\tthis._properties = properties;\n")
                .append("}");
        CtConstructor ctConstructor;
        ctConstructor = CtNewConstructor.make(builder.toString(), ctClass);
        ctClass.addConstructor(ctConstructor);
        return ctConstructor;
    }

    protected CtMethod overrideSetter(Class<?> accessorType, Field field, CtClass ctClass) throws Exception {
        var fieldType = field.getType();
        var fieldName = field.getName();
        var setterName = "set" + WordUtils.capitalize(fieldName);

        Method setter;
        try {
            setter = accessorType.getDeclaredMethod(setterName, fieldType);
        } catch (NoSuchMethodException ex) {
            throw new IllegalStateException(ex);
        }

        var builder = new StringBuilder();
        builder.append("public ");
        if (setter.getReturnType().isAssignableFrom(accessorType)) {
            builder.append(setter.getReturnType().getCanonicalName());
        }
        else {
            builder.append("void");
        }
        var valueExpression = fieldName;
        if (fieldType.isPrimitive()) {
            Class wrapperType = ClassUtils.primitiveToWrapper(fieldType);
            valueExpression = wrapperType.getCanonicalName() + ".valueOf(" + valueExpression + ")";
        }
        builder.append(" ")
                .append(setterName)
                .append("(")
                .append(fieldType.getCanonicalName())
                .append(" ")
                .append(fieldName)
                .append(") {\n")
                .append("\tthis._properties.set(\"")
                .append(getPropertyName(field))
                .append("\", ")
                .append(valueExpression)
                .append(");\n");
        if (setter.getReturnType().isAssignableFrom(accessorType)) {
            builder.append("return this;");
        }
        builder.append("}");
        CtMethod ctMethod;
        ctMethod = CtMethod.make(builder.toString(), ctClass);
        ctClass.addMethod(ctMethod);
        return ctMethod;
    }

    protected CtMethod overrideGetter(Class<?> accessorType, Field field, CtClass ctClass) throws Exception {
        var fieldType = field.getType();
        var fieldName = field.getName();
        var getterName = (Boolean.TYPE.equals(field.getType()) ? "is" : "get") + WordUtils.capitalize(fieldName);

        try {
            accessorType.getDeclaredMethod(getterName);
        } catch (NoSuchMethodException ex) {
            throw new IllegalStateException(ex);
        }

        var valueExpression = "(" + ClassUtils.primitiveToWrapper(fieldType).getCanonicalName() + ")this._properties.get(\"" + getPropertyName(field) + "\")";

        var builder = new StringBuilder();
        builder.append("public ")
                .append(fieldType.getCanonicalName())
                .append(" ")
                .append(getterName)
                .append("() {\n")
                .append("\tif (!this._properties.has(\"")
                .append(getPropertyName(field))
                .append("\")) {\n")
                .append("\t\treturn super.")
                .append(getterName)
                .append("();\n")
                .append("\t}\n")
                .append("\treturn ");
        if (fieldType.isPrimitive()) {
            Method unboxMethod = unboxTypeMethodMap.get(fieldType);
            builder.append("(")
                    .append(valueExpression)
                    .append(").")
                    .append(unboxMethod.getName())
                    .append("()");
        } else {
            builder.append(valueExpression);
        }
        builder.append(";\n")
                .append("}");
        CtMethod ctMethod;
        ctMethod = CtMethod.make(builder.toString(), ctClass);
        ctClass.addMethod(ctMethod);
        return ctMethod;
    }
}
