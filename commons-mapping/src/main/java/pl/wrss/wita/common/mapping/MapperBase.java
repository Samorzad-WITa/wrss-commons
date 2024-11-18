package pl.wrss.wita.common.mapping;

import pl.wrss.wita.common.model.entity.scheme.IdSupport;
import pl.wrss.wita.common.utils.GenericHelper;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public abstract class MapperBase<S, D, P extends MappingProperties> implements Mapper<S, D, P> {

    private final Class<S> sourceType;
    private final Class<D> destinationType;
    private final Class<P> propertiesType;

    public MapperBase() {
        sourceType = GenericHelper.getArgumentType(getClass(), "S");
        destinationType = GenericHelper.getArgumentType(getClass(), "D");
        propertiesType = GenericHelper.getArgumentType(getClass(), "P");
    }

    @Override
    public Class<S> getSourceType() {
        return sourceType;
    }

    @Override
    public Class<D> getDestinationType() {
        return destinationType;
    }

    @Override
    public Class<P> getPropertiesType() {
        return propertiesType;
    }

    @Override
    public D map(S source, MappingProperties properties) {
        if(source == null) {
            return null;
        }
        var destination = createDestination(source);
        transform(source, destination, properties.wrapAs(propertiesType));
        return destination;
    }

    @Override
    public void map(S source, D destination, MappingProperties properties) {
        transform(source, destination, properties.wrapAs(propertiesType));
    }

    @Override
    public <Sx, Dx, Px extends MappingProperties> void map(Consumer<Dx> setter, Sx source, Mapper<Sx, Dx, Px> mapper, MappingProperties properties) {
        var destination = source != null ? mapper.createDestination(source) : null;
        if(source != null) {
            mapper.transform(source, destination, properties.wrapAs(mapper.getPropertiesType()));
        }
        setter.accept(destination);
    }

    @Override
    public <Sx, Dx, Px extends MappingProperties> void map(Consumer<Dx[]> setter, Iterable<Sx> source, Mapper<Sx, Dx, Px> mapper, MappingProperties properties) {
        Collection<Dx> mapped = mapper.map(source, properties.wrapAs(mapper.getPropertiesType()));
        Dx[] array = mapped.toArray((Dx[]) Array.newInstance(mapper.getDestinationType(), 0));
        setter.accept(array);
    }

    @Override
    public <Sx, Dx, Px extends MappingProperties> void mapToList(Consumer<List<Dx>> setter, Iterable<Sx> source, Mapper<Sx, Dx, Px> mapper, MappingProperties properties) {
        List<Dx> mapped = mapper.map(source, properties.wrapAs(mapper.getPropertiesType()));
        setter.accept(mapped);
    }

    @Override
    public List<D> map(Iterable<? extends S> source, MappingProperties properties) {
        if (source == null) {
            return null;
        }
        List<D> mapped = new ArrayList<>();
        source.forEach(s -> mapped.add(map(s, properties)));
        return mapped;
    }

    @Override
    public D createDestination(S source) {
        return createDestination(destinationType);
    }

    public <T> T createDestination(Class<T> destinationType) {
        try {
            return (T)destinationType.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
            throw new IllegalStateException("Could not create destination instance", ex);
        }
    }

    @Override
    public void transform(S source, D destination, P properties) {
        if(source instanceof IdSupport src && destination instanceof IdSupport dst) {
            dst.setId(src.getId());
        }
    }
}
