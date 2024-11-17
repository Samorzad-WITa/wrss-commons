package pl.wrss.wita.common.model.filter.binder;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class FilterBinderProvider implements InitializingBean {

    private final ApplicationContext applicationContext;
    private final Map<Class<?>, FilterBinder> entityFilterBinderMap = new HashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        for(FilterBinderBase filterBinder : applicationContext.getBeansOfType(FilterBinderBase.class).values()) {
            Class<?> entityType = filterBinder.getEntityType();
            entityFilterBinderMap.put(entityType, filterBinder);
        }
    }

    public FilterBinder getFilterBinder(Class<?> entityType) {
        return entityFilterBinderMap.get(entityType);
    }
}
