package pl.wrss.wita.common.model.hibernate;

import org.hibernate.generator.GeneratorCreationContext;
import org.hibernate.id.factory.spi.CustomIdGeneratorCreationContext;
import org.hibernate.id.uuid.UuidGenerator;

import java.lang.reflect.Member;

public class PreservingUUIDGenerator extends UuidGenerator {
    public PreservingUUIDGenerator(org.hibernate.annotations.UuidGenerator config, Member idMember, CustomIdGeneratorCreationContext creationContext) {
        super(config, idMember, creationContext);
    }

    public PreservingUUIDGenerator(org.hibernate.annotations.UuidGenerator config, Member member, GeneratorCreationContext creationContext) {
        super(config, member, creationContext);
    }
}
