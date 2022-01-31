package com.thierno.dropwizard.db.util;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class HibernateEntityManagerFactoryUtil {

	private static class EntityManagerHolder {

		private static final EntityManagerFactory ENTITY_MANAGER_FACTORY = new JpaEntityManagerFactory( Constants.ENTITY_CLASSES, Constants.NAMED_QUERIES_MAP ) //
				.getEntityManagerFactory();
	}

	public static EntityManager getJpaEntityManager() {
		return EntityManagerHolder.ENTITY_MANAGER_FACTORY.createEntityManager();
	}
}
