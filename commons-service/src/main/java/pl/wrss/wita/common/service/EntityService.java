package pl.wrss.wita.common.service;

import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import pl.wrss.wita.common.model.entity.EntityBase;

import java.util.Collection;
import java.util.UUID;

public interface EntityService<E extends EntityBase> {

    E findById(UUID id);

    Collection<E> getAll();

    <T extends E> T save(T entity);

    <T extends E> T saveAndFlush(T entity);

    void delete(E entity);

    void deleteById(UUID id);

    JPAQuery<?> createQuery();

    EntityManager getEntityManager();

    E getReference(UUID id);

    <T> T getReference(Class<T> type, UUID id);
}
