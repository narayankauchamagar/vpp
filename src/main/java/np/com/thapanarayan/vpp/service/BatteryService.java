package np.com.thapanarayan.vpp.service;

import np.com.thapanarayan.vpp.dto.BatterySearchResponse;
import np.com.thapanarayan.vpp.dto.BatteryServiceResponse;
import np.com.thapanarayan.vpp.entity.Battery;

import java.util.List;

public interface BatteryService {
    BatteryServiceResponse saveBatteries(List<Battery> batteries);

    BatterySearchResponse getBatteriesByPostcodeRange(Integer startPostcode, Integer endPostcode);
}
