package np.com.thapanarayan.vpp.service;

import np.com.thapanarayan.vpp.dto.BatteryServiceResponse;
import np.com.thapanarayan.vpp.entity.Battery;

import java.util.List;

public interface BatteryService {
    BatteryServiceResponse saveBatteries(List<Battery> batteries);

    BatteryServiceResponse getBatteriesByPostcodeRange(Integer startPostcode, Integer endPostcode);
}
