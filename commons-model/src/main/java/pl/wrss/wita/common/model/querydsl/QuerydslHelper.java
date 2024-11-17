package pl.wrss.wita.common.model.querydsl;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.EntityPathBase;
import org.reflections.Reflections;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import pl.wrss.wita.common.model.entity.EntityBase;
import pl.wrss.wita.common.utils.GenericHelper;
import pl.wrss.wita.common.model.entity.QEntityBase;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

@Component
public class QuerydslHelper implements InitializingBean {



    private final Map<Class<? extends EntityBase>, Constructor> entitySuperPathConstructorMap = new HashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        var reflections = new Reflections(EntityPathBase.class, EntityBase.class);
        var qTypes = reflections.getSubTypesOf(EntityPathBase.class);
        for (var qType : qTypes) {
            if (qType.getTypeParameters().length > 0) {
                continue;
            }
            Class entityType = GenericHelper.getArgumentType(qType, 0);
            if (!EntityBase.class.isAssignableFrom(entityType)) {
                continue;
            }
            Field superField;
            try {
                superField = qType.getDeclaredField("_super");
            } catch (NoSuchFieldException ex) {
                continue;
            }
            Class superPathType = superField.getType();
            if (QEntityBase.class.equals(superPathType)) {
                continue;
            }
            Constructor superPathConstructor = superPathType.getConstructor(Path.class);
            entitySuperPathConstructorMap.put(entityType, superPathConstructor);
        }
    }

    public EntityPathBase<?> getSuperPath(EntityPathBase path) {
        Class<?> entityType = path.getType();
        Constructor superPathConstructor = entitySuperPathConstructorMap.get(entityType);
        try {
            return (EntityPathBase<?>)superPathConstructor.newInstance(path);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException ex) {
            throw new IllegalStateException(String.format("Could not get super path for entity '%s'.", entityType), ex);
        }
    }
}
