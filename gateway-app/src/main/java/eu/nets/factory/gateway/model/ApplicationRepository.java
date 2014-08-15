package eu.nets.factory.gateway.model;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by kwlar on 19.06.2014.
 */
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    List<Application> findByNameLike(String query);

    @Query("select count(id) from Application where name = ?1")
    long countByName(String name);
}