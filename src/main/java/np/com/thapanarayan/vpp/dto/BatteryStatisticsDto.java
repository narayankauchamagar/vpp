package np.com.thapanarayan.vpp.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class BatteryStatisticsDto {

    private double totalWattCapacity;
    private double averageWattCapacity;
    private int numBatteries;

}
