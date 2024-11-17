package pl.wrss.wita.common.service;


import pl.wrss.wita.common.mapping.MappingProperties;

public interface MappingService {

    <T extends MappingProperties> T createProperties(Class<T> propertiesType);
}
