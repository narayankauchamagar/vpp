package np.com.thapanarayan.vpp.dto;

import lombok.*;

import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class BatteryResponseDto implements Serializable {
    private Long id;
    private String name;
    private String postcode;
    private Long capacity;
    private Instant addedTimestamp;
    private Instant lastEditedTimestamp;
}
