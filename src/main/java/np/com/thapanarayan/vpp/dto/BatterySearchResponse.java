package np.com.thapanarayan.vpp.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class BatterySearchResponse {
    private List<BatteryResponseDto> batteries;
    private BatteryStatisticsDto statistics;


}
