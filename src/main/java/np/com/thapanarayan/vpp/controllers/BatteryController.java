package np.com.thapanarayan.vpp.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import np.com.thapanarayan.vpp.dto.*;
import np.com.thapanarayan.vpp.entity.Battery;
import np.com.thapanarayan.vpp.mapper.BatteryMapper;
import np.com.thapanarayan.vpp.service.BatteryService;
import np.com.thapanarayan.vpp.swagger.SchemaConstants;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/batteries")
@RequiredArgsConstructor
public class BatteryController {


    private final BatteryService batteryService;
    private final BatteryMapper batteryMapper;

    @PostMapping
    @Operation
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Batteries saved successfully",
                    content = @Content(schema = @Schema(example = SchemaConstants.BATTERY_RESPONSE_DTO)))
    })
    public ResponseEntity<ServerResponse<?, ?>> battery(@RequestBody @Valid List<BatteryRequestDto> batteryRequestDtos) {

        List<Battery> batteries = batteryMapper.convertToBatteries(batteryRequestDtos);
        BatteryServiceResponse batteryServiceResponse = batteryService.saveBatteries(batteries);
        if (!batteryServiceResponse.getDuplicatedBatteries().isEmpty()) {
            ErrorResponse<List<BatteryResponseDto>> errorResponse = ErrorResponse.<List<BatteryResponseDto>>builder()
                    .message("Duplicate Batteries Found")
                    .metadata(batteryMapper.convertToBatteryResponse( batteryServiceResponse.getDuplicatedBatteries() ))
                    .build();

            List<BatteryResponseDto> savedBatteries = batteryMapper.convertToBatteryResponse( batteryServiceResponse.getBatteries());

            return ResponseEntity
                    .badRequest()
                    .body(ServerResponse.<List<BatteryResponseDto>, ErrorResponse<List<BatteryResponseDto>>>builder()
                    .message("Batteries saved successfully")
                    .data(savedBatteries)
                    .error(errorResponse)
                    .build());
        } else {
            List<BatteryResponseDto> savedBatteries = batteryMapper.convertToBatteryResponse( batteryServiceResponse.getBatteries());

            return ResponseEntity.ok(ServerResponse
                    .<List<BatteryResponseDto>, String>builder()
                    .message("Batteries saved successfully")
                    .data(savedBatteries)
                    .build());
        }
    }


    @GetMapping()
    @Operation
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Batteries Fetched successfully",
                    content = @Content(schema = @Schema(example = SchemaConstants.BATTERY_SEARCH_LIST))),
            @ApiResponse(responseCode = "400", description = "Batteries Fetched successfully",
                    content = @Content(schema = @Schema(example = SchemaConstants.BATTERY_SEARCH_RESPONSE_BAD_REQUEST)))
    })
    public ResponseEntity<?> getBatteries(@RequestBody @Valid BatterySearchRequest batterySearchRequest) {

        if (Long.parseLong(batterySearchRequest.getMinPostcode()) > Long.parseLong(batterySearchRequest.getMaxPostcode())) {
            ErrorResponse<?> errorResponse = ErrorResponse.builder()
                    .message("Invalid postcode range: min_postcode cannot be greater than max_postcode.")
                    .build();

            return ResponseEntity
                    .badRequest()
                    .body(ServerResponse.builder().message("Invalid Request").error(errorResponse).build());
        }
        BatterySearchResponse response = batteryService.getBatteriesByPostcodeRange(batterySearchRequest);
        return ResponseEntity
                .ok(ServerResponse.builder().message("Batteries fetched successfully").data(response).build());
    }
}
