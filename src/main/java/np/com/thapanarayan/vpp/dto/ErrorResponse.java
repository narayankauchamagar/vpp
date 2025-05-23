package np.com.thapanarayan.vpp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ErrorResponse<T> implements Serializable {
    private String message;
    private T metadata;

}
