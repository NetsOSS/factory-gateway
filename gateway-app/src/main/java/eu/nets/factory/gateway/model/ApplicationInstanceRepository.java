package eu.nets.factory.gateway.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ApplicationInstanceRepository extends JpaRepository<ApplicationInstance, Long> {

    List<ApplicationInstance> findByNameLike(String query);

    @Query("select count(id) from ApplicationInstance where name = ?1")
    long countByName(String name);
}
