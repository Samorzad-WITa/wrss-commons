package pl.wrss.wita.common.model.querydsl;

import com.querydsl.core.types.EntityPath;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.querydsl.EntityPathResolver;

public class DefaultPathProvider<T> implements PathProvider<T> {

    private final EntityPath<T> path;

    public DefaultPathProvider(JpaEntityInformation<T, ?> entityInformation, EntityPathResolver resolver) {
        this.path = resolver.createPath(entityInformation.getJavaType());
    }

    @Override
    public EntityPath<T> getPath() {
        return path;
    }
}
