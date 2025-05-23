package np.com.thapanarayan.vpp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class BatteryResponseDto implements Serializable {
    private Long id;
    private String name;
    private String postcode;
    private Long capacity;
    private Instant addedTimestamp;
    private Instant lastEditedTimestamp;
}
