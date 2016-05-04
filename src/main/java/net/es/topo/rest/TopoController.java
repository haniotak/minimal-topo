package net.es.topo.rest;


import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.factories.SchemaFactoryWrapper;
import lombok.extern.slf4j.Slf4j;
import net.es.topo.dao.TopologyRepository;
import net.es.topo.dto.Converter;
import net.es.topo.dto.TopologyV4;
import net.es.topo.ent.TopoInfo;
import net.es.topo.ent.TopoVertex;
import net.es.topo.ent.Topology;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mapping.model.MappingException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Controller
public class TopoController {
    @Autowired
    private TopologyRepository topoRepo;

    @Autowired
    private Converter v4converter;


    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public void handleResourceNotFoundException(NoSuchElementException ex) {
        // LOG.warn("user requested a strResource which didn't exist", ex);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public void handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        // LOG.warn("user requested a strResource which didn't exist", ex);
    }

    @Value("${server.port}")
    Integer port;

    @Value("${server.address}")
    String address;



    @RequestMapping(value = "/topology", method = RequestMethod.GET)
    @ResponseBody
    public Topology asTopology() {

        String baseUrl = "https://"+address+":"+port+"/topologies/";

        Topology topo = Topology.builder()
                .layer("LOOKUP")
                .name("all_topologies")
                .description("A topology of topologies")
                .vertices(new HashSet<>())
                .edges(new HashSet<>())
                .build();

        topoRepo.findAll().stream()
                .forEach(t -> {
                    TopoVertex tv = TopoVertex.builder()
                            .urn(t.getName())
                            .info(new HashSet<>())
                            .build();
                    TopoInfo ti = TopoInfo.builder()
                            .type("topology info")
                            .url(baseUrl+t.getName())
                            .build();
                    tv.getInfo().add(ti);

                    topo.getVertices().add(tv);
                });

        return topo;
    }

    @RequestMapping(value = "/names", method = RequestMethod.GET)
    @ResponseBody
    public List<String> getNames() {

        return topoRepo.findAll().stream().map(Topology::getName).collect(Collectors.toList());

    }

    @RequestMapping(value = "/topologies/{name}", method = RequestMethod.GET)
    @ResponseBody
    public Topology getTopology(@PathVariable("name") String name) {
        log.info("retrieving " + name);
        return topoRepo.findByName(name).orElseThrow(NoSuchElementException::new);
    }

    @RequestMapping(value = "/topology-v4", method = RequestMethod.GET)
    @ResponseBody
    public TopologyV4 getV4Topology() {
        log.info("retrieving v4 topology");
        Topology v4topo = topoRepo.findByName("ESnetV4").orElseThrow(NoSuchElementException::new);
        return v4converter.convertToV4(v4topo);
    }

    @RequestMapping(value = "/topology-v4-schema", method = RequestMethod.GET)
    @ResponseBody
    public JsonSchema  getV4Schema() throws JsonMappingException {
        ObjectMapper m = new ObjectMapper();
        SchemaFactoryWrapper visitor = new SchemaFactoryWrapper();
        m.acceptJsonFormatVisitor(m.constructType(TopologyV4.class), visitor);
        JsonSchema jsonSchema = visitor.finalSchema();
        return jsonSchema;
    }

    @RequestMapping(value = "/topologies/update", method = RequestMethod.POST)
    @ResponseBody
    public Topology update(@RequestBody Topology inTopo) {
        Optional<Topology> topo = topoRepo.findByName(inTopo.getName());
        if (topo.isPresent()) {
            Long id = topo.get().getId();
            inTopo.setId(id);
        }

        inTopo = topoRepo.save(inTopo);
        return inTopo;
    }



}
