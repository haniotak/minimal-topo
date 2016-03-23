package net.es.topo.pop;

import lombok.extern.slf4j.Slf4j;
import net.es.topo.dao.TopologyRepository;
import net.es.topo.ent.TopoEdge;
import net.es.topo.ent.TopoInfo;
import net.es.topo.ent.TopoVertex;
import net.es.topo.ent.Topology;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

@Component
@Slf4j
public class BGP_Populator {
    @Autowired
    private TopologyRepository topoRepo;

    @PostConstruct
    public void initializeTopoDatabase() {
        List<Topology> topos = topoRepo.findAll();

        if (topos.isEmpty()) {
            Map<String, Topology> topologies = new HashMap<>();

            String[] bgp_names = {"canonical", "actual"};
            String[] device_names = {"alpha", "bravo", "charlie"};


            Arrays.asList(bgp_names).stream().forEach(bgp_name -> {
                Topology topo = Topology.builder()
                        .name(bgp_name)
                        .layer("BGP")
                        .description("a BGP topology")
                        .edges(new HashSet<>())
                        .vertices(new HashSet<>())
                        .info(new HashSet<>())
                        .build();


                TopoInfo topology_info = TopoInfo.builder()
                        .type("bgp-topo")
                        .url("https://bgp.info.server/" + bgp_name)
                        .build();

                topo.getInfo().add(topology_info);
                topologies.put(bgp_name, topo);

                Arrays.asList(device_names).stream().forEach(dev_name_i -> {

                    TopoVertex av = TopoVertex.builder()
                            .urn(dev_name_i)
                            .info(new HashSet<>())
                            .build();
                    topo.getVertices().add(av);

                    TopoInfo device_info = TopoInfo.builder()
                            .type("device-info")
                            .url("https://device.info.server/" + dev_name_i)
                            .build();
                    av.getInfo().add(device_info);

                    Arrays.asList(device_names).stream()
                            .filter(t -> !dev_name_i.equals(t))
                            .forEach(dev_name_j -> {
                                TopoEdge edge = TopoEdge.builder()
                                        .type("bgp-peering")
                                        .a(dev_name_i)
                                        .z(dev_name_j)
                                        .build();

                                if (bgp_name.equals("actual")) {
                                    if (!dev_name_i.equals("bravo") && !dev_name_j.equals("bravo")) {
                                        topo.getEdges().add(edge);
                                    }
                                } else {
                                    topo.getEdges().add(edge);
                                }
                            });
                });

            });


            topoRepo.save(topologies.values());

        } else {
            log.info("topo db not empty");


        }

    }
}