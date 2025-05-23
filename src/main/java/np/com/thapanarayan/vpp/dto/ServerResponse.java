package np.com.thapanarayan.vpp.dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class ServerResponse<T, E> implements Serializable {
    private String message;
    private T data;
    private E error;

}
