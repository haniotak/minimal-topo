package net.es.topo.pop;

import lombok.extern.slf4j.Slf4j;
import net.es.topo.dao.TopologyRepository;
import net.es.topo.ent.TopoEdge;
import net.es.topo.ent.TopoVertex;
import net.es.topo.ent.Topology;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Component
@Slf4j
public class BGP_Populator {
    @Autowired
    private TopologyRepository topoRepo;

    @PostConstruct
    public void initializeTopoDatabase() {
        List<Topology> topos = topoRepo.findAll();

        if (topos.isEmpty()) {

            Topology canonical = Topology.builder()
                    .name("bgp-canonical")
                    .layer("BGP")
                    .edges(new HashSet<>())
                    .vertices(new HashSet<>())
                    .build();

            Topology actual = Topology.builder()
                    .name("bgp-actual")
                    .layer("BGP")
                    .edges(new HashSet<>())
                    .vertices(new HashSet<>())
                    .build();


            String[] names = {"alpha", "bravo", "charlie", "delta"};

            Arrays.asList(names).stream().forEach(name_i -> {
                TopoVertex cv = TopoVertex.builder().urn(name_i).build();
                canonical.getVertices().add(cv);

                TopoVertex av = TopoVertex.builder().urn(name_i).build();
                actual.getVertices().add(av);

                Arrays.asList(names).stream().filter(t -> !name_i.equals(t)).forEach(name_j -> {
                    TopoEdge ce = TopoEdge.builder()
                            .a(name_i)
                            .z(name_j)
                            .build();

                    canonical.getEdges().add(ce);

                    if (!name_i.equals("bravo") && !name_j.equals("bravo")) {
                        TopoEdge ae = TopoEdge.builder()
                                .a(name_i)
                                .z(name_j)
                                .build();
                        actual.getEdges().add(ae);

                    }
                });
            });
            topoRepo.save(canonical);
            topoRepo.save(actual);


        } else {
            log.info("topo db not empty");


        }

    }
}