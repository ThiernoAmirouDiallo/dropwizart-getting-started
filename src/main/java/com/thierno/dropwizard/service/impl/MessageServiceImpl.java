package com.thierno.dropwizard.service.impl;

import com.thierno.dropwizard.db.util.HibernateUtil;
import com.thierno.dropwizard.entity.Message;
import com.thierno.dropwizard.service.MessageService;

import org.hibernate.Session;

public class MessageServiceImpl implements MessageService {

	@Override
	public void saveMessag( String messageValue ) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		Message message = new Message();
		message.setMessage( messageValue );
		session.save( message );
		session.getTransaction().commit();
		session.close();
	}
}
