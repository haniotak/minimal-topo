package net.es.topo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class IfceV4 {
    private String urn;

    private Set<AddressV4> addresses;

    private Map<String, Object> arbitraryData;

}
