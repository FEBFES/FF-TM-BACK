package com.febfes.fftmback.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.quartz.QuartzProperties;
import org.springframework.boot.autoconfigure.quartz.SchedulerFactoryBeanCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class QuartzConfig {

    public static final String DEADLINE = "deadline";
    public static final String JOB = "job";
    public static final String TRIGGER = "trigger";

        private final QuartzProperties quartzProperties;
    private final DataSource dataSource;

//    @Bean
//    public Properties quartzProperties() throws IOException {
//        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
//        propertiesFactoryBean.setLocation(new ClassPathResource("/application.properties"));
//        Properties props = new Properties();
//        props.putAll(quartzProperties.getProperties());
//        propertiesFactoryBean.setProperties(props);
//        propertiesFactoryBean.afterPropertiesSet();
//        return propertiesFactoryBean.getObject();
//    }

    @Bean
    public SchedulerFactoryBeanCustomizer schedulerFactoryBeanCustomizer() {
        return bean -> bean.setQuartzProperties(createQuartzProperties());
    }

    private Properties createQuartzProperties() {
        Properties props = new Properties();
        props.put("org.quartz.jobStore.driverDelegateClass", "org.quartz.impl.jdbcjobstore.PostgreSQLDelegate");
//        props.putAll(quartzProperties.getProperties());
        return props;
    }

//    @Bean
//    public SchedulerFactoryBean schedulerFactoryBean() throws IOException {
//        SchedulerFactoryBean factory = new SchedulerFactoryBean();
////        factory.setOverwriteExistingJobs(true);
//        factory.setDataSource(dataSource);
////        factory.setQuartzProperties(quartzProperties());
////        factory.setConfigLocation(new ClassPathResource("quartz.properties"));
//        return factory;
//    }
//
//    @Bean
//    public Scheduler scheduler() throws IOException {
//        return schedulerFactoryBean().getScheduler();
//    }
}
