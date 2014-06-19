package eu.nets.factory.gateway.model;


import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Created by kwlar on 19.06.2014.
 */
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    List<Application> findByNameLike(String query);
}