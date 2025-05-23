package np.com.thapanarayan.vpp.dto;


import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class BatteryRequestDto implements Serializable {

    @NotBlank(message = "Battery Name is required")
    @Size(min = 5, max= 15, message = "Battery Name should be minimum of 5 characters")
    private String name;
    @NotBlank(message = "Postcode is required")
    @Size(min = 4, max= 4, message = "Postcode should be 4 characters") // as per sample format
    private Integer postcode;
    @NotBlank(message = "Capacity is required")
    @Positive(message = "Capacity should be positive number")
    private Long capacity;


}
