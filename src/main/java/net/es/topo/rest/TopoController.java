package net.es.topo.rest;


import lombok.extern.slf4j.Slf4j;
import net.es.topo.dao.TopologyRepository;
import net.es.topo.ent.Topology;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Controller
public class TopoController {
    @Autowired
    private TopologyRepository topoRepo;

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


    @RequestMapping(value = "/topologies/", method = RequestMethod.GET)
    @ResponseBody
    public List<String> getAll() {
        return topoRepo.findAll().stream()
                .map(Topology::getName)
                .collect(Collectors.toList());
    }

    @RequestMapping(value = "/topologies/{name}", method = RequestMethod.GET)
    @ResponseBody
    public Topology getTopology(@PathVariable("name") String name) {
        log.info("retrieving " + name);
        return topoRepo.findByName(name).orElseThrow(NoSuchElementException::new);
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