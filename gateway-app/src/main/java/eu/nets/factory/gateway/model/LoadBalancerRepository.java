package eu.nets.factory.gateway.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LoadBalancerRepository extends JpaRepository<LoadBalancer, Long>{

    List<LoadBalancer> findByNameLike(String query);

    @Query("select count(id) from LoadBalancer where name = ?1")
    long countByName(String name);

    @Query("select count(id) from LoadBalancer where host = ?1 and installationPath = ?2")
    long countByHostInstallationPath(String host, String installationPath);

    @Query("select count(id) from LoadBalancer where host = ?1 and publicPort = ?2")
    long countByHostPublicPort(String host, int publicPort);
}
