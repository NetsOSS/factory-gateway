package eu.nets.factory.gateway.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoadBalancerRepository extends JpaRepository<LoadBalancer, Long>{
    List<LoadBalancer> findByNameLike(String query);

}
