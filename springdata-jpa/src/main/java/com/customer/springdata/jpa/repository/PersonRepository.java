package com.customer.springdata.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.RepositoryDefinition;
import org.springframework.data.repository.query.Param;

import com.customer.springdata.jpa.entity.Person;

/**
 * 1. Repository 是一个空接口，是一个标记接口
 * 2. 若我们自定义的接口实现了 Repository 接口，则该接口会被 IOC 容器识别为一个 Repository Bean
 * 纳入到 IOC 容器中，进而可以在改接口中定义一些满足规范的方法
 * 3. 实际上，也可以通过 @RepositoryDefinition 注解来标记自定义个接口为一个 Repository Bean
 * <p>
 * Repository Bean 接口中声明方法的规范：
 * 1. 查询方法以 find | get | read 开头
 * 2. 涉及到条件查询时，条件的属性用条件关键字连接，注意: 条件属性以大写字母开头
 * 3. 在 SpringData 的 Repository 中支持级联属性的查询，默认使用级联属性直接加级联属性对应类的属性即可，但是如果在自己的Entity 中存在
 * 与级联属性拼接相同的，则优先使用自己实体类的属性，而不是级联属性，例如， 本实体中有 addressId, 而级联属性为 Address address,也有id 属性
 * 则会优先使用自己实体中属性: addressId
 * 如果要使用级联属性，则需要使用接连属性的 类名_属性名 的格式，例如: Address_Id 则会查级联属性
 */
//@RepositoryDefinition(domainClass = Person.class, idClass = Integer.class)
public interface PersonRepository extends JpaRepository<Person, Integer>, JpaSpecificationExecutor<Person>, PersonDao {

    Person getByLastName(String lastName);

    Person getByLastNameStartingWithAndIdGreaterThanEqual(String lastName, Integer id);

    List<Person> getAllByIdIn(List<Integer> ids);

    /**
     * 在 SpringData 的 Repository 中支持关联查询
     */
    List<Person> getByAddress_IdGreaterThan(Integer id);

    /**
     * 可以使用 @Query 注解来定义 指定 的 JPQL 表达式
     */
    @Query("select p from Person p where p.id = (select max(p2.id) from Person p2)")
    Person getMaxIdPerson();

    /**
     * 在 @Query 注解中可以使用 ?n 来传递参数, n 表示参数的位置
     */
    @Query("from Person p where p.lastName = ?1 and p.email = ?2")
    List<Person> getPersonsQueryParameter(String lastName, String email);

    /**
     * 在 @Query 注解中还可以使用 具名参数的形式专递参数， 可以使用 @Param 注解指定具名参数 的名称
     */
    @Query("from Person p where p.lastName = :lastName and p.email = :email")
    List<Person> getPersonQueryNamedParameter(@Param("email") String email, @Param("lastName") String lastName);

    /**
     * 在模糊查询中， @Query 注解支持在 参数两边使用"%", 比如 %?1%
     */
    @Query("from Person p where p.lastName like %?1% or p.email like %?2%")
    List<Person> getPersonQueryParameterLike(String lastName, String email);

    /**
     * 在模糊查询中， @Query 注解支持在 具名参数两边使用"%", 比如 %:lastName%
     */
    @Query("from Person p where p.lastName like %:lastName% or p.email like %:email%")
    List<Person> getPersonQueryNamedParameterLike(@Param("email") String email, @Param("lastName") String lastName);

    /**
     * 在 @Query 注解中，使用 nativeQuery = true, 表名本次查询使用的是 本地 SQL 查询
     */
    @Query(value = "select count(id) from tbl_person", nativeQuery = true)
    Long getCount();

    /**
     * 1. JPQL 支持 更新/删除操作， 不支持 插入操作
     * 2. 更新/删除操作步骤：
     * 2.1. 使用 @Query 注解来指定 更新/删除 操作的 JPQL 语句
     * 2.2. 默认情况下 只指定 @Query 注解不支持 更新/删除 操作, 如果要支持, 需要 使用 @Modifying 注解修饰
     * 2.3. 默认情况下 SpringData Repository 中一个方法一个事务，是只读事务，如果要修改的话需要新加事务，所以修改操作应该在 Service 层开启事务再操作
     */
    @Modifying
    @Query("update Person p set p.email = :email where p.lastName = :lastName")
    void updatePerson(@Param("lastName") String lastName, @Param("email") String email);
}
