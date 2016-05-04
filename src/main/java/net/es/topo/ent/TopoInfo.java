package net.es.topo.ent;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity
@JsonIgnoreProperties({"id"})
public class TopoInfo {
    @Id
    @GeneratedValue
    private Long id;

    private String type;
    private String url;

    @Lob
    @Column(length = 65535)
    private String data;
}
