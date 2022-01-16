package com.thierno.dropwizard.service.impl;

import com.thierno.dropwizard.db.util.HibernateUtil;
import com.thierno.dropwizard.domain.entity.Message;
import com.thierno.dropwizard.service.MessageService;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageServiceImpl implements MessageService {

	Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);

	@Override
	public void saveMessag( String messageValue ) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		Message message = new Message();
		message.setMessage( messageValue );
		session.save( message );
		session.getTransaction().commit();
		session.close();
		logger.info( "logging using logback" );
	}
}
