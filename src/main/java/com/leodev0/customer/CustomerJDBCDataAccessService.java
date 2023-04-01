package com.leodev0.customer;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.List;
import java.util.Optional;

@Repository("jdbc")
public class CustomerJDBCDataAccessService implements CustomerDao {
    private final JdbcTemplate jdbcTemplate;
    private final CustomerRowMapper customerRowMapper;

    public CustomerJDBCDataAccessService(JdbcTemplate jdbcTemplate, CustomerRowMapper customerRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.customerRowMapper = customerRowMapper;
    }

    @Override
    public List<Customer> selectAllCustomers() {
        String sql = """
                SELECT id, name, email, age
                FROM customer
                """;

        List<Customer> customers = jdbcTemplate.query(sql, customerRowMapper);

        return customers;
    }

    @Override
    public Optional<Customer> selectCustomerById(Integer id) {
        String sql = """
                SELECT id, name, email, age
                FROM customer
                WHERE id = ?
                LIMIT 1
                """;

        return jdbcTemplate.query(sql, customerRowMapper, id)
                .stream()
                .findFirst();

//        try {
//            Customer customer = jdbcTemplate.queryForObject(sql, customerRowMapper, id);
//            return Optional.ofNullable(customer);
//        } catch (EmptyResultDataAccessException e) {
//            System.out.println("EmptyResultDataAccessException error: " + e);
//            return Optional.empty();
//        }
    }

    @Override
    public void insertCustomer(Customer customer) {
        String sql = """
                INSERT INTO customer (name, email, age)
                VALUES (?, ?, ?)
                """;

        int update = jdbcTemplate.update(
                sql,
                customer.getName(),
                customer.getEmail(),
                customer.getAge()
        );

        System.out.println("[%s] rows affected: %s".formatted(sql, update));
    }

    @Override
    public boolean existsCustomerWithEmail(String email) {
        String sql = """
                SELECT COUNT(*)
                FROM customer
                WHERE email = ?
                """;

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }

    @Override
    public void deleteCustomerById(Integer id) {
        String sql = """
                DELETE
                FROM customer
                WHERE id = ?
                """;

        int delete = jdbcTemplate.update(sql, id);
        System.out.println("[%s] rows affected: %s".formatted(sql, delete));
    }

    @Override
    public boolean existsCustomerWithId(Integer id) {
        String sql = """
                SELECT COUNT(*)
                FROM customer
                WHERE id = ?
                """;

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    @Override
    public void updateCustomer(Customer update) {
        if (update.getName() != null) {
            String sqlName = """
                    UPDATE customer
                    SET name = ?
                    WHERE id = ?
                    """;
            int rowsAffected = jdbcTemplate.update(sqlName, update.getName(), update.getId());
            System.out.println("[%s] rows affected: %s".formatted(sqlName, rowsAffected));
        }

        if (update.getEmail() != null) {
            String sqlEmail = """
                    UPDATE customer
                    SET email = ?
                    WHERE id = ?
                    """;
            int rowsAffected = jdbcTemplate.update(sqlEmail, update.getEmail(), update.getId());
            System.out.println("[%s] rows affected: %s".formatted(sqlEmail, rowsAffected));
        }

        if (update.getAge() != null) {
            String sqlAge = """
                    UPDATE customer
                    SET age = ?
                    WHERE id = ?
                    """;
            int rowsAffected = jdbcTemplate.update(sqlAge, update.getAge(), update.getId());
            System.out.println("[%s] rows affected: %s".formatted(sqlAge, rowsAffected));
        }
    }
}
