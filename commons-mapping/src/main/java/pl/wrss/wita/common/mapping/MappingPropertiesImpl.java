package pl.wrss.wita.common.mapping;

import java.util.HashMap;
import java.util.Map;

public class MappingPropertiesImpl implements MappingProperties {

    private final MappingProperties parent;
    private final Map<String, Object> store = new HashMap<>();
    private final MappingPropertiesAccessorBuilder accessorBuilder;

    public MappingPropertiesImpl(MappingPropertiesAccessorBuilder accessorBuilder) {
        this(null, accessorBuilder);
    }

    public MappingPropertiesImpl(MappingProperties parent, MappingPropertiesAccessorBuilder accessorBuilder) {
        this.parent = parent;
        this.accessorBuilder = accessorBuilder;
    }

    @Override
    public Object get(String property, Object defaultValue) {
        if (store.containsKey(property)) {
            return store.get(property);
        }
        if (parent != null) {
            return parent.get(property, defaultValue);
        }
        return defaultValue;
    }

    @Override
    public void set(String property, Object value) {
        store.put(property, value);
    }

    @Override
    public boolean has(String property) {
        if (store.containsKey(property)) {
            return true;
        }
        if (parent != null) {
            return parent.has(property);
        }
        return false;
    }

    @Override
    public <T extends MappingProperties> T as(Class<T> accessorType) {
        try {
            return accessorBuilder.buildClass(accessorType).getConstructor(MappingProperties.class).newInstance(this);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public MappingProperties wrap() {
        return new MappingPropertiesImpl(this, accessorBuilder);
    }

    public MappingProperties unwrap() {
        return parent;
    }
}
