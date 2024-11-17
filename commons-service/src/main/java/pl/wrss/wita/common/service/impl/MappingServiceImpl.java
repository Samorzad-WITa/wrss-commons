package pl.wrss.wita.common.service.impl;

import org.springframework.stereotype.Service;
import pl.wrss.wita.common.mapping.MappingProperties;
import pl.wrss.wita.common.mapping.MappingPropertiesAccessorBuilder;
import pl.wrss.wita.common.mapping.MappingPropertiesImpl;
import pl.wrss.wita.common.service.MappingService;

@Service
public class MappingServiceImpl implements MappingService {

    private final MappingPropertiesAccessorBuilder propertiesAccessorBuilder = new MappingPropertiesAccessorBuilder();

    @Override
    public <T extends MappingProperties> T createProperties(Class<T> propertiesType) {
        try {
            return propertiesAccessorBuilder.buildClass(propertiesType).getConstructor(MappingProperties.class).newInstance(new MappingPropertiesImpl(propertiesAccessorBuilder));
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }
}
