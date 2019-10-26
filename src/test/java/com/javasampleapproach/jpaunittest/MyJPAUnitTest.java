package com.javasampleapproach.jpaunittest;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

import com.javasampleapproach.jpaunittest.entity.Customer;
import com.javasampleapproach.jpaunittest.repo.CustomerRepository;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
@DataJpaTest
@ContextConfiguration(classes = TestPersistenceConfig.class)
public class MyJPAUnitTest {

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	CustomerRepository repository;

	@ClassRule
	public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

	@Rule
	public final SpringMethodRule springMethodRule = new SpringMethodRule();

	@Test
	public void should_find_no_customers_if_repository_is_empty() {
		Iterable<Customer> customers = repository.findAll();

		assertThat(customers).isEmpty();
	}

	@Test
	public void should_store_a_customer() {
		Customer customer = repository.save(new Customer("Jack", "Smith"));

		assertThat(customer).hasFieldOrPropertyWithValue("firstName", "Jack");
		assertThat(customer).hasFieldOrPropertyWithValue("lastName", "Smith");
	}

	@Test
	public void should_delete_all_customer() {
		entityManager.persist(new Customer("Jack", "Smith"));
		entityManager.persist(new Customer("Adam", "Johnson"));

		repository.deleteAll();

		assertThat(repository.findAll()).isEmpty();
	}

	@Test
	public void should_find_all_customers() {
		Customer customer1 = new Customer("Jack", "Smith");
		entityManager.persist(customer1);

		Customer customer2 = new Customer("Adam", "Johnson");
		entityManager.persist(customer2);

		Customer customer3 = new Customer("Peter", "Smith");
		entityManager.persist(customer3);

		Iterable<Customer> customers = repository.findAll();

		assertThat(customers).hasSize(3).contains(customer1, customer2, customer3);
	}

	// @See https://www.baeldung.com/junit-params
	// @See https://github.com/Pragmatists/JUnitParams
	@Test
	@Parameters({
			"Jack, Smith",
			"Adam, Johnson",
			"Peter, Smith",
	})
	public void should_find_all_customers2(String firstname, String lastname) {
		Customer customer = new Customer(firstname, lastname);
		entityManager.persist(customer);

		Customer foundCustomer = repository.findOne(customer.getId());

		assertThat(foundCustomer).isEqualTo(customer);
	}

	@Test
	public void should_find_customer_by_id() {
		Customer customer1 = new Customer("Jack", "Smith");
		entityManager.persist(customer1);

		Customer customer2 = new Customer("Adam", "Johnson");
		entityManager.persist(customer2);

		Customer foundCustomer = repository.findOne(customer2.getId());

		assertThat(foundCustomer).isEqualTo(customer2);
	}

}
