package np.com.thapanarayan.vpp.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class BatterySearchRequest implements Serializable {

    @Size(min = 1, message = "Min-Postcode must be at least 1 digits")
    @NotNull(message = "Min-Postcode is required")
    private String minPostcode;

    @NotNull(message = "Max-Postcode is required")
    @Size(min = 1, message = "Max-Postcode must be at least 1 digits")
    private String maxPostcode;

    @Positive(message = "Min Watt Capacity should be positive number")
    private Long minWattCapacity;
    @Positive(message = "Max-Watt Capacity should be positive number")
    private Long maxWattCapacity;

}
