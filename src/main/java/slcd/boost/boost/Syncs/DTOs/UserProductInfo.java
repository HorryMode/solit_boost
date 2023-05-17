package slcd.boost.boost.Syncs.DTOs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserProductInfo {
    @JsonProperty
    private ProductInfo product;

    @JsonProperty
    private String role;

    @JsonProperty
    private Long busy;
}
