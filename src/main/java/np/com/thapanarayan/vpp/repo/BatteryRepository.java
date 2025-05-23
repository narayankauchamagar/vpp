package np.com.thapanarayan.vpp.repo;

import np.com.thapanarayan.vpp.entity.Battery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BatteryRepository extends JpaRepository<Battery, Long>, JpaSpecificationExecutor<Battery> {

    @Query("select battery from Battery battery where battery.name = :name")
    Optional<Battery> findBatteryByName(String name);

    @Query("select battery from Battery battery where battery.postcode = :postcode")
    Optional<Battery> findByPostcode(String postcode);

    @Query("select battery from Battery battery where battery.postcode between :startPostcode and :endPostcode order by battery.name asc")
    List<Battery> findByPostcodeBetween(Integer startPostcode, Integer endPostcode);

    @Query("select battery from Battery battery where battery.postcode between :startPostcode and :endPostcode AND battery.capacity between :minCapacity and :maxCapacity order by battery.name asc")
    List<Battery> findByPostcodeBetweenAndCapacityBetween(Integer startPostcode, Integer endPostcode, Long minCapacity, Long maxCapacity);

}
