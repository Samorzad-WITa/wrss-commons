package pl.wrss.wita.common.mapping;

public interface MappingProperties {

    default Object get(String property) {
        return get(property, null);
    };

    Object get(String property, Object defaultValue);

    void set(String property, Object value);

    boolean has(String property);

    <T extends MappingProperties> T as(Class<T> accessorType);

    MappingProperties wrap();

    MappingProperties unwrap();

    default <T extends MappingProperties> T wrapAs(Class<T> accessorType) {
        return wrap().as(accessorType);
    }
}
