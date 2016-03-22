package net.es.topo.dao;

import net.es.topo.ent.Topology;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TopologyRepository extends CrudRepository<Topology, Long> {
    List<Topology> findAll();
    Optional<Topology> findByName(String name);

}
