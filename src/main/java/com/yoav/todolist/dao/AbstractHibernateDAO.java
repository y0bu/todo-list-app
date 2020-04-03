package com.yoav.todolist.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * this class is simply but implementation of hibernate CRUD operations
 * **/
public abstract class AbstractHibernateDAO<T> {

    private Class<T> ourClass;

    /**
     * @implNote all the extends class that inherent this abstract class should set the class in the constructor
     * @see AccountMysqlImpl for example
     * **/
    public void setOurClass(Class<T> ourClass) {
        this.ourClass = ourClass;
    }

    @Autowired
    private SessionFactory sessionFactory;

    protected Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    public T getById(int id) {
        return (T) sessionFactory.getCurrentSession().get(ourClass, id);
    }

    public List<T> getAll() {
        return sessionFactory.getCurrentSession().createQuery("from " + ourClass.getName()).list();
    }

    public void save(T t) {
        sessionFactory.getCurrentSession().persist(t);
    }

    public T update(T t) {
        return (T) sessionFactory.getCurrentSession().merge(t);
    }

    public void delete(T t) {
        sessionFactory.getCurrentSession().delete(t);
    }

    public void deleteById(int id) {
        final T t = getById(id);
        delete(t);
        sessionFactory.getCurrentSession().flush();
    }
}