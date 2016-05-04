package net.es.topo.parse;

import lombok.extern.slf4j.Slf4j;
import net.es.topo.dao.TopologyRepository;
import net.es.topo.ent.TopoEdge;
import net.es.topo.ent.TopoVertex;
import net.es.topo.ent.Topology;
import net.es.topo.prop.ImportProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;


@Slf4j
@Service
public class TodayDotTpParser {
    @Autowired
    private TopologyRepository topoRepo;

    @Autowired
    private ImportProperties importProps;

    private final String SEP = "-+-";
    private final String SEP_RE = "\\-\\+\\-";

    private final String BASE = "BASE";
    private final String ADDR = "ADDR";
    private final String MASK = "MASK";


    @PostConstruct
    public void startup() throws IOException {
        log.info("Startup. Will attempt import from files set in parse.todayTpFilename property.");
        if (importProps == null) {
            log.error("No 'parse' stanza in application properties! Skipping import.");
            return;
        }

        String todayTpFilename = importProps.getTodayTpFilename();
        if (todayTpFilename == null) {
            log.error("No 'topo.todayTpFilename' entry in application properties! Skipping import.");
            return;
        }

        Topology topology = Topology.builder()
                .description("ipv4 topology")
                .layer("IPV4")
                .name("ESnetV4")
                .vertices(new HashSet<>())
                .edges(new HashSet<>())
                .build();

        List<String> lines = Files.readAllLines(new File(todayTpFilename).toPath());
        List<String> routers = collectRouters(lines);

        Set<String> subnets = new HashSet<>();
        Set<String> addrs = new HashSet<>();


        routers.stream().forEach(r -> {
            String router_urn = r;

            TopoVertex router_vertex = TopoVertex.builder().urn(router_urn).build();
            topology.getVertices().add(router_vertex);

            List<String> ifces = collectIfces(lines, r);

            ifces.stream().forEach(i -> {
                String ifce_urn = r+"::"+i;

                TopoVertex ifce_vertex = TopoVertex.builder().urn(ifce_urn).build();
                topology.getVertices().add(ifce_vertex);



                Map ipv4net = collectIpv4Net(lines, r, i);
                String addr = ipv4net.get(ADDR)+"/"+ipv4net.get(MASK);
                String subnet = ipv4net.get(BASE)+"/"+ipv4net.get(MASK);
                addrs.add(addr);
                subnets.add(subnet);

                String addr_urn = addr;
                String subnet_urn = subnet;

                TopoVertex addr_vertex = TopoVertex.builder().urn(addr_urn).build();
                TopoVertex subnet_vertex = TopoVertex.builder().urn(subnet_urn).build();

                topology.getVertices().add(addr_vertex);
                topology.getVertices().add(subnet_vertex);

                TopoEdge router_to_ifce = TopoEdge.builder().a(router_urn).z(ifce_urn).type("ROUTER_HAS_IFCE").build();
                topology.getEdges().add(router_to_ifce);

                TopoEdge subnet_to_addr = TopoEdge.builder().a(subnet).z(addr_urn).type("SUBNET_CONTAINS_ADDR").build();
                topology.getEdges().add(subnet_to_addr);

                TopoEdge ifce_to_addr = TopoEdge.builder().a(ifce_urn).z(addr_urn).type("IFCE_HAS_ADDR").build();
                topology.getEdges().add(ifce_to_addr);

                TopoEdge ifce_to_subnet = TopoEdge.builder().a(ifce_urn).z(subnet_urn).type("IFCE_ROUTES_SUBNET").build();
                topology.getEdges().add(ifce_to_subnet);

            });
        });

        topoRepo.save(topology);

    }

    public List<String> collectIfces(List<String> lines, String router) {
        List<String> ifces = new ArrayList<>();
        lines.stream()
                .filter(l -> l.startsWith("ipv4net") && l.contains(SEP+router+SEP+"int_name"+SEP))
                .forEach( l -> {
                    String[] parts = l.split(SEP_RE);
                    assert parts.length == 5;
                    String ifce = parts[4];
                    ifces.add(ifce);
                });

        return ifces;

    }

    public Map<String, String> collectIpv4Net(List<String> lines, String router, String ifce) {
        String base = null;
        String mask = null;
        String addr = null;

        Map<String, String> result = new HashMap<>();
        for (String line : lines) {
            // these are all the ipv4net lines for the router
            if (line.startsWith("ipv4net") && line.contains(SEP+router+SEP)) {
                String[] parts = line.split(SEP_RE);

                if (base == null) {
                    if (line.contains(SEP + "int_name" + SEP + ifce)) {
                        assert parts.length == 5;
                        base = parts[1];
                    }
                } else if (mask == null) {
                    if (line.startsWith("ipv4net"+SEP+base+SEP+router) && line.contains(SEP+"mask"+SEP)) {
                        assert parts.length == 7;
                        mask = parts[6];
                        addr = parts[4];
                    }
                }

            }
        }

        result.put(BASE, base);
        result.put(MASK, mask);
        result.put(ADDR, addr);
        return result;
    }


    public List<String> collectRouters(List<String> lines) {
        List<String> routers = new ArrayList<>();
        lines.stream().filter( l -> l.startsWith("router_system") && l.contains(SEP+"name"+SEP)).forEach( l -> {
            String[] parts = l.split(SEP_RE);
            assert parts.length == 4;
            String name = parts[1];
            routers.add(name);
        });

        return routers;

    }

}
