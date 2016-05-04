package net.es.topo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class TopologyV4 {
    private Set<RouterV4> routers;
    private Set<SubnetV4> subnets;
}
