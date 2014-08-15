package eu.nets.factory.gateway.model;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ApplicationInstanceRepository extends JpaRepository<ApplicationInstance, Long> {

    List<ApplicationInstance> findByNameLike(String query);

    @Query("select count(id) from ApplicationInstance where name = ?1")
    long countByName(String name);
}
