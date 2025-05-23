package np.com.thapanarayan.vpp.mapper;


import np.com.thapanarayan.vpp.dto.BatteryRequestDto;
import np.com.thapanarayan.vpp.dto.BatteryResponseDto;
import np.com.thapanarayan.vpp.entity.Battery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for BatteryMapper.
 * This class tests the mapping logic between DTOs and entities.
 */
@ExtendWith(MockitoExtension.class)
class BatteryMapperTest {

    private BatteryMapper batteryMapper; // The mapper under test

    @BeforeEach
    void setUp() {
        // Initialize the mapper before each test
        batteryMapper = new BatteryMapper();
    }

    @Test
    @DisplayName("Should correctly convert BatteryRequestDto to Battery entity")
    void convertToBattery_singleDto() {
        // Given
        BatteryRequestDto requestDto = new BatteryRequestDto();
        requestDto.setName("TestBattery1");
        requestDto.setPostcode("12345");
        requestDto.setCapacity(100L);

        // When
        Battery battery = batteryMapper.convertToBattery(requestDto);

        // Then
        assertThat(battery).isNotNull();
        assertThat(battery.getName()).isEqualTo("TestBattery1");
        assertThat(battery.getPostcode()).isEqualTo("12345");
        assertThat(battery.getCapacity()).isEqualTo(100L);
        assertThat(battery.getId()).isNull(); // ID should be null as it's a new entity
    }

    @Test
    @DisplayName("Should correctly convert a list of BatteryRequestDto to a list of Battery entities")
    void convertToBatteries_listOfDtos() {
        // Given
        BatteryRequestDto requestDto1 = new BatteryRequestDto();
        requestDto1.setName("TestBattery1");
        requestDto1.setPostcode("11111");
        requestDto1.setCapacity(50L);

        BatteryRequestDto requestDto2 = new BatteryRequestDto();
        requestDto2.setName("TestBattery2");
        requestDto2.setPostcode("22222");
        requestDto2.setCapacity(75L);

        List<BatteryRequestDto> requestDtos = Arrays.asList(requestDto1, requestDto2);

        // When
        List<Battery> batteries = batteryMapper.convertToBatteries(requestDtos);

        // Then
        assertThat(batteries).isNotNull().hasSize(2);

        // Verify first battery
        assertThat(batteries.get(0).getName()).isEqualTo("TestBattery1");
        assertThat(batteries.get(0).getPostcode()).isEqualTo("11111");
        assertThat(batteries.get(0).getCapacity()).isEqualTo(50L);

        // Verify second battery
        assertThat(batteries.get(1).getName()).isEqualTo("TestBattery2");
        assertThat(batteries.get(1).getPostcode()).isEqualTo("22222");
        assertThat(batteries.get(1).getCapacity()).isEqualTo(75L);
    }

    @Test
    @DisplayName("Should correctly convert Battery entity to BatteryResponseDto")
    void convertToBatteryResponse_singleEntity() {
        // Given
        Battery battery = new Battery();
        battery.setId(1L);
        battery.setName("ProdBattery1");
        battery.setPostcode("98765");
        battery.setCapacity(250L);

        // When
        BatteryResponseDto responseDto = batteryMapper.convertToBatteryResponse(battery);

        // Then
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getId()).isEqualTo(1L);
        assertThat(responseDto.getName()).isEqualTo("ProdBattery1");
        assertThat(responseDto.getPostcode()).isEqualTo("98765");
        assertThat(responseDto.getCapacity()).isEqualTo(250L);
    }

    @Test
    @DisplayName("Should correctly convert a list of Battery entities to a list of BatteryResponseDto")
    void convertToBatteryResponse_listOfEntities() {
        // Given
        Battery battery1 = new Battery();
        battery1.setId(10L);
        battery1.setName("ProdBatteryX");
        battery1.setPostcode("33333");
        battery1.setCapacity(120L);


        Battery battery2 = new Battery();
        battery2.setId(11L);
        battery2.setName("ProdBatteryY");
        battery2.setPostcode("44444");
        battery2.setCapacity(180L);

        List<Battery> batteries = Arrays.asList(battery1, battery2);

        // When
        List<BatteryResponseDto> responseDtos = batteryMapper.convertToBatteryResponse(batteries);

        // Then
        assertThat(responseDtos).isNotNull().hasSize(2);

        // Verify first response DTO
        assertThat(responseDtos.get(0).getId()).isEqualTo(10L);
        assertThat(responseDtos.get(0).getName()).isEqualTo("ProdBatteryX");
        assertThat(responseDtos.get(0).getPostcode()).isEqualTo("33333");
        assertThat(responseDtos.get(0).getCapacity()).isEqualTo(120L);

        // Verify second response DTO
        assertThat(responseDtos.get(1).getId()).isEqualTo(11L);
        assertThat(responseDtos.get(1).getName()).isEqualTo("ProdBatteryY");
        assertThat(responseDtos.get(1).getPostcode()).isEqualTo("44444");
        assertThat(responseDtos.get(1).getCapacity()).isEqualTo(180L);
    }

    @Test
    @DisplayName("Should handle empty list for convertToBatteries gracefully")
    void convertToBatteries_emptyList() {
        // When
        List<Battery> batteries = batteryMapper.convertToBatteries(List.of());

        // Then
        assertThat(batteries).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("Should handle empty list for convertToBatteryResponse gracefully")
    void convertToBatteryResponse_emptyList() {
        // When
        List<BatteryResponseDto> responseDtos = batteryMapper.convertToBatteryResponse(List.of());

        // Then
        assertThat(responseDtos).isNotNull().isEmpty();
    }
}
