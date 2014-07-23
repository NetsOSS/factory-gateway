package eu.nets.factory.gateway.model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
public class HeaderRule extends AbstractEntity {



    @NotNull
    public String name;
    @NotNull
    public String prefixMatch;

    @NotNull
    @ManyToOne
    private Application application;

    public HeaderRule(String name, String prefixMatch, Application application) {
        this.name = name;
        this.prefixMatch = prefixMatch;
        this.application=application;
    }
    public HeaderRule() {

    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrefixMatch() {
        return prefixMatch;
    }

    public void setPrefixMatch(String prefixMatch) {
        this.prefixMatch = prefixMatch;
    }

    public void setApplication(Application application) { this.application = application; }
    public Application getApplication() {
        return application;
    }
}
