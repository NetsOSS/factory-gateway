package eu.nets.payment.fitnesse.example;

import eu.nets.payment.fitnesse.NetsFitNesseSuite;
import fitnesse.junit.FitNesseSuite;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(NetsFitNesseSuite.class)
@FitNesseSuite.Name("FunctionalTests.AllTests")
@FitNesseSuite.FitnesseDir("./")
@FitNesseSuite.OutputDir("./target")
@FitNesseSuite.DebugMode(false)
//Please use a unique default port for your application, or at least add the systemProperty option. 
//Failure to do this will create random havoc on your CI when other projects also use the default :)
//value = default port, systemProperty = optional override
@FitNesseSuite.Port(value = 1337, systemProperty = "fitnesse.port") 
public class GatewayFunctionalTest {

    @Test
    public void runAllTests() throws Exception {
        //Should be empty
    }

}
