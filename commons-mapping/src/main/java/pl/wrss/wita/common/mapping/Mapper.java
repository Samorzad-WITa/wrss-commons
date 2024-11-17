package pl.wrss.wita.common.mapping;

import java.util.List;
import java.util.function.Consumer;

public interface Mapper<S, D, P extends MappingProperties> {

    Class<S> getSourceType();
    Class<D> getDestinationType();
    Class<P> getPropertiesType();

    D map(S source, MappingProperties properties);
    void map(S source, D destination, MappingProperties mappingProperties);

    <Sx, Dx, Px extends MappingProperties> void map(Consumer<Dx> setter, Sx source, Mapper<Sx, Dx, Px> mapper, MappingProperties properties);
    <Sx, Dx, Px extends MappingProperties> void map(Consumer<Dx[]> setter, Iterable<Sx> source, Mapper<Sx, Dx, Px> mapper, MappingProperties properties);
    <Sx, Dx, Px extends MappingProperties> void mapToList(Consumer<List<Dx>> setter, Iterable<Sx> source, Mapper<Sx, Dx, Px> mapper, MappingProperties properties);
    List<D> map(Iterable<? extends S> source, MappingProperties properties);

    void transform(S source, D destination, P properties);
    D createDestination(S source);
}
