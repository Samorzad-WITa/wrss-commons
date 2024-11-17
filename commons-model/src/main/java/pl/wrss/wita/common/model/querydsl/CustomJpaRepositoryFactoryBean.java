package pl.wrss.wita.common.model.querydsl;

import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import pl.wrss.wita.common.model.filter.binder.FilterBinderProvider;

public class CustomJpaRepositoryFactoryBean<T extends Repository<S, I>, S, I> extends JpaRepositoryFactoryBean<T, S, I> {

    private FilterBinderProvider filterBinderProvider;

    public CustomJpaRepositoryFactoryBean(Class<? extends T> repositoryInterface) {
        super(repositoryInterface);
    }

    @Override
    protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {
        return new CustomJpaRepositoryFactory(entityManager, filterBinderProvider);
    }

    @Autowired
    public void setFilterBinderProvider(FilterBinderProvider filterBinderProvider) {
        this.filterBinderProvider = filterBinderProvider;
    }
}
