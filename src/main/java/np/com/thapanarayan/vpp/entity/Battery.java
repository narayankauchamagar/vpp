package np.com.thapanarayan.vpp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "BATTERY")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Battery implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;
    @Column(name = "NAME", length = 100, unique = true)
    private String name;
    @Column(name="POSTCODE", length = 5)
    private Integer postcode;
    @Column(name = "CAPACITY")
    private Long capacity;
    @Column(name = "ADDED_TIMESTAMP")
    private Instant addedTimestamp = Instant.now();
    @Column(name = "LAST_MODIFIED_TIMESTAMP")
    private Instant lastModifiedTimestamp = Instant.now();


}
