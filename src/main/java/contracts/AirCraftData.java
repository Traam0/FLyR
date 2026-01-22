package contracts;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AirCraftData(
        @JsonProperty("id") int id,
        @JsonProperty("code") String code,
        @JsonProperty("model") String model,
        @JsonProperty("total_capacity") int totalCapacity,
        @JsonProperty("economy_capacity") int economyCapacity,
        @JsonProperty("business_capacity") int businessCapacity) {
}
