package net.es.topo.ent;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity
@JsonIgnoreProperties({"id"})
public class Topology {
    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String name;

    private String description;

    private String layer;

    @OneToMany(cascade = CascadeType.ALL)
    private Set<TopoInfo> info;


    @OneToMany(cascade = CascadeType.ALL)
    private Set<TopoVertex> vertices;

    @OneToMany(cascade = CascadeType.ALL)
    private Set<TopoEdge> edges;

}
