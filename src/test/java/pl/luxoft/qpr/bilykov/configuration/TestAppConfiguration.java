package pl.luxoft.qpr.bilykov.configuration;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.sql.DataSource;

@Configuration
@EnableWebMvc
@EnableJpaRepositories(basePackages = {"pl.luxoft.qpr.bilykov.repository"})
@EnableTransactionManagement
@ComponentScan("pl.luxoft.qpr.bilykov")
@PropertySource("classpath:application.properties")
@Profile("test")
public class TestAppConfiguration extends WebMvcConfigurerAdapter {

    @Bean
    public DataSource dataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl("jdbc:h2:mem:test_mem                      ;DB_CLOSE_ON_EXIT=FALSE");
        dataSource.setDriverClassName("org.h2.Driver");
        return dataSource;
    }

}
