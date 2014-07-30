package eu.nets.factory.gateway;

import eu.nets.factory.gateway.model.Application;
import eu.nets.factory.gateway.model.ApplicationGroup;
import eu.nets.factory.gateway.model.ApplicationInstance;
import eu.nets.factory.gateway.model.LoadBalancer;
import org.fest.assertions.GenericAssert;

import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.Formatting.format;


public class ApplicationAssert extends GenericAssert<ApplicationAssert, Application> {
    protected ApplicationAssert(Application actual) { super(ApplicationAssert.class, actual); }

    public ApplicationAssert hasName(String expected) {
        if (actual == null && expected == null) return this;
        isNotNull();
        if (! actual.getName().equals(expected)) failIfNotEquals(actual.getName(), expected);

        return this;
    }

    public ApplicationAssert doesNotHaveName(String expected) {
        if (actual == null && expected == null) return this;
        isNotNull();
        if (actual.getName().equals(expected)) failIfEquals(actual.getName(), expected);

        return this;
    }

    public ApplicationAssert hasPublicUrl(String expected) {
        if (actual == null && expected == null) return this;
        isNotNull();
        if (! actual.getPublicUrl().equals(expected)) failIfNotEquals(actual.getPublicUrl(), expected);

        return this;
    }

    public ApplicationAssert doesNotHavePublicUrl(String expected) {
        if (actual == null && expected == null) return this;
        isNotNull();
        if (actual.getPublicUrl().equals(expected)) failIfEquals(actual.getPublicUrl(), expected);

        return this;
    }

    public ApplicationAssert hasEmails(String expected) {
        if (actual == null && expected == null) return this;
        isNotNull();
        if (! actual.getEmails().equals(expected)) failIfNotEquals(actual.getEmails(), expected);

        return this;
    }

    public ApplicationAssert doesNotHaveEmails(String expected) {
        if (actual == null && expected == null) return this;
        isNotNull();
        if (actual.getEmails().equals(expected)) failIfEquals(actual.getEmails(), expected);

        return this;
    }

    public ApplicationAssert hasCheckPath(String expected) {
        if (actual == null && expected == null) return this;
        isNotNull();
        if (! actual.getCheckPath().equals(expected)) failIfNotEquals(actual.getCheckPath(), expected);

        return this;
    }

    public ApplicationAssert doesNotHaveCheckPath(String expected) {
        if (actual == null && expected == null) return this;
        isNotNull();
        if (actual.getCheckPath().equals(expected)) failIfEquals(actual.getEmails(), expected);

        return this;
    }

    private ApplicationAssert hasApplicationInstance(ApplicationInstance expected) {
        isNotNull();
        validateIsNotNull(actual.getApplicationInstances());
        validateIsNotNull(expected);

        for(ApplicationInstance applicationInstance : actual.getApplicationInstances()) {
            if(applicationInstance == expected) {
                return this;
            }
        }

        failIfNotContains(actual.getApplicationInstances(), expected);
        return null;
    }

    private ApplicationAssert hasApplicationInstance(String expected) {
        isNotNull();
        validateIsNotNull(actual.getApplicationInstances());
        validateIsNotNull(expected);

        for(ApplicationInstance applicationInstance : actual.getApplicationInstances()) {
            if(applicationInstance.getName().equals(expected)) {
                return this;
            }
        }

        failIfNotContains(actual.getApplicationInstances(), expected);
        return null;
    }

    private ApplicationAssert excludesApplicationInstance(ApplicationInstance x) {
        isNotNull();
        validateIsNotNull(actual.getApplicationInstances());
        validateIsNotNull(x);

        for(ApplicationInstance applicationInstance : actual.getApplicationInstances()) {
            if(applicationInstance == x) {
                failIfContains(actual.getApplicationInstances(), x);
            }
        }

        return this;
    }

    private ApplicationAssert excludesApplicationInstance(String x) {
        isNotNull();
        validateIsNotNull(actual.getApplicationInstances());
        validateIsNotNull(x);

        for(ApplicationInstance applicationInstance : actual.getApplicationInstances()) {
            if(applicationInstance.getName().equals(x)) {
                failIfContains(actual.getApplicationInstances(), x);
            }
        }

        return this;
    }

    /* Ignores sequence and duplicates */
    public ApplicationAssert hasApplicationInstances(ApplicationInstance... sequence) {
        isNotNull();
        validateIsNotNull(actual.getApplicationInstances());
        validateIsNotNull(sequence);

        replaceDefaultErrorMessagesWith(format("sequence:<%s>  is not equal to the sequence:<%s>", actual.getApplicationInstances(), sequence));

        for(ApplicationInstance applicationInstance : sequence) {
            hasApplicationInstance(applicationInstance);
        }

        return this;
    }

    public ApplicationAssert hasApplicationInstances(String... sequence) {
        isNotNull();
        validateIsNotNull(actual.getApplicationInstances());
        validateIsNotNull(sequence);

        replaceDefaultErrorMessagesWith(format("sequence:<%s>  is not equal to the sequence:<%s>", actual.getApplicationInstances(), sequence));

        for(String name : sequence) {
            hasApplicationInstance(name);
        }

        return this;
    }

    /* Ignores sequence */
    public ApplicationAssert hasExactApplicationInstances(ApplicationInstance... sequence) {
        isNotNull();
        validateIsNotNull(actual.getApplicationInstances());
        validateIsNotNull(sequence);
        if(sequence.length != actual.getApplicationInstances().size()) failIfNotEquals(actual.getApplicationInstances(), list(sequence));

        for(ApplicationInstance applicationInstance : sequence) {
            hasApplicationInstance(applicationInstance);
        }

        return this;
    }

    public ApplicationAssert hasExactApplicationInstances(String... sequence) {
        isNotNull();
        validateIsNotNull(actual.getApplicationInstances());
        validateIsNotNull(sequence);
        if(sequence.length != actual.getApplicationInstances().size()) failIfNotEquals(actual.getApplicationInstances(), list(sequence));

        for(String name : sequence) {
            hasApplicationInstance(name);
        }

        return this;
    }

    public ApplicationAssert excludesApplicationInstances(ApplicationInstance... sequence) {
        isNotNull();
        validateIsNotNull(actual.getApplicationInstances());
        validateIsNotNull(sequence);

        for(ApplicationInstance applicationInstance : sequence) {
            excludesApplicationInstance(applicationInstance);
        }

        return this;
    }

    public ApplicationAssert excludesApplicationInstances(String... sequence) {
        isNotNull();
        validateIsNotNull(actual.getApplicationInstances());
        validateIsNotNull(sequence);

        for(String name : sequence) {
            excludesApplicationInstance(name);
        }

        return this;
    }


    public ApplicationAssert hasApplicationGroup(ApplicationGroup expected) {
        if (actual == null && expected == null) return this;
        isNotNull();
        if (actual.getApplicationGroup().getId().equals(expected.getId())) return this;

        failIfNotEquals(actual.getId(), expected.getId());
        return null;
    }

    public ApplicationAssert doesNotHaveApplicationGroup(ApplicationGroup expected) {
        if (actual == null && expected == null) return this;
        isNotNull();
        if (actual.getApplicationGroup().getId().equals(expected.getId())) return this;

        failIfNotEquals(actual.getApplicationGroup().getId(), expected.getId());
        return null;
    }


    private ApplicationAssert hasLoadBalancer(LoadBalancer expected) {
        isNotNull();
        validateIsNotNull(actual.getLoadBalancers());
        validateIsNotNull(expected);

        for(LoadBalancer loadBalancer : actual.getLoadBalancers()) {
            if(loadBalancer == expected) {
                return this;
            }
        }

        failIfNotContains(actual.getLoadBalancers(), expected);
        return null;
    }

    private ApplicationAssert hasLoadBalancer(String expected) {
        isNotNull();
        validateIsNotNull(actual.getLoadBalancers());
        validateIsNotNull(expected);

        for(LoadBalancer loadBalancer : actual.getLoadBalancers()) {
            if(loadBalancer.getName().equals(expected)) {
                return this;
            }
        }

        failIfNotContains(actual.getLoadBalancers(), expected);
        return null;
    }

    private ApplicationAssert excludesLoadBalancer(LoadBalancer x) {
        isNotNull();
        validateIsNotNull(actual.getLoadBalancers());
        validateIsNotNull(x);

        for(LoadBalancer loadBalancer : actual.getLoadBalancers()) {
            if(loadBalancer == x) {
                failIfContains(actual.getLoadBalancers(), x);
            }
        }

        return this;
    }

    private ApplicationAssert excludesLoadBalancer(String x) {
        isNotNull();
        validateIsNotNull(actual.getLoadBalancers());
        validateIsNotNull(x);

        for(LoadBalancer loadBalancer : actual.getLoadBalancers()) {
            if(loadBalancer.getName().equals(x)) {
                failIfContains(actual.getLoadBalancers(), x);
            }
        }

        return this;
    }

    /* Ignores sequence and duplicates */
    public ApplicationAssert hasLoadBalancers(LoadBalancer... sequence) {
        isNotNull();
        validateIsNotNull(actual.getLoadBalancers());
        validateIsNotNull(sequence);

        replaceDefaultErrorMessagesWith(format("sequence:<%s>  is not equal to the sequence:<%s>", actual, sequence));

        for(LoadBalancer loadBalancer : sequence) {
            hasLoadBalancer(loadBalancer);
        }

        return this;
    }

    public ApplicationAssert hasLoadBalancers(String... sequence) {
        isNotNull();
        validateIsNotNull(actual.getLoadBalancers());
        validateIsNotNull(sequence);

        replaceDefaultErrorMessagesWith(format("sequence:<%s>  is not equal to the sequence:<%s>", actual.getName(), sequence));

        for(String name : sequence) {
            hasLoadBalancer(name);
        }

        return this;
    }

    /* Ignores sequence */
    public ApplicationAssert hasExactLoadBalancers(LoadBalancer... sequence) {
        isNotNull();
        validateIsNotNull(actual.getLoadBalancers());
        validateIsNotNull(sequence);
        if(sequence.length != actual.getLoadBalancers().size()) failIfNotEquals(actual.getLoadBalancers(), sequence);

        for(LoadBalancer loadBalancer : sequence) {
            hasLoadBalancer(loadBalancer);
        }

        return this;
    }

    public ApplicationAssert hasExactLoadBalancers(String... sequence) {
        isNotNull();
        validateIsNotNull(actual.getLoadBalancers());
        validateIsNotNull(sequence);
        if(sequence.length != actual.getLoadBalancers().size()) failIfNotEquals(actual.getLoadBalancers(), list(sequence));

        for(String name : sequence) {
            hasLoadBalancer(name);
        }

        return this;
    }

    public ApplicationAssert excludesLoadBalancers(LoadBalancer... sequence) {
        isNotNull();
        validateIsNotNull(actual.getLoadBalancers());
        validateIsNotNull(sequence);

        for(LoadBalancer loadBalancer : sequence) {
            excludesLoadBalancer(loadBalancer);
        }

        return this;
    }

    public ApplicationAssert excludesLoadBalancers(String... sequence) {
        isNotNull();
        validateIsNotNull(actual.getLoadBalancers());
        validateIsNotNull(sequence);

        for(String name : sequence) {
            excludesLoadBalancer(name);
        }

        return this;
    }


    protected final void validateIsNotNull(List<?> objects) {
        if (objects == null)
            fail(formattedErrorMessage("The given List should not be null"));
    }

    protected final void validateIsNotNull(Object object) {
        if (object == null)
            fail(formattedErrorMessage("The given object should not be null"));
    }


    private void failIfNotContains(Object actual_, Object notContain) {
        failIfCustomMessageIsSet();
        fail(format("object:<%s> does not contain the object:<%s>", actual_, notContain));
    }

    private void failIfContains(Object actual_, Object contain) {
        failIfCustomMessageIsSet();
        fail(format("object:<%s> contains the object:<%s>", actual_, contain));
    }


    private void failIfNotEquals(Object actual_, Object notEqual) {
        failIfCustomMessageIsSet();
        fail(format("object:<%s>  is not equal to the object:<%s>", actual_, notEqual));
    }

    private void failIfEquals(Object actual_, Object equal) {
        failIfCustomMessageIsSet();
        fail(format("object:<%s> is equal to the object:<%s>", actual_, equal));
    }

    public static <T> List<T> list(T... elements) {
        if (elements == null) return null;
        List<T> list = new ArrayList<T>();
        for (T e : elements) list.add(e);
        return list;
    }
}
