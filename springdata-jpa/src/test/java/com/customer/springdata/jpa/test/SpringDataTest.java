package com.customer.springdata.jpa.test;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.sql.DataSource;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.customer.springdata.jpa.entity.Person;
import com.customer.springdata.jpa.repository.PersonRepository;
import com.customer.springdata.jpa.service.PersonService;

/**
 * SpringDataTest
 *
 * @author Zichao Zhang
 * @date 2020/5/4
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
public class SpringDataTest {

    @Test
    public void testJpa() {

    }

    @Autowired
    private DataSource dataSource;

    @Test
    public void testDataSource() throws SQLException {
        System.out.println("dataSource.getConnection() = " + dataSource.getConnection());
    }

    @Autowired
    private PersonRepository personRepository;

    @Test
    public void testPersonRepository() {

        System.out.println("personRepository.getByLastName(\"AA\") = " + personRepository.getByLastName("AA"));
    }

    @Test
    public void testRepository() {
        System.out.println("personRepository.getByLastNameStartingWithAndIdGreaterThanEqual(\"I\", 9) = "
                + personRepository.getByLastNameStartingWithAndIdGreaterThanEqual("I", 9));
    }

    @Test
    public void testRepository2() {
        System.out.println("personRepository.getAllByIdIn(Arrays.asList(1,2,3,4,5,6)) = "
                + personRepository.getAllByIdIn(Arrays.asList(1, 2, 3, 4, 5, 6)));
    }

    @Test
    public void testKeyWords2() {
        System.out.println("personRepository.getByAddressIdGreaterThan(2) = " + personRepository.getByAddress_IdGreaterThan(2));
    }

    @Test
    public void testQuery() {
        System.out.println(personRepository.getMaxIdPerson());
    }

    @Test
    public void testQueryParameter1() {
        System.out.println(personRepository.getPersonsQueryParameter("AA", "aa@customer.com"));
    }

    @Test
    public void testQueryNamedParameter() {
        System.out.println(personRepository.getPersonQueryNamedParameter("bb@customer.com", "BB"));
    }

    @Test
    public void testQueryParameterLike() {
        System.out.println(personRepository.getPersonQueryParameterLike("A", "bb"));
    }

    @Test
    public void testQueryNamedParamterLike() {
        System.out.println(personRepository.getPersonQueryNamedParameterLike("cc", "D"));
    }

    @Test
    public void testNativeQuery() {
        System.out.println("personRepository.getCount() = " + personRepository.getCount());
    }

    @Autowired
    private PersonService personService;

    @Test
    public void testUpdate() {
        personService.updatePerson("AA", "aaaaa@customer.com");
    }

    @Test
    public void testBatchInsert() {
        List<Person> list = new ArrayList<>();
        for (int i = 'a'; i < 'z'; i++) {
            Person person = new Person((char) (i - 32) + "" + (char) (i - 32) + "", ((char) i) + "" + ((char) i) +
                    "@customer.com",
                    new Date());
            person.setAddressId(i);
            list.add(person);
        }
        personService.batchInsert(list);
    }

    /**
     * PagingAndSortingRepository.findAll(Pageable pageable) 方法可以实现通用的分页查询
     * 缺点:
     * 不能是写带查询条件的 分页（待查询条件的分页查询需要使用 JpaSpecificationExecutor 接口来完成）
     */
    @Test
    public void testTestPagingAndSorting() {
        int pageNo = 3;
        // 在 PageRequest 接口中, page 是从 0 开始计数的
        int page = pageNo - 1;
        // pageSize 是每页的容量
        int pageSize = 2;
        // 每一个 Order 对象表示一个 排序的列，构造器参数: 第一个， 排序方式; 第二个: 排序的属性名
        Sort.Order order1 = new Sort.Order(Sort.Direction.DESC, "id");
        Sort.Order order2 = new Sort.Order(Sort.Direction.ASC, "email");
        // Sort 对象用来封装 总的排序信息， 每一个属性的排序信息由 Order 对象进行封装
        Sort sort = new Sort(order1, order2);
        Pageable pageable = new PageRequest(page, pageSize, sort);
        Page<Person> pagePerson = personRepository.findAll(pageable);
        System.out.println("当前页的总记录数 = " + pagePerson.getNumberOfElements());
        System.out.println("数据表中的总记录数 =  " + pagePerson.getTotalElements());
        System.out.println("数据表的总页数 = " + pagePerson.getTotalPages());
        System.out.println("获取到的总记录(数据集合形式) = " + pagePerson.getContent());
        System.out.println("当前页码数: = " + (pagePerson.getNumber() + 1));
        System.out.println("每页的数据数量 = " + pagePerson.getSize());
    }

    /**
     * JpaRepository.saveAndFlush 和 EntityManager.merge 方法的作用一样
     * 如果一个对象没有Id 就增加，
     * 如果一个对象有 id， 但是ID 不再数据表中村则，则增加
     * 如果一个对象有 id， 且 ID 在数据表中或者缓存中存在，则修改
     */
    @Test
    public void testSaveAndFlush() {
        Person person = new Person("XY", "xy@customer.com", new Date());
        person.setAddressId(123);
        person.setId(26);
        Person personAnother = personRepository.saveAndFlush(person);
        System.out.println(personAnother == person);
    }

    /**
     * 目标: 实现待查询条件的分页
     * 方案： 调用 JpaSpecificationExecutor.findAll(Specification<T> spec, Pageable pageable) 方法
     * Specification: 封装了 JPA Criteria 的查询条件
     * pageable: 封装了 分页和排序信息
     */
    @Test
    public void testJpaSpecificationExecutor() {
        int pageNo = 7 - 1;
        int pageSize = 3;
        Pageable pageable = new PageRequest(pageNo, pageSize);
        Specification<Person> spec = new Specification<Person>() {
            /**
             *
             * @param root 代表查询的实体类
             * @param query 可以从中得到 Root 对象，即告知 JPA Criteria 要查询哪个实体，还可以添加查询条件，可以结合 EntityManager
             *              获取到最终的TypeQuery 对象
             * @param cb CriteriaBuilder: 创建 Criteria 对象的工厂， 查询条件通过 CriteriaBuilder 对象进行封装
             *              可以从中获取到 Predicate 对象
             * @return Predicate: 代表一个查询条件
             */
            @Override
            public Predicate toPredicate(Root<Person> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                // 通过 root 导航到具体的属性
                Path<Integer> path = root.get("id");
                return cb.gt(path, 6);
            }
        };
        Page<Person> pagePerson = personRepository.findAll(spec, pageable);
        System.out.println("当前页的总记录数 = " + pagePerson.getNumberOfElements());
        System.out.println("数据表中的总记录数 =  " + pagePerson.getTotalElements());
        System.out.println("数据表的总页数 = " + pagePerson.getTotalPages());
        System.out.println("获取到的总记录(数据集合形式) = " + pagePerson.getContent());
        System.out.println("当前页码数: = " + (pagePerson.getNumber() + 1));
        System.out.println("每页的数据数量 = " + pagePerson.getSize());
    }

    /**
     * 自定义 Repository 接口方法的步骤:
     * 1. 自定义接口，将要自定义的 Repository 方法声明在里面
     * 2. 自定义实现类实现 自定义的接口， 实现类的名称有要求: xxxRepositoryImpl
     * 3. 在Repository 中 继承自定义的接口
     */
    @Test
    public void testCustomerRepositoryMethod() {
        personRepository.test();
    }

}
