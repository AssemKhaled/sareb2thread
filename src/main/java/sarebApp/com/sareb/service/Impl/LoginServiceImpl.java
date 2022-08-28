package sarebApp.com.sareb.service.Impl;

import com.mongodb.BasicDBObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import sarebApp.com.sareb.config.security.JwtUtil;
import sarebApp.com.sareb.config.security.MyUserDetailsService;
import sarebApp.com.sareb.dto.ApiResponse;
import sarebApp.com.sareb.dto.ApiResponseBuilder;
import sarebApp.com.sareb.dto.requests.AuthRequest;
import sarebApp.com.sareb.dto.responses.LoginResponse;
import sarebApp.com.sareb.entities.MongoPositions;
import sarebApp.com.sareb.entities.User;
import sarebApp.com.sareb.exception.ApiGetException;
import sarebApp.com.sareb.repository.MongoPositionsRepository;
import sarebApp.com.sareb.repository.UserRepository;
import sarebApp.com.sareb.service.LoginService;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregationOptions;

/**
 * @author Assem
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final UserRepository userRepository;
    private final MyUserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtTokenUtil;
    private final MongoTemplate mongoTemplate;
    private final MongoPositionsRepository mongoPositionsRepository;


    @Override
    public ApiResponse<LoginResponse> login(String authorization) {
        log.info("************************ Login STARTED ***************************");
        ApiResponseBuilder<LoginResponse> builder = new ApiResponseBuilder<>();
        AuthRequest authRequest = basicAuthentication(authorization);
        UserDetails userDetails= userDetailsService.loadUserByUsername(authRequest.getEmail());
        String jwt=jwtTokenUtil.generateToken(userDetails);
        User user = userRepository.findByEmail(authRequest.getEmail());
//        try {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(authRequest.getEmail(), getMd5(authRequest.getPassword()));
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
//        }
//        catch (BadCredentialsException e){
//
//            builder.setStatusCode(400);
//            builder.setMessage("INVALID Credentials");
//            builder.setEntity(null);
//            builder.setSize(0);
//            return builder.build();
//
//        }
//        UserDetails userDetails= userDetailsService.loadUserByUsername(authRequest.getEmail());
//        String jwt=jwtTokenUtil.generateToken(userDetails);

        log.info("************************ Login Ended ***************************");

        builder.setStatusCode(200);
        builder.setSuccess(true);
        builder.setEntity(LoginResponse
                .builder()
                .id(user.getId())
                .name(user.getName())
                .email(authRequest.getEmail())
                .jwt(jwt)
                .build());
        return builder.build();
    }

    @Override
    public ApiResponse<?> logout() {
        return null;
    }

    public  String getMd5(String input) {
        try {

            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public AuthRequest basicAuthentication(String authorization){
        if(!Objects.equals(authorization, "") && authorization.toLowerCase().startsWith("basic")){
            String base64Credentials = authorization.substring("Basic".length()).trim();
            byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(credDecoded, StandardCharsets.UTF_8);

            final String[] values = credentials.split(":",2);
            String email = values[0];
            String password = values[1];
            return AuthRequest
                    .builder()
                    .email(email)
                    .password(password)
                    .build();
        }
        return null;
    }

    public ApiResponse<User> getUser(String email){
        User user = userRepository.findByEmail(email);
        ApiResponseBuilder<User> builder = new ApiResponseBuilder<>();
        builder.setSize(0);
        builder.setMessage("Success");
        builder.setStatusCode(200);
        builder.setEntity(user);
        return builder.build();
    }
    public ApiResponse<List<MongoPositions>> getTrips(Long deviceId, String from , String to){
        ApiResponseBuilder<List<MongoPositions>> builder = new ApiResponseBuilder<>();
        List<MongoPositions> result = new ArrayList<>();
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        SimpleDateFormat inputFormat1 = new SimpleDateFormat("yyyy-MM-dd");Date dateFrom;Date dateTo = null;
        inputFormat1.setLenient(false);inputFormat.setLenient(false);outputFormat.setLenient(false);

        try {
            dateFrom = inputFormat.parse(from);
            from = outputFormat.format(dateFrom);
            dateTo = inputFormat.parse(to);
            to = outputFormat.format(dateTo);
        } catch (ParseException e2) {
            try {
                dateFrom = inputFormat1.parse(from);
                from = outputFormat.format(dateFrom);
                dateTo = inputFormat1.parse(to);
                to = outputFormat.format(dateTo);
            } catch (ParseException e) {
                throw new ApiGetException("Start and End Dates should be in the following format YYYY-MM-DD or yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            }
        }
        Aggregation aggregation = newAggregation(
                match(Criteria.where("deviceid").in(deviceId)),
                match(Criteria.where("devicetime").gte(dateFrom)),
                match(Criteria.where("devicetime").lte(dateTo))
//      {$and:[{deviceid:131},{devicetime:{$gte:new Date('2022-08-15 11:50:07')}},{devicetime:{$lte:new Date('2022-08-16 11:50:07')}}]}
        ).withOptions(newAggregationOptions().allowDiskUse(true).build());

        log.info("STARTED????????????????/");
        AggregationResults<MongoPositions> groupResults
                = mongoTemplate.aggregate(aggregation,"tc_positions", MongoPositions.class);
        log.info("ENDED????????????????/");
        if(groupResults.getMappedResults().size() > 0) {

            result.addAll(groupResults.getMappedResults());
        }
//        List<MongoPositions> result = mongoPositionsRepository.findAllByDeviceidAndServertimeBetween(deviceId,dateFrom,dateTo);
//        log.info("STARTED??????");
//        result = mongoPositionsRepository.findTrips(deviceId,dateFrom,dateTo);
//        log.info("ENDED??????");
        builder.setSize(result.size());
        builder.setMessage("Success");
        builder.setStatusCode(200);
        builder.setEntity(result);
        return builder.build();
    }
}
