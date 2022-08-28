package sarebApp.com.sareb.helper.Impl;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mongodb.BasicDBObject;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;
import sarebApp.com.sareb.dto.responses.MongoMapperResponse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregationOptions;

@Repository
@RequiredArgsConstructor
public class Utilities {

    private final MongoTemplate mongoTemplate;

    public double speedConverter(Double speed){
        double convertedSpeed = Math.abs(speed * (1.852));
        return Math.round(convertedSpeed*100.0)/100.0;
    }

    public String monitoringTimeZoneConverter(Date date, String timeOffset){
        if(timeOffset.contains("%2B")){
            timeOffset = "+" + timeOffset.substring(3);
        }
        if(date != null){
            ZoneOffset zo = ZoneOffset.of(timeOffset);
            OffsetDateTime odt = OffsetDateTime.ofInstant(date.toInstant(), zo);
          return String.valueOf(odt).substring(0,19).replace("T", " ");
        }
        return null;
    }

    public String timeZoneConverter(Date date, String timeOffset){
//        if(timeOffset.contains("%2B")){
//            timeOffset = "+" + timeOffset.substring(3);
//        }
//        if(date != null){
//            ZoneOffset zo = ZoneOffset.of(timeOffset);
//            OffsetDateTime odt = OffsetDateTime.ofInstant(date.toInstant(), zo);
//            return String.valueOf(odt).substring(0,19).replace("T", " ");
//        }
//        return null;
        SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy, HH:mm:ss aa");
        if(timeOffset.contains("%2B")){
            timeOffset = "+" + timeOffset.substring(3);
        }
        if(date != null){
            ZoneOffset zo = ZoneOffset.of(timeOffset);
            OffsetDateTime odt = OffsetDateTime.ofInstant(date.toInstant(), zo);
            return String.valueOf(odt).substring(0,19).replace("T", " ");
//            long epochMilli = odt.toInstant().toEpochMilli();
//            Date dateObject = new Date(epochMilli);
//            return outputFormat.format(dateObject);
        }
        return null;
    }

    public Double humidityCalculations(Map<String,Object> obj){

        int countHum = 0;
        double Hum = 0.0;
        double avgHum = 0.0;

        if(obj.containsKey("hum1")) {
            if(Double.parseDouble(obj.get("hum1").toString()) != 0) {
                Hum = Hum + Double.parseDouble(obj.get("hum1").toString());
                countHum = countHum + 1;
            }
        }
        if(obj.containsKey("hum2")) {
            if(Double.parseDouble(obj.get("hum2").toString()) != 0) {
                Hum = Hum + Double.parseDouble(obj.get("hum2").toString());
                countHum = countHum + 1;
            }
        }
        if(obj.containsKey("hum3")) {
            if(Double.parseDouble(obj.get("hum3").toString()) != 0) {
                Hum = Hum + Double.parseDouble(obj.get("hum3").toString());
                countHum = countHum + 1;
            }
        }
        if(obj.containsKey("hum4")) {
            if(Double.parseDouble(obj.get("hum4").toString()) != 0) {
                Hum = Hum + Double.parseDouble(obj.get("hum4").toString());
                countHum = countHum + 1;
            }
        }
        if(obj.containsKey("wirehum1")) {
            if(Double.parseDouble(obj.get("wirehum1").toString()) != 0) {
                Hum = Hum + Double.parseDouble(obj.get("wirehum1").toString());
                countHum = countHum + 1;
            }
        }
        if(obj.containsKey("wirehum2")) {
            if(Double.parseDouble(obj.get("wirehum2").toString()) != 0) {
                Hum = Hum + Double.parseDouble(obj.get("wirehum2").toString());
                countHum = countHum + 1;
            }
        }
        if(obj.containsKey("wirehum3")) {
            if(Double.parseDouble(obj.get("wirehum3").toString()) != 0) {
                Hum = Hum + Double.parseDouble(obj.get("wirehum3").toString());
                countHum = countHum + 1;
            }
        }
        if(obj.containsKey("wirehum4")) {
            if(Double.parseDouble(obj.get("wirehum4").toString()) != 0) {
                Hum = Hum + Double.parseDouble(obj.get("wirehum4").toString());
                countHum = countHum + 1;
            }
        }
        if(countHum != 0) {
            avgHum = Hum / countHum;
        }
        return Math.round(avgHum * 100.0) / 100.0;
    }

    public Double temperatureCalculations(Map<String,Object> obj){
        int countTemp = 0;
        double Temp =  0.0;
        double avgTemp = 0.0;

        if(obj.containsKey("temp1")) {
            if(Double.parseDouble(obj.get("temp1").toString()) != 0) {
                Temp = Temp + Double.parseDouble(obj.get("temp1").toString());
                countTemp = countTemp + 1;
            }
        }
        if(obj.containsKey("temp2")) {
            if(Double.parseDouble(obj.get("temp2").toString()) != 0) {
                Temp = Temp + Double.parseDouble(obj.get("temp2").toString());
                countTemp = countTemp + 1;
            }
        }
        if(obj.containsKey("temp3")) {
            if(Double.parseDouble(obj.get("temp3").toString()) != 0) {
                Temp = Temp + Double.parseDouble(obj.get("temp3").toString());
                countTemp = countTemp + 1;
            }
        }
        if(obj.containsKey("temp4")) {
            if(Double.parseDouble(obj.get("temp4").toString()) != 0) {
                Temp = Temp + Double.parseDouble(obj.get("temp4").toString());
                countTemp = countTemp + 1;
            }
        }
        if(obj.containsKey("wiretemp1")) {
            if(Double.parseDouble(obj.get("wiretemp1").toString()) != 0) {
                Temp = Temp + Double.parseDouble(obj.get("wiretemp1").toString());
                countTemp = countTemp + 1;
            }
        }
        if(obj.containsKey("wiretemp2")) {
            if(Double.parseDouble(obj.get("wiretemp2").toString()) != 0) {
                Temp = Temp + Double.parseDouble(obj.get("wiretemp2").toString());
                countTemp = countTemp + 1;
            }
        }
        if(obj.containsKey("wiretemp3")) {
            if(Double.parseDouble(obj.get("wiretemp3").toString()) != 0) {
                Temp = Temp + Double.parseDouble(obj.get("wiretemp3").toString());
                countTemp = countTemp + 1;
            }
        }
        if(obj.containsKey("wiretemp4")) {
            if(Double.parseDouble(obj.get("wiretemp4").toString()) != 0) {
                Temp = Temp + Double.parseDouble(obj.get("wiretemp4").toString());
                countTemp = countTemp + 1;
            }
        }
        if(countTemp != 0) {
            avgTemp = Temp / countTemp;
        }

        return Math.round(avgTemp * 100.0) / 100.0;
    }

    public String durationCalculation(String duration){
        long timeDuration = 0;
        String totalDuration = "00:00:00";

        timeDuration = Math.abs(Long.parseLong(duration));

        Long hoursDuration =   TimeUnit.MILLISECONDS.toHours(timeDuration) ;
        Long minutesDuration = TimeUnit.MILLISECONDS.toMinutes(timeDuration) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeDuration));
        Long secondsDuration = TimeUnit.MILLISECONDS.toSeconds(timeDuration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeDuration));

        totalDuration = String.valueOf(hoursDuration)+":"+String.valueOf(minutesDuration)+":"+String.valueOf(secondsDuration);
        return totalDuration;
    }

    public MongoMapperResponse MongoObjJsonMapper(Object attributes ,Double speed
            , Long deviceId , String lastUpdate ){

        MongoMapperResponse result = new MongoMapperResponse();
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
//       mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, true);
        String json = null;

        try {
                json = mapper.writeValueAsString(attributes);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            JSONObject obj = new JSONObject(json);

//            if(obj.has("ignition")) {
//                if(obj.getBoolean("ignition")==true) {
//                    if(speed > 0) {
//                        ArrayList<Map<Object,Object>> lastPoints;
//
//                        lastPoints = getLastPoints(deviceId,lastUpdate);
//                        result.setLastPoints(lastPoints);
//
//                    }
//                }
//            }

            if(obj.has("power")) {
                if(obj.get("power") != null) {
                    if(obj.get("power") != "") {
                        double p = Double.valueOf(obj.get("power").toString());
                        double round = Math.round(p * 100.0 )/ 100.0;
                        obj.put("power",String.valueOf(round));
                    }
                    else {
                        obj.put("power", "0");
                    }
                }
                else {
                    obj.put("power", "0");
                }
            }

            if(obj.has("battery")) {
                if(obj.get("battery") != null) {
                    if(obj.get("battery") != "") {
                        double p = Double.parseDouble(obj.get("battery").toString());
                        double round = Math.round(p * 100.0 )/ 100.0;
                        obj.put("battery",String.valueOf(round));
                    }
                    else {
                        obj.put("battery", "0");
                    }
                }
                else {
                    obj.put("battery", "0");
                }
            }
            result.setAttributes(attributes);

        return result;
    }
    public ArrayList<Map<Object,Object>> getLastPoints(Long deviceId , String lastUpdate)  {
        ArrayList<Map<Object,Object>> lastPoints = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date date = new Date();
        Date minusDate = new Date();
        if (lastUpdate!=null){
            try {
                TimeZone etTimeZone = TimeZone.getTimeZone("UTC");
                dateFormat.setTimeZone(etTimeZone);
                date = dateFormat.parse(lastUpdate);

            }catch (ParseException e){

            }

            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.HOUR, -5);
            minusDate = cal.getTime();
        }


        Aggregation aggregation = newAggregation(
                match(Criteria.where("deviceid").in(deviceId)),
                match(Criteria.where("devicetime").gte(minusDate)),
                match(Criteria.where("devicetime").lte(date)),
                project("deviceid","devicetime","latitude","longitude"),
                sort(Sort.Direction.DESC, "devicetime"),
                limit(5)

        ).withOptions(newAggregationOptions().allowDiskUse(true).build());


        AggregationResults<BasicDBObject> groupResults
                = mongoTemplate.aggregate(aggregation,"tc_positions", BasicDBObject.class);

        if(groupResults.getMappedResults().size() > 0) {

            Iterator<BasicDBObject> iterator = groupResults.getMappedResults().iterator();
            while (iterator.hasNext()) {
                BasicDBObject object = (BasicDBObject) iterator.next();

                Map<Object,Object> points = new HashMap<Object, Object>();
                points.put("lat", object.getDouble("latitude"));
                points.put("long", object.getDouble("longitude"));

                lastPoints.add(points);
            }
        }

        return lastPoints;
    }

}
