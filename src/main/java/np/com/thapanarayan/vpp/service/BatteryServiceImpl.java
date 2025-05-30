package np.com.thapanarayan.vpp.service;

import lombok.RequiredArgsConstructor;
import np.com.thapanarayan.vpp.dto.*;
import np.com.thapanarayan.vpp.entity.Battery;
import np.com.thapanarayan.vpp.mapper.BatteryMapper;
import np.com.thapanarayan.vpp.repo.BatteryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BatteryServiceImpl implements BatteryService {

    private final BatteryRepository batteryRepository;
    private final BatteryMapper batteryMapper;

    @Transactional
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
    public BatterySearchResponse getBatteriesByPostcodeRange(BatterySearchRequest request) {
        List<Battery> batteryList;
        if (request.getMinWattCapacity() != null || request.getMaxWattCapacity() != null) {
           batteryList = batteryRepository.findByPostcodeBetweenAndCapacityBetween(request.getMinPostcode(), request.getMaxPostcode(), request.getMinWattCapacity(), request.getMaxWattCapacity());
        } else {
            batteryList= batteryRepository.findByPostcodeBetween(request.getMinPostcode(), request.getMaxPostcode());
        }

        List<BatteryResponseDto> batteriesFetched = batteryMapper.convertToBatteryResponse( batteryList);

        // Calculate statistics
        double totalWattCapacity = batteryList.stream()
                .mapToDouble(Battery::getCapacity)
                .sum();
        int numBatteries = batteryList.size();
        double averageWattCapacity = numBatteries > 0 ? totalWattCapacity / numBatteries : 0;

        BatteryStatisticsDto statistics = new BatteryStatisticsDto(totalWattCapacity, averageWattCapacity, numBatteries);

        return BatterySearchResponse.builder()
                .batteries(batteriesFetched)
                .statistics(statistics)
                .build();

    }
}
