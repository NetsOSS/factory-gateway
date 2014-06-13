package eu.nets.factory.gateway.model;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import com.google.common.collect.ImmutableMap;
import com.jolbox.bonecp.BoneCPDataSource;
import org.apache.commons.lang.StringUtils;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.flywaydb.core.api.MigrationInfoService;
import org.hibernate.SessionFactory;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.cfg.ImprovedNamingStrategy;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.dialect.Oracle10gDialect;
import org.hibernate.dialect.PostgreSQL9Dialect;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.hibernate.stat.Statistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.jmx.export.MBeanExporter;
import org.springframework.orm.hibernate4.SpringSessionContext;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import static java.lang.String.format;
import static org.apache.commons.lang.StringUtils.trimToEmpty;
import static org.hibernate.cfg.AvailableSettings.CURRENT_SESSION_CONTEXT_CLASS;
import static org.hibernate.cfg.AvailableSettings.DEFAULT_CACHE_CONCURRENCY_STRATEGY;
import static org.hibernate.cfg.AvailableSettings.DIALECT;
import static org.hibernate.cfg.AvailableSettings.FORMAT_SQL;
import static org.hibernate.cfg.AvailableSettings.GENERATE_STATISTICS;
import static org.hibernate.cfg.AvailableSettings.HBM2DDL_AUTO;
import static org.hibernate.cfg.AvailableSettings.SHOW_SQL;
import static org.hibernate.cfg.AvailableSettings.USE_NEW_ID_GENERATOR_MAPPINGS;
import static org.hibernate.cfg.AvailableSettings.USE_SECOND_LEVEL_CACHE;
import static org.hibernate.cfg.AvailableSettings.USE_SQL_COMMENTS;
import static org.hibernate.jpa.AvailableSettings.NAMING_STRATEGY;

@ComponentScan(basePackageClasses = ModelConfig.class)
@EnableJpaRepositories(basePackageClasses = ModelConfig.class)
public class ModelConfig {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Bean
    public PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer(Environment env) throws Exception {
        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
        configurer.setProperties(System.getProperties());
        configurer.setEnvironment(env);
        configurer.setLocalOverride(true);
        return configurer;
    }

    @Bean
    public DataSource dataSource(BoneCPDataSource innerDataSource) {
        return new TransactionAwareDataSourceProxy(new LazyConnectionDataSourceProxy(innerDataSource));
    }


    @Bean(destroyMethod = "close")
    BoneCPDataSource innerDataSource(MyAppSettings settings,
                                     @Value("${database.testOnStart:true}") boolean testOnStart,
                                     @Value("${bonecp.partitionCount:1}") int partitionCount,
                                     @Value("${bonecp.acquireIncrement:1}") int acquireIncrement,
                                     @Value("${bonecp.minConnectionsPerPartition:1}") int minConnectionsPerPartition,
                                     @Value("${bonecp.maxConnectionsPerPartition:40}") int maxConnectionsPerPartition) {
        BoneCPDataSource ds = new BoneCPDataSource();
        ds.setJdbcUrl(settings.getDatabaseUrl());
        ds.setUsername(settings.getDatabaseUsername());
        ds.setPassword(settings.getDatabasePassword());

        ds.setIdleConnectionTestPeriodInSeconds(60);
        ds.setIdleMaxAgeInSeconds(240);
        ds.setMaxConnectionsPerPartition(maxConnectionsPerPartition);
        ds.setMinConnectionsPerPartition(minConnectionsPerPartition);
        ds.setPartitionCount(partitionCount);
        ds.setAcquireIncrement(acquireIncrement);
        ds.setStatementsCacheSize(1000);
        ds.setStatisticsEnabled(true);

//        if (testOnStart) {
//            testConnection(ds, jdbcUrl);
//        }

        if (settings.migrateDatabase()) {
            log.info("Running migrations");
            Flyway flyway = new Flyway();
            flyway.setDataSource(ds);
            flyway.setOutOfOrder(true);
            MigrationInfoService info = flyway.info();
            log.info(format("%-15s %-10s %-19s %s", "Version", "State", "Installed on", "Description"));
            for (MigrationInfo mi : info.all()) {
                Date installedOn = mi.getInstalledOn();
                log.info(format("%-15s %-10s %-19s %s",
                        mi.getVersion(),
                        trimToEmpty(mi.getState().getDisplayName()),
                        installedOn != null ? installedOn.getTime() : "",
                        StringUtils.substring(trimToEmpty(mi.getDescription()), 0, 20)));
            }
            flyway.migrate();
        } else {
            log.info("Skipping migrations");
        }

        return ds;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource,
                                                                       @Value("${hibernate.hbm2ddl.auto:validate}") String hbm2ddl,
                                                                       @Value("${hibernate.showSql:false}") boolean showSql,
                                                                       @Value("${hibernate.dialect:}") String dialect) throws ClassNotFoundException {
        LocalContainerEntityManagerFactoryBean x = new LocalContainerEntityManagerFactoryBean();
        x.setDataSource(dataSource);
        if (dialect.equals("")) {
            dialect = guessDialect(dataSource);
        }
        x.setJpaPropertyMap(createJpaMap(hbm2ddl, showSql, dialect));
        x.setPackagesToScan(AbstractEntity.class.getPackage().getName());
        x.setPersistenceProvider(new HibernatePersistenceProvider());

        return x;
    }

    private String guessDialect(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            String databaseProductName = connection.getMetaData().getDatabaseProductName().toLowerCase();
            log.info("using database " + databaseProductName);
            if (databaseProductName.contains("h2")) {
                return H2Dialect.class.getName();
            } else if (databaseProductName.contains("postgres")) {
                return PostgreSQL9Dialect.class.getName();
            } else if (databaseProductName.contains("oracle")) {
                return Oracle10gDialect.class.getName();
            }
        } catch (SQLException ignore) {
        }

        return "";
    }

    public static Map<String, Object> createJpaMap(String hbm2ddl, boolean showSql, String dialect) {
        Map<String, Object> map = new HashMap<>();
        map.put(HBM2DDL_AUTO, hbm2ddl);
        map.put(FORMAT_SQL, showSql);
        map.put(SHOW_SQL, showSql);
        map.put(USE_SQL_COMMENTS, showSql);
        map.put(GENERATE_STATISTICS, true);
        map.put(NAMING_STRATEGY, ImprovedNamingStrategy.class.getName());
        map.put(USE_NEW_ID_GENERATOR_MAPPINGS, "true");
        map.put(DEFAULT_CACHE_CONCURRENCY_STRATEGY, CacheConcurrencyStrategy.READ_WRITE.name());
        map.put(CURRENT_SESSION_CONTEXT_CLASS, SpringSessionContext.class.getName());

        map.put(USE_SECOND_LEVEL_CACHE, false);

//        map.put(CACHE_REGION_FACTORY, org.hibernate.cache.ehcache.EhCacheRegionFactory.class.getName());
//        map.put(USE_SECOND_LEVEL_CACHE, true);
//        map.put(USE_QUERY_CACHE, true);
//        map.put(SHARED_CACHE_MODE, SharedCacheMode.ENABLE_SELECTIVE.name());
//        map.put(SHARED_CACHE_MODE, SharedCacheMode.NONE.name());

        if (!dialect.equals("")) {
            map.put(DIALECT, dialect);
        }

        return map;
    }


    @Bean
    public SessionFactory sessionFactory(LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        return ((org.hibernate.jpa.HibernateEntityManagerFactory) entityManagerFactory.nativeEntityManagerFactory).getSessionFactory();
    }

    @Bean
    public JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean
    public TransactionTemplate transactionTemplate(PlatformTransactionManager platformTransactionManager) {
        return new TransactionTemplate(platformTransactionManager);
    }

    @Bean
    public MBeanExporter jmxService(Statistics statistics) {
        MBeanExporter exporter = new MBeanExporter();
        exporter.setBeans(ImmutableMap.of("Hibernate:name=statistics", (Object) statistics));
        return exporter;
    }

    @Bean
    public Statistics statisticsService(SessionFactory sessionFactory) {
        return sessionFactory.getStatistics();
    }
}
