package net.es.topo.dto;


import lombok.extern.slf4j.Slf4j;
import net.es.topo.ent.Topology;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
@Slf4j
public class Converter {
    public TopologyV4 convertToV4(Topology topology) {
        TopologyV4 result = TopologyV4.builder().routers(new HashSet<>()).subnets(new HashSet<>()).build();

        topology.getVertices().stream().filter(v -> v.getType().equals("ROUTER")).forEach(r -> {

            RouterV4 r_v4 = RouterV4.builder().ifces(new HashSet<>()).urn(r.getUrn()).build();
            result.getRouters().add(r_v4);

            topology.getEdges()
                    .stream()
                    .filter(e -> e.getType().equals("ROUTER_HAS_IFCE") & e.getA().equals(r.getUrn()))
                    .forEach(r_i_edge -> {
                        String ifce_urn = r_i_edge.getZ();
                        IfceV4 ifceV4 = IfceV4.builder().addresses(new HashSet<>()).urn(ifce_urn).build();
                        r_v4.getIfces().add(ifceV4);

                        topology.getEdges()
                                .stream()
                                .filter(e -> e.getType().equals("IFCE_HAS_ADDR") && e.getA().equals(r_i_edge.getZ()))
                                .forEach(i_a_edge -> {
                                    AddressV4 addressV4 = AddressV4.builder().address(i_a_edge.getZ()).build();
                                    ifceV4.getAddresses().add(addressV4);
                                });
                    });

        });

        topology.getVertices().stream().filter(v -> v.getType().equals("SUBNET")).forEach(s -> {

            SubnetV4 subnetV4 = SubnetV4.builder().addresses(new HashSet<>()).base(s.getUrn()).build();

            topology.getEdges()
                    .stream()
                    .filter(e -> e.getType().equals("SUBNET_CONTAINS_ADDR") && e.getA().equals(s.getUrn()))
                    .forEach(s_a_edge -> {
                        AddressV4 addressV4 = AddressV4.builder().address(s_a_edge.getZ()).build();
                        subnetV4.getAddresses().add(addressV4);

            });
            result.getSubnets().add(subnetV4);

        });


        return result;

    }

}
