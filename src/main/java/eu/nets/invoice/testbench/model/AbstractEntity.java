package eu.nets.oss.template.webapp.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.SequenceGenerator;

import org.hibernate.annotations.AttributeAccessor;

@MappedSuperclass
public class AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq-gen")
    @SequenceGenerator(name = "seq-gen", sequenceName = "id_sequence", initialValue = 10000, allocationSize = 1000)
    @AttributeAccessor("property")
    private Long id;

    public Long getId() {
        return id;
    }
}
