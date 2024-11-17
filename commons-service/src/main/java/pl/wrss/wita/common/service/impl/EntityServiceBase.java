package pl.wrss.wita.common.service.impl;

import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import pl.wrss.wita.common.model.entity.EntityBase;
import pl.wrss.wita.common.model.querydsl.EntityRepository;
import pl.wrss.wita.common.service.EntityService;

import java.util.Collection;
import java.util.UUID;

public abstract class EntityServiceBase<E extends EntityBase> implements EntityService<E> {

    private final EntityRepository<E> repository;

    public EntityServiceBase(EntityRepository<E> repository) {
        this.repository = repository;
    }

    @Override
    public E findById(UUID id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public Collection<E> getAll() {
        return repository.findAll();
    }

    @Override
    public <T extends E> T save(T entity) {
        return repository.save(entity);
    }

    @Override
    public <T extends E> T saveAndFlush(T entity) {
        return repository.saveAndFlush(entity);
    }

    @Override
    public void delete(E entity) {
        repository.delete(entity);
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public JPAQuery<?> createQuery() {
        return new JPAQuery<>(getEntityManager());
    }

    public EntityRepository<E> getRepository() {
        return this.repository;
    }


    @Override
    public EntityManager getEntityManager() {
        return repository.getEntityManager();
    }

    @Override
    public <T> T getReference(Class<T> type, UUID id) {
        return getEntityManager().getReference(type, id);
    }

    @Override
    public E getReference(UUID id) {
        return repository.getReferenceById(id);
    }
}
