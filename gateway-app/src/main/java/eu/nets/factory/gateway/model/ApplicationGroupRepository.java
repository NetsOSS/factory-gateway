package eu.nets.factory.gateway.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by kwlar on 19.06.2014.
 */
public interface ApplicationGroupRepository extends JpaRepository<ApplicationGroup, Long> {

        List<ApplicationGroup> findByNameLike(String query);

    @Query("select count(id) from ApplicationGroup where name = ?1")
    long countByName(String name);
}
