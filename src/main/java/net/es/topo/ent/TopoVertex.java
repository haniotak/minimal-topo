package net.es.topo.ent;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity
@JsonIgnoreProperties({"id"})
public class TopoVertex {
    @Id
    @GeneratedValue
    private Long id;

    private String urn;

    @OneToMany(cascade = CascadeType.ALL)
    private Set<TopoInfo> info;

}
