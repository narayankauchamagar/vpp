package np.com.thapanarayan.vpp.dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class ErrorResponse<T> implements Serializable {
    private String message;
    private T metadata;

}
