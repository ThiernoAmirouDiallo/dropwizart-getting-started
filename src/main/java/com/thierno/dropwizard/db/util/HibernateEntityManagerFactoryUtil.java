package com.thierno.dropwizard.db.util;

import com.thierno.dropwizard.domain.entity.Message;

import javax.persistence.EntityManager;

public class HibernateEntityManagerFactoryUtil {

	private static class EntityManagerHolder {

		private static final EntityManager ENTITY_MANAGER = new JpaEntityManagerFactory( new Class[] { Message.class } ).getEntityManager();
	}

	public static EntityManager getJpaEntityManager() {
		return EntityManagerHolder.ENTITY_MANAGER;
	}
}
