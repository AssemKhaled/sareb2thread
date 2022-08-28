package sarebApp.com.sareb.repository;

import com.mongodb.BasicDBObject;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Repository
public class MongoPositionsRepo {
    private final MongoTemplate mongoTemplate;

    public MongoPositionsRepo(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public Integer getCountFromAttrbuitesChart(List<String> positionIds, String attr, Boolean value){

        Integer size = 0;


        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        String currentDate=formatter.format(date);

        String from = currentDate +" 00:00:01";
        String to = currentDate +" 23:59:59";

        Date dateFrom = null;
        Date dateTo = null;
        try {
            dateFrom = output.parse(from);
            dateTo = output.parse(to);

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }



        List<ObjectId> ids = new ArrayList<ObjectId>();

        for(String id:positionIds) {
            if(id != null) {
                ids.add(new ObjectId(id));
            }
        }

        Aggregation aggregation = newAggregation(
                match(Criteria.where("_id").in(ids).and("devicetime").gte(dateFrom).lte(dateTo).and("attributes."+attr).in(value)),
                count().as("size")

        ).withOptions(newAggregationOptions().allowDiskUse(true).build());


        AggregationResults<BasicDBObject> groupResults
                = mongoTemplate.aggregate(aggregation,"tc_positions", BasicDBObject.class);

        if(groupResults.getMappedResults().size() > 0) {

            Iterator<BasicDBObject> iterator = groupResults.getMappedResults().iterator();
            while (iterator.hasNext()) {
                BasicDBObject object = (BasicDBObject) iterator.next();
                size = object.getInt("size");
            }

        }
        return size;
    }

    public List<Map> getCharts(List<String> positionIds){

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        String currentDate=formatter.format(date);

        String from = currentDate +" 00:00:01";
        String to = currentDate +" 23:59:59";

        Date dateFrom = null;
        Date dateTo = null;
        try {
            dateFrom = output.parse(from);
            dateTo = output.parse(to);

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Calendar calendarFrom = Calendar.getInstance();
        calendarFrom.setTime(dateFrom);
        calendarFrom.add(Calendar.HOUR_OF_DAY, 3);
        dateFrom = calendarFrom.getTime();

        Calendar calendarTo = Calendar.getInstance();
        calendarTo.setTime(dateTo);
        calendarTo.add(Calendar.HOUR_OF_DAY, 3);
        dateTo = calendarTo.getTime();

        List<Map> positions = new ArrayList<Map>();

        List<ObjectId> ids = new ArrayList<ObjectId>();

        for(String id:positionIds) {
            if(id != null) {
                ids.add(new ObjectId(id));
            }
        }

        Aggregation aggregation = newAggregation(
                match(Criteria.where("_id").in(ids).and("devicetime").gte(dateFrom).lte(dateTo)),
                project("deviceid","attributes","deviceName","driverName","attributes.todayHours","attributes.todayHoursString").and("devicetime").dateAsFormattedString("%Y-%m-%dT%H:%M:%S.%LZ").as("devicetime"),
                sort(Sort.Direction.DESC, "devicetime"),
                sort(Sort.Direction.DESC, "attributes.todayHours"),
                limit(10)


        ).withOptions(newAggregationOptions().allowDiskUse(true).build());

        AggregationResults<BasicDBObject> groupResults
                = mongoTemplate.aggregate(aggregation,"tc_positions", BasicDBObject.class);

        if(groupResults.getMappedResults().size() > 0) {

            Iterator<BasicDBObject> iterator = groupResults.getMappedResults().iterator();
            while (iterator.hasNext()) {
                BasicDBObject object = (BasicDBObject) iterator.next();
                Map position = new HashMap();


                position.put("hours","0");

                if(object.containsField("todayHours")) {

                    long min = TimeUnit.MILLISECONDS.toMinutes((long) object.getDouble("todayHours"));

                    Double hours = (double) min;
                    double roundOffDistance = Math.round(hours * 100.0) / 100.0;
                    position.put("hours",hours/60);

                }
                if(object.containsField("deviceName") && object.get("deviceName") != null) {

                    position.put("deviceName",object.get("deviceName").toString());

                }
                if(object.containsField("driverName") && object.get("driverName") != null) {

                    position.put("driverName",object.get("driverName").toString());

                }

                positions.add(position);

            }
        }

        return positions;
    }


}
