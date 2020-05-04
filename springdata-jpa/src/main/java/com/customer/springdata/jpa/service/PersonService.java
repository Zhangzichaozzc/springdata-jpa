package com.customer.springdata.jpa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.customer.springdata.jpa.entity.Person;
import com.customer.springdata.jpa.repository.PersonRepository;

/**
 * PersonService
 *
 * @author Zichao Zhang
 * @date 2020/5/4
 */
@Service
public class PersonService {

    @Autowired
    private PersonRepository personRepository;

    @Transactional
    public void updatePerson(String lastName, String email) {
        personRepository.updatePerson(lastName, email);
    }

    /**
     * 在使用 CRUDRepository.save 方法的时候，需要将配置的 JpaTransactionManager 命名为"transactionManager", 否则会报错
     */
    @Transactional
    public void batchInsert(Iterable<Person> persons) {
        personRepository.save(persons);
    }

}
