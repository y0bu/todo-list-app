package com.yoav.todolist.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@ComponentScan("com.yoav.todolist")
public class ApplicationConfig {

    /*
    * a session factory for hibernate operation
    * */
    @Bean(name = {"entityManagerFactory", "sessionFactory"})
    public LocalSessionFactoryBean sessionFactory() {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource());
        sessionFactory.setPackagesToScan("com.yoav.todolist");
        sessionFactory.setHibernateProperties(hibernateProperties());
        return sessionFactory;
    }

    /*
    * the data source with the username and password and url and driver to make the connection to the database
    * */
    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUsername("java");
        dataSource.setPassword("password");
        dataSource.setUrl("jdbc:mysql://localhost:3306/todo_list");
        return dataSource;
    }

    /*
    * i don't really know what this function is really
    * */
    @Bean    
    public PlatformTransactionManager hibernateTransactionManager() {
        HibernateTransactionManager transactionManager = new HibernateTransactionManager();
        transactionManager.setSessionFactory(sessionFactory().getObject());        
        return transactionManager;    
    }    

    /*
    * hibernate properties like auto creating database if not exist and more properties
    * */
    private final Properties hibernateProperties() {
        Properties hibernateProperties = new Properties();       
        hibernateProperties.setProperty("hibernate.hbm2ddl.auto", "update");
        hibernateProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");
        hibernateProperties.setProperty("show-sql", "true");
        return hibernateProperties;  
    }
}
