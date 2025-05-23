package np.com.thapanarayan.vpp.service;

import lombok.RequiredArgsConstructor;
import np.com.thapanarayan.vpp.dto.BatteryServiceResponse;
import np.com.thapanarayan.vpp.entity.Battery;
import np.com.thapanarayan.vpp.repo.BatteryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BatteryServiceImpl implements BatteryService {

    private final BatteryRepository batteryRepository;


    // Add your service methods here
    public BatteryServiceResponse saveBatteries(List<Battery> batteries) {

        List<Battery> duplicateBatteries = batteries
                .stream()
                .parallel()
                .filter(battery -> batteryRepository.findBatteryByName(battery.getName()).isPresent())
                .toList();

        List<Battery> savedBatteries = batteries.stream()
                .filter(battery ->  !duplicateBatteries.contains(battery))
                .map(batteryRepository::save)
                .toList();

        return BatteryServiceResponse
                .builder()
                .batteries(savedBatteries)
                .duplicatedBatteries(duplicateBatteries)
                .build();

    }

    @Override
    public BatteryServiceResponse getBatteriesByPostcodeRange(Integer startPostcode, Integer endPostcode) {

        List<Battery> batteryList=  batteryRepository.findByPostcodeBetween(startPostcode, endPostcode);
        return BatteryServiceResponse
                .builder()
                .batteries(batteryList)
                .build();

    }
}
