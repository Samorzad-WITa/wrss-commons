package pl.wrss.wita.common.model.querydsl;

import jakarta.persistence.EntityManager;

public interface EntityManagerProvider {

    EntityManager getEntityManager();
}
