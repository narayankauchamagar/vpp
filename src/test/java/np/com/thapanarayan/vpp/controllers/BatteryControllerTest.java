package np.com.thapanarayan.vpp.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import np.com.thapanarayan.vpp.dto.*;
import np.com.thapanarayan.vpp.entity.Battery;
import np.com.thapanarayan.vpp.mapper.BatteryMapper;
import np.com.thapanarayan.vpp.service.BatteryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BatteryController.class) // Focuses on testing the BatteryController
class BatteryControllerTest {

    @Autowired
    MockMvc mockMvc; // Used to perform HTTP requests

    @Autowired
    ObjectMapper objectMapper; // Used to convert objects to JSON strings

    @MockBean
    BatteryService batteryService; // Mock the BatteryService dependency

    @MockBean
    BatteryMapper batteryMapper; // Mock the BatteryMapper dependency

    // --- Tests for POST /api/batteries (battery method) ---


    @Test
    @DisplayName("Should save batteries successfully when no duplicates are present")
    void battery_saveSuccess_noDuplicates() throws Exception {
        // Given
        BatteryRequestDto batteryRequestDto1 = new BatteryRequestDto();
        batteryRequestDto1.setName("Battery 1");
        batteryRequestDto1.setPostcode("12345");
        batteryRequestDto1.setCapacity(100L);

        BatteryRequestDto batteryRequestDto2 = new BatteryRequestDto();
        batteryRequestDto2.setName("Battery 2");
        batteryRequestDto2.setPostcode("12346");
        batteryRequestDto2.setCapacity(200L);

        List<BatteryRequestDto> requestDtos = Arrays.asList(batteryRequestDto1, batteryRequestDto2);

        Battery battery1 = new Battery();
        battery1.setName("Battery 1");
        battery1.setPostcode("12345");
        battery1.setCapacity(100L);

        Battery battery2 = new Battery();
        battery2.setName("Battery 2");
        battery2.setPostcode("12346");
        battery2.setCapacity(200L);

        List<Battery> batteries = List.of(battery1,battery2  );

        List<BatteryResponseDto> responseDtos = Arrays.asList(
                BatteryResponseDto.builder().id(1L).name("Battery 1").postcode("12345").capacity(100L).build(),
                BatteryResponseDto.builder().id(2L).name("Battery 2").postcode("12346").capacity(200L).build()
        );

        BatteryServiceResponse serviceResponse = BatteryServiceResponse.builder()
                .batteries(List.of(battery1, battery2))
                .duplicatedBatteries(List.of()) // No duplicates
                .build();

        when(batteryMapper.convertToBatteries(Mockito.anyList())).thenReturn(List.of(battery1, battery2));
        when(batteryService.saveBatteries(Mockito.anyList())).thenReturn(serviceResponse);
        when(batteryMapper.convertToBatteryResponse(Mockito.anyList())).thenReturn(responseDtos);

        // When & Then
        mockMvc.perform(post("/api/batteries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDtos)))
                .andExpect(status().isOk()) // Expect 200 OK
                .andExpect(jsonPath("$.message").value("Batteries saved successfully"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].name").value("Battery 1"))
                .andExpect(jsonPath("$.data[1].name").value("Battery 2"))
                .andExpect(jsonPath("$.error").doesNotExist()); // No error object

       verify(batteryService, times(1)).saveBatteries(batteries);
       verify(batteryMapper, times(1)).convertToBatteries(requestDtos);
       verify(batteryMapper, times(1)).convertToBatteryResponse(batteries);
    }

    @Test
    @DisplayName("Should return bad request for invalid BatteryRequestDto (e.g., null name)")
    void battery_invalidInput_validationError() throws Exception {

        BatteryRequestDto batteryRequestDto = new BatteryRequestDto();
        batteryRequestDto.setPostcode("12345");
        batteryRequestDto.setCapacity(100L);

        // Given
        // Create a DTO with a missing required field (assuming @Valid applies to 'name' and it's not null)
        List<BatteryRequestDto> requestDtos = Collections.singletonList( batteryRequestDto );

        // When & Then
        mockMvc.perform(post("/api/batteries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDtos)))
                .andExpect(status().isBadRequest()) // Expect 400 Bad Request due to validation
                .andExpect(jsonPath("$.error").exists()); // Spring's default error handling for validation

        // Ensure service and mapper are not called if validation fails at controller layer
        verify(batteryMapper, never()).convertToBatteries(anyList());
        verify(batteryService, never()).saveBatteries(anyList());
    }



    @Test
    @DisplayName("Should get batteries successfully with statistics for valid postcode range")
    void getBatteries_success() throws Exception {
        // Given
        BatterySearchRequest searchRequest = new BatterySearchRequest();
        searchRequest.setMinPostcode("12340");
        searchRequest.setMaxPostcode("12345");
        searchRequest.setMinWattCapacity(100L);
        searchRequest.setMaxWattCapacity(300L);


        List<BatteryResponseDto> foundBatteries = Arrays.asList(
                BatteryResponseDto.builder().id(1L).name("Battery A").postcode("12345").capacity(100L).build(),
                BatteryResponseDto.builder().id(2L).name("Battery B").postcode("12346").capacity(200L).build()
        );
        BatteryStatisticsDto statistics = new BatteryStatisticsDto(300.0, 150.0, 2);

        BatterySearchResponse serviceResponse = BatterySearchResponse.builder()
                .batteries(foundBatteries)
                .statistics(statistics)
                .build();

        when(batteryService.getBatteriesByPostcodeRange(any())).thenReturn(serviceResponse);

        // When & Then
        mockMvc.perform(get("/api/batteries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchRequest)))
                .andExpect(status().isOk()) // Expect 200 OK
                .andExpect(jsonPath("$.message").value("Batteries fetched successfully"))
                .andExpect(jsonPath("$.data.batteries").isArray())
                .andExpect(jsonPath("$.data.batteries[0].name").value("Battery A"))
                .andExpect(jsonPath("$.data.statistics.totalWattCapacity").value(300.0))
                .andExpect(jsonPath("$.data.statistics.averageWattCapacity").value(150.0))
                .andExpect(jsonPath("$.data.statistics.numBatteries").value(2));

       verify(batteryService, times(1)).getBatteriesByPostcodeRange(searchRequest);
    }

    @Test
    @DisplayName("Should return bad request for invalid postcode range (min > max)")
    void getBatteries_invalidPostcodeRange() throws Exception {
        // Given
        BatterySearchRequest searchRequest = new BatterySearchRequest();
        searchRequest.setMinPostcode("12350");
        searchRequest.setMaxPostcode("12345");

        // When & Then
        mockMvc.perform(get("/api/batteries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchRequest)))
                .andExpect(status().isBadRequest()) // Expect 400 Bad Request
                .andExpect(jsonPath("$.message").value("Invalid Request"))
                .andExpect(jsonPath("$.error.message").value("Invalid postcode range: min_postcode cannot be greater than max_postcode."));

        // Ensure service is not called as validation happens in controller
        verify(batteryService, never()).getBatteriesByPostcodeRange(any());
    }

    @Test
    @DisplayName("Should return empty list and zero statistics when no batteries found")
    void getBatteries_noBatteriesFound() throws Exception {
        // Given

        BatterySearchRequest searchRequest = new BatterySearchRequest();
        searchRequest.setMinPostcode("99990");
        searchRequest.setMaxPostcode("99999");
        searchRequest.setMinWattCapacity(100L);
        searchRequest.setMaxWattCapacity(300L);

        BatterySearchResponse serviceResponse = BatterySearchResponse.builder()
                .batteries(List.of())
                .statistics(new BatteryStatisticsDto(0.0, 0.0, 0))
                .build();

        when(batteryService.getBatteriesByPostcodeRange(Mockito.any())).thenReturn(serviceResponse);

        // When & Then
        mockMvc.perform(get("/api/batteries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchRequest)))
                .andExpect(status().isOk()) // Still 200 OK, just no data
                .andExpect(jsonPath("$.message").value("Batteries fetched successfully"))
                .andExpect(jsonPath("$.data.batteries").isArray())
                .andExpect(jsonPath("$.data.batteries").isEmpty())
                .andExpect(jsonPath("$.data.statistics.totalWattCapacity").value(0.0))
                .andExpect(jsonPath("$.data.statistics.averageWattCapacity").value(0.0))
                .andExpect(jsonPath("$.data.statistics.numBatteries").value(0));

        verify(batteryService, times(1)).getBatteriesByPostcodeRange(searchRequest);
    }
}
