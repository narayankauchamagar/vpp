package np.com.thapanarayan.vpp.dto;

import lombok.*;
import np.com.thapanarayan.vpp.entity.Battery;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
@ToString
public class BatteryServiceResponse implements Serializable {
    private List<Battery> batteries;
    private List<Battery> duplicatedBatteries;

}
