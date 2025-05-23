package np.com.thapanarayan.vpp.mapper;

import np.com.thapanarayan.vpp.dto.BatteryRequestDto;
import np.com.thapanarayan.vpp.dto.BatteryResponseDto;
import np.com.thapanarayan.vpp.entity.Battery;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BatteryMapper {


    public List<Battery> convertToBatteries( List<BatteryRequestDto> batteryRequestDtos){
        return batteryRequestDtos.stream()
                .map(this::convertToBattery)
                .toList();
    }

    public Battery convertToBattery(BatteryRequestDto batteryRequestDto) {
        Battery battery = new Battery();
        battery.setName(batteryRequestDto.getName());
        battery.setPostcode(batteryRequestDto.getPostcode());
        battery.setCapacity(batteryRequestDto.getCapacity());
        return battery;
    }


    // this can hide database column implementation
    // can hide audit columns like createdAt, updatedAt, createdBy, updatedBy
    // and further columns like status, isDeleted, isActive
    // before sending data to client
    public List<BatteryResponseDto> convertToBatteryResponse(List<Battery> batteryList){
        return  batteryList.stream()
                .map(this::convertToBatteryResponse)
                .toList();
    }

    public BatteryResponseDto convertToBatteryResponse(Battery battery) {
        return BatteryResponseDto
                .builder()
                .id(battery.getId())
                .name(battery.getName())
                .capacity(battery.getCapacity())
                .postcode(battery.getPostcode().toString())
                .build();
    }
}
