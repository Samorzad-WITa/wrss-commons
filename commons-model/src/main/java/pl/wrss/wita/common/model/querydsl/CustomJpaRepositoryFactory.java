package pl.wrss.wita.common.model.querydsl;

import jakarta.persistence.EntityManager;
import org.springframework.data.jpa.repository.support.CrudMethodMetadata;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.querydsl.EntityPathResolver;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryComposition;
import org.springframework.data.repository.core.support.RepositoryFragment;
import pl.wrss.wita.common.model.entity.EntityBase;
import pl.wrss.wita.common.model.filter.binder.FilterBinderProvider;

public class CustomJpaRepositoryFactory extends JpaRepositoryFactory {

    private final FilterBinderProvider filterBinderProvider;

    public CustomJpaRepositoryFactory(EntityManager entityManager, FilterBinderProvider filterBinderProvider) {
        super(entityManager);
        this.filterBinderProvider = filterBinderProvider;
    }

    @Override
    protected RepositoryComposition.RepositoryFragments getRepositoryFragments(RepositoryMetadata metadata, EntityManager entityManager, EntityPathResolver resolver, CrudMethodMetadata crudMethodMetadata) {
        RepositoryComposition.RepositoryFragments fragments = super.getRepositoryFragments(metadata, entityManager, resolver, crudMethodMetadata);
        var entityInformation = (JpaEntityInformation<? extends EntityBase, ?>) super.getEntityInformation(metadata.getDomainType());

        if(FilterExecutor.class.isAssignableFrom(metadata.getRepositoryInterface()) && EntityBase.class.isAssignableFrom(metadata.getDomainType())) {
            var filterExecutorFragment = new FilterJpaExecutor<>(entityInformation, entityManager, resolver, crudMethodMetadata, filterBinderProvider.getFilterBinder(metadata.getDomainType()));
            fragments = fragments.append(RepositoryFragment.implemented(filterExecutorFragment));
        }
        if(EntityManagerProvider.class.isAssignableFrom(metadata.getRepositoryInterface())) {
            var entityManagerProviderFragment = new DefaultEntityManagerProvider(entityManager);
            fragments = fragments.append(RepositoryFragment.implemented(entityManagerProviderFragment));
        }
        if(PathProvider.class.isAssignableFrom(metadata.getRepositoryInterface())) {
            var pathProviderFragment = new DefaultPathProvider<>(entityInformation, resolver);
            fragments = fragments.append(RepositoryFragment.implemented(pathProviderFragment));
        }

        return fragments;
    }
}
