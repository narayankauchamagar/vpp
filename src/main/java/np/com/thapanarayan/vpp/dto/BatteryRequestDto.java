package np.com.thapanarayan.vpp.dto;


import jakarta.validation.constraints.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class BatteryRequestDto implements Serializable {

    @NotBlank(message = "Battery Name is required")
    @Size(min = 5, max= 50, message = "Battery Name should be of 5-50 characters")
    private String name;
    @NotNull(message = "Postcode is required")
    @Size(min = 1, message = "postcode must be at least 1 digits")
    private String postcode;
    @NotNull(message = "Capacity is required")
    @Positive(message = "Capacity should be positive number")
    private Long capacity;


}
