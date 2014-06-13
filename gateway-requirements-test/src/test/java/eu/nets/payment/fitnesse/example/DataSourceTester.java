package eu.nets.payment.fitnesse.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

@Component
public class DataSourceTester {

    private final Logger logger = LoggerFactory.getLogger(DataSourceTester.class);

    @Autowired
    public DataSourceTester(DataSourceTester dataSource, TransactionTemplate transactionTemplate) {

        logger.info("DATASOURCE: [{}]", dataSource);
        logger.info("        TX: [{}]", transactionTemplate);

    }

}
