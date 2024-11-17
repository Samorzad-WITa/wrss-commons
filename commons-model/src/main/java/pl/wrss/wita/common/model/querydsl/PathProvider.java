package pl.wrss.wita.common.model.querydsl;

import com.querydsl.core.types.EntityPath;

public interface PathProvider<T> {

    EntityPath<T> getPath();
}
