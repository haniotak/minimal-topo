package net.es.topo.client;

import lombok.extern.slf4j.Slf4j;
import net.es.topo.dao.TopologyRepository;
import net.es.topo.ent.Topology;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Slf4j
@Component
public class TopologyConsumerDemo {

    @Autowired
    private TopologyRepository topoRepo;

    @Scheduled(fixedDelay = 30000)
    @Transactional

    public void walkTopology() {
        Topology topology = topoRepo.findByName("ESnetV4").orElseThrow(NoSuchElementException::new);

        topology.getVertices().stream().filter(v -> v.getType().equals("ROUTER")).forEach(r -> {
            Double rand = Math.random();
            if (rand < 0.05) {
                topology.getEdges()
                        .stream()
                        .filter(e -> e.getType().equals("ROUTER_HAS_IFCE") & e.getA().equals(r.getUrn()))
                        .forEach(r_i_edge -> {

                    topology.getEdges()
                            .stream()
                            .filter(e -> e.getType().equals("IFCE_HAS_ADDR") && e.getA().equals(r_i_edge.getZ()))
                            .forEach(i_a_edge -> {
                                log.info(i_a_edge.getA()+" ADDR: "+i_a_edge.getZ());

                    });
                });

            }
        });

    }

}
