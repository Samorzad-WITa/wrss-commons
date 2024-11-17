package pl.wrss.wita.common.model.filter.binder;

import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;

public class PredicateBuilder<Q extends EntityPath> {

    public enum Grouping {
        And,
        Or
    }

    public interface PredicateProviderFromValue<Q extends EntityPath, V> {
        Predicate provide(Q path, V value);
    }

    public interface PredicateProviderWithoutValue<Q extends EntityPath> {
        Predicate provide(Q path);
    }

    private Predicate predicate;
    private final Q path;
    private final Grouping grouping;

    public PredicateBuilder(Q path) {
        this(path, Grouping.And, null);
    }

    public PredicateBuilder(Predicate predicate, Q path) {
        this(path, Grouping.And, predicate);
    }

    public PredicateBuilder(Q path, Grouping grouping) {
        this(path, grouping, null);
    }

    public PredicateBuilder(Q path, Grouping grouping, Predicate predicate) {
        this.predicate = predicate;
        this.path = path;
        this.grouping = grouping;
    }

    public Q getPath() {
        return path;
    }

    public <V extends Collection> PredicateBuilder<Q> addOptional(V value, PredicateProviderFromValue<Q, V> predicateProvider) {
        if (value.isEmpty()) {
            return this;
        }
        return add(predicateProvider.provide(path, value));
    }

    public <V> PredicateBuilder<Q> addOptional(V value, PredicateProviderFromValue<Q, V> predicateProvider) {
        return addOptional(Optional.ofNullable(value), predicateProvider);
    }

    public <V> PredicateBuilder<Q> addOptional(Optional<V> value, PredicateProviderFromValue<Q, V> predicateProvider) {
        if (value.isEmpty()) {
            return this;
        }
        return add(predicateProvider.provide(path, value.get()));
    }

    public PredicateBuilder<Q> add(Predicate predicate) {
        return combine(predicate);
    }

    protected PredicateBuilder<Q> combine(Predicate right) {
        predicate = switch (grouping) {
            case And -> ExpressionUtils.and(predicate, right);
            case Or -> ExpressionUtils.or(predicate, right);
            default -> throw new UnsupportedOperationException(String.format("Grouping %s is not supported.", grouping));
        };
        return this;
    }

    public PredicateBuilder<Q> or(Consumer<PredicateBuilder<Q>> builderConsumer) {
        var builder = new PredicateBuilder<>(path, Grouping.Or);
        builderConsumer.accept(builder);
        this.add(builder.build());
        return this;
    }

    public Predicate build() {
        if(predicate == null) {
            return switch (grouping) {
                case And -> Expressions.asBoolean(true).isTrue();
                case Or -> Expressions.asBoolean(true).isFalse();
                default -> throw new UnsupportedOperationException(String.format("Grouping %s not supported.", grouping));
            };
        }
        return predicate;
    }
}
