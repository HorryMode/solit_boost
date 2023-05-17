package slcd.boost.boost.Syncs.DTOs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class InternalUserInfo {
    @JsonProperty(namespace = "id")
    private String id;

    @JsonProperty
    private String email;

    @JsonProperty
    private String name;

    @JsonProperty
    private String firstName;

    @JsonProperty
    private String middleName;

    @JsonProperty
    private String lastName;

    @JsonProperty
    private String sex;

    @JsonProperty
    private String rate;

    @JsonProperty
    private String location;

    @JsonProperty
    private String dateStart;

    @JsonProperty
    private String birthDate;

    @JsonProperty
    private String subdivision;

    @JsonProperty
    private String post;

    @JsonProperty
    private List<UserProductInfo> products;
}