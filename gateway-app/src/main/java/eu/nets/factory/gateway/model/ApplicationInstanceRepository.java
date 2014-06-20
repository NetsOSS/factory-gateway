package eu.nets.factory.gateway.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationInstanceRepository extends JpaRepository<ApplicationInstance, Long> {

    List<ApplicationInstance> findByNameLike(String query);
}
