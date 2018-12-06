package com.netdevs.csvtodb;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

	// Wired database configuration bean
	@Autowired
	DataSource dataSource;

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;


	// Read CSV file content and Map to user defined model
	@Bean
	public FlatFileItemReader<User> fileReader() {

		// Create reader instance
		FlatFileItemReader<User> reader = new FlatFileItemReader<User>();

		// Set input file location
		reader.setResource(new ClassPathResource("contacts.csv"));

		// Set number of lines to skips. Use it if file has header rows.
		reader.setLinesToSkip(1);

		// Configure how each line will be parsed and mapped to different values
		reader.setLineMapper(new DefaultLineMapper<User>() {
			{

				// 2 columns in each row
				setLineTokenizer(new DelimitedLineTokenizer() {
					{
						setNames(new String[] { "name", "email" });
					}
				});

				// Set values in User class
				setFieldSetMapper(new BeanWrapperFieldSetMapper<User>() {
					{
						setTargetType(User.class);
					}
				});
			}
		});

		return reader;
	}
	
	// Define processor to process custom amount of records
	@Bean
	public UserProcessor processor() {
		return new UserProcessor();
	}

	// Write read data to DB
	@Bean
	public JdbcBatchItemWriter<User> dbWriter() {
		JdbcBatchItemWriter<User> writer = new JdbcBatchItemWriter<User>();
		writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<User>());
		writer.setSql("INSERT INTO user (name, email) VALUES (:name, :email)");
		writer.setDataSource(dataSource);

		return writer;
	}

	// Define a DB writing Job
	@Bean
	public Job writeDbJob() {

		return jobBuilderFactory.get("readCSVFilesJob").incrementer(new RunIdIncrementer()).start(step1()).build();
	}

	// Define appropriate step to execute this Job
	@Bean
	public Step step1() {

		return stepBuilderFactory.get("step1").<User, User>chunk(10).reader(fileReader()).processor(processor()).writer(dbWriter()).build();
	}

}
