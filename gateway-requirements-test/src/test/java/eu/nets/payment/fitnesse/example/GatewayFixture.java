package eu.nets.payment.fitnesse.example;

import eu.nets.payment.fitnesse.fixture.SpringAnnotationTransactionalDoFixture;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import javax.sql.DataSource;

@Component
@ContextConfiguration(classes = FunctionalTestConfiguration.class)
public class GatewayFixture extends SpringAnnotationTransactionalDoFixture {

    @Resource
    private DataSource dataSource;

    @Resource
    private TransactionTemplate tx;

    public boolean testConfig() {
        return dataSource != null && tx != null;
    }

    @Override
    protected void clearTransientState() {
    }
}
