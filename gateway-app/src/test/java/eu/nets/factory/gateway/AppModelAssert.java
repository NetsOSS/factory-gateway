package eu.nets.factory.gateway;

import eu.nets.factory.gateway.web.AppInstModel;
import eu.nets.factory.gateway.web.AppModel;
import eu.nets.factory.gateway.web.LoadBalancerModel;
import org.fest.assertions.GenericAssert;

import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.Formatting.format;


public class AppModelAssert extends GenericAssert<AppModelAssert, AppModel> {


    protected AppModelAssert(AppModel actual) { super(AppModelAssert.class, actual); }


    /*
     *
     */
    public AppModelAssert hasId(Long expected) {
        if (actual == null && expected == null) return this;
        isNotNull();
        if (! actual.getId().equals(expected)) failIfNotEquals(actual.getId(), expected);

        return this;
    }

    /*
     *
     */
    public AppModelAssert doesNotHaveId(Long expected) {
        if (actual == null && expected == null) return this;
        isNotNull();
        if (actual.getId().equals(expected)) failIfEquals(actual.getId(), expected);

        return this;
    }

    /*
     *
     */
    public AppModelAssert hasName(String expected) {
        if (actual == null && expected == null) return this;
        isNotNull();
        if (! actual.getName().equals(expected)) failIfNotEquals(actual.getName(), expected);

        return this;
    }

    /*
     *
     */
    public AppModelAssert doesNotHaveName(String expected) {
        if (actual == null && expected == null) return this;
        isNotNull();
        if (actual.getName().equals(expected)) failIfEquals(actual.getName(), expected);

        return this;
    }

    /*
     *
     */
    public AppModelAssert hasPublicUrl(String expected) {
        if (actual == null && expected == null) return this;
        isNotNull();
        if (! actual.getPublicUrl().equals(expected)) failIfNotEquals(actual.getPublicUrl(), expected);

        return this;
    }

    /*
     *
     */
    public AppModelAssert doesNotHavePublicUrl(String expected) {
        if (actual == null && expected == null) return this;
        isNotNull();
        if (actual.getPublicUrl().equals(expected)) failIfEquals(actual.getPublicUrl(), expected);

        return this;
    }

    /*
     *
     */
    public AppModelAssert hasEmails(String expected) {
        if (actual == null && expected == null) return this;
        isNotNull();
        if (! actual.getEmails().equals(expected)) failIfNotEquals(actual.getEmails(), expected);

        return this;
    }

    /*
     *
     */
    public AppModelAssert doesNotHaveEmails(String expected) {
        if (actual == null && expected == null) return this;
        isNotNull();
        if (actual.getEmails().equals(expected)) failIfEquals(actual.getEmails(), expected);

        return this;
    }

    /*
     *
     */
    public AppModelAssert hasCheckPath(String expected) {
        if (actual == null && expected == null) return this;
        isNotNull();
        if (! actual.getCheckPath().equals(expected)) failIfNotEquals(actual.getEmails(), expected);

        return this;
    }

    /*
     *
     */
    public AppModelAssert doesNotHaveCheckPath(String expected) {
        if (actual == null && expected == null) return this;
        isNotNull();
        if (actual.getCheckPath().equals(expected)) failIfEquals(actual.getEmails(), expected);

        return this;
    }


    /*
     *
     */
    private AppModelAssert hasAppInst(AppInstModel expected) {
        isNotNull();
        validateIsNotNull(actual.getApplicationInstances());
        validateIsNotNull(expected);

        for(AppInstModel appInstModel : actual.getApplicationInstances()) {
            if(appInstModel == expected) {
                return this;
            }
        }

        failIfNotContains(actual.getApplicationInstances(), expected);
        return null;
    }

    /*
     *
     */
    private AppModelAssert hasAppInst(String expected) {
        isNotNull();
        validateIsNotNull(actual.getApplicationInstances());
        validateIsNotNull(expected);

        for(AppInstModel appInstModel : actual.getApplicationInstances()) {
            if(appInstModel.getName() == expected) {
                return this;
            }
        }

        failIfNotContains(actual.getApplicationInstances(), expected);
        return null;
    }

    /*
     *
     */
    private AppModelAssert excludesAppInst(AppInstModel x) {
        isNotNull();
        validateIsNotNull(actual.getApplicationInstances());
        validateIsNotNull(x);

        for(AppInstModel appInstModel : actual.getApplicationInstances()) {
            if(appInstModel == x) {
                failIfContains(actual.getApplicationInstances(), x);
            }
        }

        return this;
    }

    /*
     *
     */
    private AppModelAssert excludesAppInst(String x) {
        isNotNull();
        validateIsNotNull(actual.getApplicationInstances());
        validateIsNotNull(x);

        for(AppInstModel appInstModel : actual.getApplicationInstances()) {
            if(appInstModel.getName() == x) {
                failIfContains(actual.getApplicationInstances(), x);
            }
        }

        return this;
    }

    /*
     * Ignores sequence and duplicates
     */
    public AppModelAssert hasAppInsts(AppInstModel... sequence) {
        isNotNull();
        validateIsNotNull(actual.getApplicationInstances());
        validateIsNotNull(sequence);

        replaceDefaultErrorMessagesWith(format("sequence:<%s>  is not equal to the sequence:<%s>", actual, sequence));

        for(AppInstModel appInstModel : sequence) {
            hasAppInst(appInstModel);
        }

        return this;
    }

    /*
     *
     */
    public AppModelAssert hasAppInsts(String... sequence) {
        isNotNull();
        validateIsNotNull(actual.getApplicationInstances());
        validateIsNotNull(sequence);

        replaceDefaultErrorMessagesWith(format("sequence:<%s>  is not equal to the sequence:<%s>", actual.getName(), sequence));

        for(String name : sequence) {
            hasAppInst(name);
        }

        return this;
    }

    /*
     * Ignores sequence
     */
    public AppModelAssert hasExactAppInsts(AppInstModel... sequence) {
        isNotNull();
        validateIsNotNull(actual.getApplicationInstances());
        validateIsNotNull(sequence);
        if(sequence.length != actual.getApplicationInstances().size()) failIfNotEquals(actual.getApplicationInstances(), sequence);

        for(AppInstModel appInstModel : sequence) {
            hasAppInst(appInstModel);
        }

        return this;
    }

    /*
     *
     */
    public AppModelAssert hasExactAppInsts(String... sequence) {
        isNotNull();
        validateIsNotNull(actual.getApplicationInstances());
        validateIsNotNull(sequence);
        if(sequence.length != actual.getApplicationInstances().size()) failIfNotEquals(actual.getApplicationInstances(), list(sequence));

        for(String name : sequence) {
            hasAppInst(name);
        }

        return this;
    }

    /*
     *
     */
    public AppModelAssert excludesAppInsts(AppInstModel... sequence) {
        isNotNull();
        validateIsNotNull(actual.getApplicationInstances());
        validateIsNotNull(sequence);

        for(AppInstModel appInstModel : sequence) {
            excludesAppInst(appInstModel);
        }

        return this;
    }

    /*
     *
     */
    public AppModelAssert excludesAppInsts(String... sequence) {
        isNotNull();
        validateIsNotNull(actual.getApplicationInstances());
        validateIsNotNull(sequence);

        for(String name : sequence) {
            excludesAppInst(name);
        }

        return this;
    }


    /*
     *
     */
    public AppModelAssert hasAppGroup(Long expected) {
        if (actual == null && expected == null) return this;
        isNotNull();
        if (actual.getApplicationGroupId() != expected) failIfNotEquals(actual.getId(), expected);

        return this;
    }
    /*
 *
 */
    public AppModelAssert doesNotHaveAppGroupId(Long expected) {
        if (actual == null && expected == null) return this;
        isNotNull();
        if (actual.getApplicationGroupId() == expected) failIfNotEquals(actual.getApplicationGroupId(), expected);

        return this;
    }


    /*
     *
     */
    private AppModelAssert hasLoadBalancer(LoadBalancerModel expected) {
        isNotNull();
        validateIsNotNull(actual.getLoadBalancers());
        validateIsNotNull(expected);

        for(LoadBalancerModel loadBalancerModel : actual.getLoadBalancers()) {
            if(loadBalancerModel == expected) {
                return this;
            }
        }

        failIfNotContains(actual.getLoadBalancers(), expected);
        return null;
    }

    /*
     *
     */
    private AppModelAssert hasLoadBalancer(String expected) {
        isNotNull();
        validateIsNotNull(actual.getLoadBalancers());
        validateIsNotNull(expected);

        for(LoadBalancerModel loadBalancerModel : actual.getLoadBalancers()) {
            if(loadBalancerModel.getName() == expected) {
                return this;
            }
        }

        failIfNotContains(actual.getLoadBalancers(), expected);
        return null;
    }

    /*
     *
     */
    private AppModelAssert excludesLoadBalancer(LoadBalancerModel x) {
        isNotNull();
        validateIsNotNull(actual.getLoadBalancers());
        validateIsNotNull(x);

        for(LoadBalancerModel loadBalancerModel : actual.getLoadBalancers()) {
            if(loadBalancerModel == x) {
                failIfContains(actual.getLoadBalancers(), x);
            }
        }

        return this;
    }

    /*
     *
     */
    private AppModelAssert excludesLoadBalancer(String x) {
        isNotNull();
        validateIsNotNull(actual.getLoadBalancers());
        validateIsNotNull(x);

        for(LoadBalancerModel loadBalancerModel : actual.getLoadBalancers()) {
            if(loadBalancerModel.getName() == x) {
                failIfContains(actual.getLoadBalancers(), x);
            }
        }

        return this;
    }

    /*
     * Ignores sequence and duplicates
     */
    public AppModelAssert hasLoadBalancers(LoadBalancerModel... sequence) {
        isNotNull();
        validateIsNotNull(actual.getLoadBalancers());
        validateIsNotNull(sequence);

        replaceDefaultErrorMessagesWith(format("sequence:<%s>  is not equal to the sequence:<%s>", actual, sequence));

        for(LoadBalancerModel loadBalancerModel : sequence) {
            hasLoadBalancer(loadBalancerModel);
        }

        return this;
    }

    /*
     *
     */
    public AppModelAssert hasLoadBalancers(String... sequence) {
        isNotNull();
        validateIsNotNull(actual.getLoadBalancers());
        validateIsNotNull(sequence);

        replaceDefaultErrorMessagesWith(format("sequence:<%s>  is not equal to the sequence:<%s>", actual.getName(), sequence));

        for(String name : sequence) {
            hasLoadBalancer(name);
        }

        return this;
    }

    /*
     * Ignores sequence
     */
    public AppModelAssert hasExactLoadBalancers(LoadBalancerModel... sequence) {
        isNotNull();
        validateIsNotNull(actual.getLoadBalancers());
        validateIsNotNull(sequence);
        if(sequence.length != actual.getLoadBalancers().size()) failIfNotEquals(actual.getLoadBalancers(), sequence);

        for(LoadBalancerModel loadBalancerModel : sequence) {
            hasLoadBalancer(loadBalancerModel);
        }

        return this;
    }

    /*
     *
     */
    public AppModelAssert hasExactLoadBalancers(String... sequence) {
        isNotNull();
        validateIsNotNull(actual.getLoadBalancers());
        validateIsNotNull(sequence);
        if(sequence.length != actual.getLoadBalancers().size()) failIfNotEquals(actual.getLoadBalancers(), list(sequence));

        for(String name : sequence) {
            hasLoadBalancer(name);
        }

        return this;
    }

    /*
 * Ignores sequence and duplicates
 */
    public AppModelAssert excludesLoadBalancers(LoadBalancerModel... sequence) {
        isNotNull();
        validateIsNotNull(actual.getLoadBalancers());
        validateIsNotNull(sequence);

        for(LoadBalancerModel loadBalancerModel : sequence) {
            excludesLoadBalancer(loadBalancerModel);
        }

        return this;
    }

    /*
     *
     */
    public AppModelAssert excludesLoadBalancers(String... sequence) {
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
