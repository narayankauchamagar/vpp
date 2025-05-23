package np.com.thapanarayan.vpp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BatteryStatisticsDto {

    private double totalWattCapacity;
    private double averageWattCapacity;
    private int numBatteries;

}
