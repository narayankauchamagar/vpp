package np.com.thapanarayan.vpp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class BatterySearchRequest implements Serializable {
    @NotBlank(message = "Min-Postcode is required")
    @Size(min = 4, max= 4, message = "Postcode should be 4 characters") // as per sample format
    private Integer minPostcode;
    @NotBlank(message = "Max-Postcode is required")
    @Size(min = 4, max= 4, message = "Postcode should be 4 characters") // as per sample format
    private Integer maxPostcode;
    private Long minWattCapacity;
    private Long maxWattCapacity;

}
