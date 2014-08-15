package eu.nets.factory.gateway.model;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by kwlar on 19.06.2014.
 */
public interface ApplicationGroupRepository extends JpaRepository<ApplicationGroup, Long> {

    List<ApplicationGroup> findByNameLike(String query);

    @Query("select count(id) from ApplicationGroup where name = ?1")
    long countByName(String name);

    @Query("select count(id) from ApplicationGroup where port = ?1")
    long countByPort(int port);
}
