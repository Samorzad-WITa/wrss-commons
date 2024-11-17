package pl.wrss.wita.common.model.querydsl;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import pl.wrss.wita.common.model.entity.EntityBase;

import java.util.UUID;

@NoRepositoryBean
public interface EntityRepository <T extends EntityBase> extends JpaRepository<T, UUID>, EntityManagerProvider, PathProvider<T>{
}
