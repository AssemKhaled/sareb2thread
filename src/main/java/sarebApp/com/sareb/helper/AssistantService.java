package sarebApp.com.sareb.helper;

import org.bson.types.ObjectId;

import java.util.List;

/**
 * @author Assem
 */
public interface AssistantService {

//   List<Long> getChildrenOfUser(Long userId);
   String getVehicleStatus(String lastUpdate);
   String getSarebVehicleStatus(String lastUpdate, ObjectId postionId , String json , Double speed);
   String driverStatus(String tripId);
   List<Long> getUserChildrens(Long userId);
}
