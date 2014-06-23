package eu.nets.factory.gateway.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;


import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={ModelConfig.class})
@TransactionConfiguration(defaultRollback = true)
public class HibernateApplicationGroupRepositoryTest {



    @Autowired
    protected ApplicationGroupRepository applicationGroupRepository;

    @Test
    @Transactional()
    public void testFindByNameLike() throws Exception {
        assertNotNull(applicationGroupRepository);
        applicationGroupRepository.save(new ApplicationGroup("test"));
        assertEquals("Should be one", 1, applicationGroupRepository.findByNameLike("test").size());
        assertEquals("Should get none", 0, applicationGroupRepository.findByNameLike("blabla").size());
    }
}