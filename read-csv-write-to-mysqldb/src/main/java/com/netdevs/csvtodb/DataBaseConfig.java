package com.netdevs.csvtodb;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;


@Configuration
public class DataBaseConfig {

	// Set up MySQL database connection
	// Set appropriate values for these key items
	// your_database_name, user_name, password
	@Bean
	public DataSource dataSource() {
		final DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource.setUrl("jdbc:mysql://localhost:3306/your_database_name");
		dataSource.setUsername("user_name");
		dataSource.setPassword("password");

		return dataSource;
	}
}
