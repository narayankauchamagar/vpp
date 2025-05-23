package np.com.thapanarayan.vpp.service;

import np.com.thapanarayan.vpp.dto.*;
import np.com.thapanarayan.vpp.entity.Battery;
import np.com.thapanarayan.vpp.mapper.BatteryMapper;
import np.com.thapanarayan.vpp.repo.BatteryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BatteryServiceImplTest {

    @Mock
    private BatteryRepository batteryRepository; // Mock the repository dependency

    @Mock
    private BatteryMapper batteryMapper; // Mock the mapper dependency

    @InjectMocks
    private BatteryServiceImpl batteryService; // Inject mocks into the service under test

    private Battery battery1;
    private Battery battery2;
    private Battery battery3;

    @BeforeEach
    void setUp() {
        // Initialize common Battery objects for use in tests
        battery1 = new Battery();
        battery1.setId(1L);
        battery1.setName("BatteryA");
        battery1.setPostcode("12345");
        battery1.setCapacity(100L);

        battery2 = new Battery();
        battery2.setId(2L);
        battery2.setName("BatteryB");
        battery2.setPostcode("12346");
        battery2.setCapacity(200L);

        battery3 = new Battery();
        battery3.setId(3L);
        battery3.setName("BatteryC");
        battery3.setPostcode("12347");
        battery3.setCapacity(150L);

    }

    @Test
    @DisplayName("Should save new batteries when no duplicates exist")
    void saveBatteries_noDuplicates() {
        // Arrange
        List<Battery> newBatteries = Arrays.asList(battery1, battery2);

        // Mock repository behavior: no existing batteries found by name
        when(batteryRepository.findBatteryByName(battery1.getName())).thenReturn(Optional.empty());
        when(batteryRepository.findBatteryByName(battery2.getName())).thenReturn(Optional.empty());

        // Mock repository save behavior: return the same battery after saving
        when(batteryRepository.save(battery1)).thenReturn(battery1);
        when(batteryRepository.save(battery2)).thenReturn(battery2);

        // Act
        BatteryServiceResponse response = batteryService.saveBatteries(newBatteries);

        // Assert
        assertNotNull(response);
        assertEquals(2, response.getBatteries().size());
        assertTrue(response.getBatteries().containsAll(newBatteries));
        assertTrue(response.getDuplicatedBatteries().isEmpty());

        // Verify that findBatteryByName and save methods were called correctly
        verify(batteryRepository, times(1)).findBatteryByName(battery1.getName());
        verify(batteryRepository, times(1)).findBatteryByName(battery2.getName());
        verify(batteryRepository, times(1)).save(battery1);
        verify(batteryRepository, times(1)).save(battery2);
    }

    @Test
    @DisplayName("Should save new batteries and identify duplicates")
    void saveBatteries_withDuplicates() {
        // Arrange
        List<Battery> batteriesToSave = Arrays.asList(battery1, battery2, battery3); // battery1 is a duplicate
        Battery existingBattery1 = new Battery();
        existingBattery1.setId(4L);
        existingBattery1.setName("BatteryA");
        existingBattery1.setPostcode("99999");
        existingBattery1.setCapacity(50L);

        // Mock repository behavior: battery1 already exists, battery2 and battery3 are new
        when(batteryRepository.findBatteryByName(battery1.getName())).thenReturn(Optional.of(existingBattery1));
        when(batteryRepository.findBatteryByName(battery2.getName())).thenReturn(Optional.empty());
        when(batteryRepository.findBatteryByName(battery3.getName())).thenReturn(Optional.empty());

        // Mock repository save behavior for new batteries
        when(batteryRepository.save(battery2)).thenReturn(battery2);
        when(batteryRepository.save(battery3)).thenReturn(battery3);

        // Act
        BatteryServiceResponse response = batteryService.saveBatteries(batteriesToSave);

        // Assert
        assertNotNull(response);
        assertEquals(2, response.getBatteries().size()); // battery2 and battery3 should be saved
        assertTrue(response.getBatteries().contains(battery2));
        assertTrue(response.getBatteries().contains(battery3));
        assertFalse(response.getBatteries().contains(battery1)); // battery1 should not be in saved list

        assertEquals(1, response.getDuplicatedBatteries().size()); // battery1 should be in duplicates
        assertTrue(response.getDuplicatedBatteries().contains(battery1));

        // Verify method calls
        verify(batteryRepository, times(1)).findBatteryByName(battery1.getName());
        verify(batteryRepository, times(1)).findBatteryByName(battery2.getName());
        verify(batteryRepository, times(1)).findBatteryByName(battery3.getName());
        verify(batteryRepository, times(1)).save(battery2);
        verify(batteryRepository, times(1)).save(battery3);
        verify(batteryRepository, never()).save(battery1); // Ensure duplicate is not saved
    }

    @Test
    @DisplayName("Should return all batteries as duplicates when all exist")
    void saveBatteries_allDuplicates() {
        // Arrange
        List<Battery> batteriesToSave = Arrays.asList(battery1, battery2);
        Battery existingBattery1 = new Battery();
        existingBattery1.setName("BatteryA");
        existingBattery1.setPostcode("99999");
        existingBattery1.setCapacity(50L);

        Battery existingBattery2 = new Battery();
        existingBattery2.setName("BatteryB");
        existingBattery2.setPostcode("88888");
        existingBattery2.setCapacity(75L);

        // Mock repository behavior: both batteries already exist
        when(batteryRepository.findBatteryByName(battery1.getName())).thenReturn(Optional.of(existingBattery1));
        when(batteryRepository.findBatteryByName(battery2.getName())).thenReturn(Optional.of(existingBattery2));

        // Act
        BatteryServiceResponse response = batteryService.saveBatteries(batteriesToSave);

        // Assert
        assertNotNull(response);
        assertTrue(response.getBatteries().isEmpty()); // No batteries should be saved
        assertEquals(2, response.getDuplicatedBatteries().size());
        assertTrue(response.getDuplicatedBatteries().containsAll(batteriesToSave));

        // Verify method calls
        verify(batteryRepository, times(1)).findBatteryByName(battery1.getName());
        verify(batteryRepository, times(1)).findBatteryByName(battery2.getName());
        verify(batteryRepository, never()).save(any(Battery.class)); // No save calls should occur
    }

    @Test
    @DisplayName("Should retrieve batteries by postcode range only and calculate statistics")
    void getBatteriesByPostcodeRange_onlyPostcode() {
        // Arrange
        BatterySearchRequest request = new BatterySearchRequest();
        request.setMinPostcode("12340");
        request.setMaxPostcode("12350");


        List<Battery> foundBatteries = Arrays.asList(battery1, battery2, battery3);
        List<BatteryResponseDto> mappedBatteries = Arrays.asList(
                BatteryResponseDto.builder().name("BatteryA").postcode("12345").capacity(100L).build(),
                BatteryResponseDto.builder().name("BatteryB").postcode("12346").capacity(200L).build(),
                BatteryResponseDto.builder().name("BatteryC").postcode("12347").capacity(150L).build()
        );

        // Mock repository behavior for postcode search
        when(batteryRepository.findByPostcodeBetween(request.getMinPostcode(), request.getMaxPostcode()))
                .thenReturn(foundBatteries);
        // Mock mapper behavior
        when(batteryMapper.convertToBatteryResponse(foundBatteries)).thenReturn(mappedBatteries);

        // Act
        BatterySearchResponse response = batteryService.getBatteriesByPostcodeRange(request);

        // Assert
        assertNotNull(response);
        assertEquals(3, response.getBatteries().size());
        assertEquals(mappedBatteries, response.getBatteries());

        BatteryStatisticsDto statistics = response.getStatistics();
        assertNotNull(statistics);
        assertEquals(450.0, statistics.getTotalWattCapacity()); // 100 + 200 + 150
        assertEquals(150.0, statistics.getAverageWattCapacity()); // 450 / 3
        assertEquals(3, statistics.getNumBatteries());

        // Verify correct repository method was called
        verify(batteryRepository, times(1)).findByPostcodeBetween(request.getMinPostcode(), request.getMaxPostcode());
        verify(batteryRepository, never()).findByPostcodeBetweenAndCapacityBetween(any(), any(), any(), any());
        verify(batteryMapper, times(1)).convertToBatteryResponse(foundBatteries);
    }

    @Test
    @DisplayName("Should retrieve batteries by postcode and capacity range and calculate statistics")
    void getBatteriesByPostcodeRange_withCapacity() {
        // Arrange
        BatterySearchRequest request = new BatterySearchRequest();
        request.setMinPostcode("12340");
        request.setMaxPostcode("12350");
        request.setMinWattCapacity(120L);
        request.setMaxWattCapacity(250L);

        // battery1 (100) is out of capacity range, battery2 (200) and battery3 (150) are in range
        List<Battery> foundBatteries = Arrays.asList(battery2, battery3);
        List<BatteryResponseDto> mappedBatteries = Arrays.asList(
                BatteryResponseDto.builder().name("BatteryB").postcode("12346").capacity(200L).build(),
                BatteryResponseDto.builder().name("BatteryC").postcode("12347").capacity(150L).build()
        );

        // Mock repository behavior for postcode and capacity search
        when(batteryRepository.findByPostcodeBetweenAndCapacityBetween(
                request.getMinPostcode(), request.getMaxPostcode(),
                request.getMinWattCapacity(), request.getMaxWattCapacity()))
                .thenReturn(foundBatteries);
        // Mock mapper behavior
        when(batteryMapper.convertToBatteryResponse(foundBatteries)).thenReturn(mappedBatteries);

        // Act
        BatterySearchResponse response = batteryService.getBatteriesByPostcodeRange(request);

        // Assert
        assertNotNull(response);
        assertEquals(2, response.getBatteries().size());
        assertEquals(mappedBatteries, response.getBatteries());

        BatteryStatisticsDto statistics = response.getStatistics();
        assertNotNull(statistics);
        assertEquals(350.0, statistics.getTotalWattCapacity()); // 200 + 150
        assertEquals(175.0, statistics.getAverageWattCapacity()); // 350 / 2
        assertEquals(2, statistics.getNumBatteries());

        // Verify correct repository method was called
        verify(batteryRepository, times(1)).findByPostcodeBetweenAndCapacityBetween(
                request.getMinPostcode(), request.getMaxPostcode(),
                request.getMinWattCapacity(), request.getMaxWattCapacity());
        verify(batteryRepository, never()).findByPostcodeBetween(any(), any());
        verify(batteryMapper, times(1)).convertToBatteryResponse(foundBatteries);
    }

    @Test
    @DisplayName("Should return empty lists and zero statistics when no batteries are found")
    void getBatteriesByPostcodeRange_noBatteriesFound() {
        // Arrange
        BatterySearchRequest request = new BatterySearchRequest();
        request.setMinPostcode("99990");
        request.setMaxPostcode("99999");

        // Mock repository to return an empty list
        when(batteryRepository.findByPostcodeBetween(request.getMinPostcode(), request.getMaxPostcode()))
                .thenReturn(Collections.emptyList());
        // Mock mapper to return an empty list
        when(batteryMapper.convertToBatteryResponse(Collections.emptyList())).thenReturn(Collections.emptyList());

        // Act
        BatterySearchResponse response = batteryService.getBatteriesByPostcodeRange(request);

        // Assert
        assertNotNull(response);
        assertTrue(response.getBatteries().isEmpty());

        BatteryStatisticsDto statistics = response.getStatistics();
        assertNotNull(statistics);
        assertEquals(0.0, statistics.getTotalWattCapacity());
        assertEquals(0.0, statistics.getAverageWattCapacity());
        assertEquals(0, statistics.getNumBatteries());

        // Verify correct repository method was called
        verify(batteryRepository, times(1)).findByPostcodeBetween(request.getMinPostcode(), request.getMaxPostcode());
        verify(batteryMapper, times(1)).convertToBatteryResponse(Collections.emptyList());
    }

    @Test
    @DisplayName("Should handle empty list of batteries to save gracefully")
    void saveBatteries_emptyList() {
        // Arrange
        List<Battery> emptyList = Collections.emptyList();

        // Act
        BatteryServiceResponse response = batteryService.saveBatteries(emptyList);

        // Assert
        assertNotNull(response);
        assertTrue(response.getBatteries().isEmpty());
        assertTrue(response.getDuplicatedBatteries().isEmpty());

        // Verify no interactions with the repository
        verify(batteryRepository, never()).findBatteryByName(anyString());
        verify(batteryRepository, never()).save(any(Battery.class));
    }
}