package sarebApp.com.sareb.helper.Impl;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.types.ObjectId;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import sarebApp.com.sareb.entities.User;
import sarebApp.com.sareb.helper.AssistantService;
import sarebApp.com.sareb.repository.DeviceRepository;
import sarebApp.com.sareb.repository.UserRepository;
import sarebApp.com.sareb.service.Impl.LoginServiceImpl;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Assem
 */
@Service
public class AssistantServiceImpl implements AssistantService {

    private static final Log logger = LogFactory.getLog(AssistantServiceImpl.class);
    private final UserRepository userRepository;
    private final DeviceRepository deviceRepository;
    private final LoginServiceImpl loginService;

    public AssistantServiceImpl(UserRepository userRepository, DeviceRepository deviceRepository, LoginServiceImpl loginService) {
        this.userRepository = userRepository;
        this.deviceRepository = deviceRepository;
        this.loginService = loginService;
    }

    @Override
    public String getVehicleStatus(String  lastUpdate) {

        String status = null;
        long minutes = 0;
        if (lastUpdate != null) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date now = new Date();
            String strDate = formatter.format(now);
            try {
                Date dateLast = formatter.parse(lastUpdate);
                Date dateNow = formatter.parse(strDate);

                minutes = getDateDiffByMin (dateLast, dateNow, TimeUnit.MINUTES);

            } catch (ParseException e) {
                e.printStackTrace();
            }
            status = "offline";
            if(minutes < 3) {
                status = "online";
            }
            if(minutes > 8) {
                status = "offline";
            }
            if(minutes < 8 && minutes > 3) {
                status = "unknown";
            }
        }else {
            status = "offline";
        }

        return status;

    }

    @Override
    public String getSarebVehicleStatus(String lastUpdate , ObjectId positionId , String json , Double speed) {
        String status = null;
        if (lastUpdate == null)
            return "No data";
        long minutes = 0;
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date now = new Date();
            String strDate = formatter.format(now);
            try {
                Date dateLast = formatter.parse(lastUpdate);
                Date dateNow = formatter.parse(strDate);

                minutes = getDateDiffByMin (dateLast, dateNow, TimeUnit.MINUTES);

            } catch (ParseException e) {
                e.printStackTrace();
            }
        if (positionId != null){
            JSONObject obj = new JSONObject(json);
            if(minutes > 8) {
               status = "In active";
            }
            else {
                if(obj.has("ignition")) {
                    if(obj.get("ignition").equals(true)) {
                        if(speed == 0) {
                            status = "Idle";
                        }
                        if(speed > 0) {
                            status = "Running";
                        }
                    }
                    if(obj.get("ignition").equals(false)) {
                        status = "Stopped";
                    }
                }
            }
        }else {
            status = "No data";
        }
        return status;
    }

    @Override
    public String driverStatus(String tripId) {
        if(Objects.nonNull(tripId)){
            return "onTrip";
        }else {
            return "idle";
        }
    }

    @Override
    public List<Long> getUserChildrens(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        List<Long> userIds =new ArrayList<>();
        userIds.add(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getAccountType() == 2){
                //get all clients of vendor
                Optional<List<User>> optionalUserList = userRepository.findByVendorIdAndDeleteDate(userId,null);
                if (optionalUserList.isPresent()) {
                    List<User> userList = optionalUserList.get();
                    userIds = userList.stream()
                            .map(User::getId)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());
                    userIds.add(userId);
                }
            }else if (user.getAccountType() == 3){
                //get all users of client
                Optional<List<User>> optionalUserList = userRepository.findAllByClientIdAndDeleteDate(userId,null);
                if (optionalUserList.isPresent()) {
                    List<User> userList = optionalUserList.get();
                    userIds = userList.stream()
                            .map(User::getId)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());
                    userIds.add(userId);
                }
            }
            return userIds;
        }
        return userIds;
    }

    public static long getDateDiffByMin(Date date1, Date date2, TimeUnit timeUnit) {
        return timeUnit.convert(date2.getTime() - date1.getTime(), TimeUnit.MILLISECONDS);
    }
    public static long getDateDiffByDays(Date date1, Date date2, TimeUnit timeUnit) {
        return timeUnit.convert(date2.getTime() - date1.getTime(), TimeUnit.DAYS);
    }

}


