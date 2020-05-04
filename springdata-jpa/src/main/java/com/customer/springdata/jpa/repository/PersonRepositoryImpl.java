package com.customer.springdata.jpa.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.customer.springdata.jpa.entity.Person;

/**
 * PersonRepositoryImpl
 *
 * @author Zichao Zhang
 * @date 2020/5/4
 */
public class PersonRepositoryImpl implements PersonDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void test() {
        Person person = entityManager.find(Person.class, 1);
        System.out.println("person --> " + person);
    }
}
