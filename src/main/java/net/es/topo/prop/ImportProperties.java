package net.es.topo.prop;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "parse")
@NoArgsConstructor
public class ImportProperties {
    @NonNull
    private String todayTpFilename;

}
