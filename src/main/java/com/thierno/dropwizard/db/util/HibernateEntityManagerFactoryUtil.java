package com.thierno.dropwizard.db.util;

import javax.persistence.EntityManager;

public class HibernateEntityManagerFactoryUtil {

	private static class EntityManagerHolder {

		private static final EntityManager ENTITY_MANAGER = new JpaEntityManagerFactory( Constants.ENTITY_CLASSES ).getEntityManager();
	}

	public static EntityManager getJpaEntityManager() {
		return EntityManagerHolder.ENTITY_MANAGER;
	}
}
