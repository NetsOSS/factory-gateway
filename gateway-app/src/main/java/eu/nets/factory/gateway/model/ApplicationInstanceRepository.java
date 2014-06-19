package eu.nets.factory.gateway.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by sleru on 19.06.2014.
 */
public interface ApplicationInstanceRepository extends JpaRepository<ApplicationInstance, Long> {

    List<ApplicationInstance> findByNameLike(String query);
}
