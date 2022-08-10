package sarebApp.com.sareb.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import sarebApp.com.sareb.entities.User;
import sarebApp.com.sareb.repository.UserRepository;
import sarebApp.com.sareb.service.LoginService;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Objects;

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


    @Override
    public ApiResponse<LoginResponse> login(String authorization) {
        log.info("************************ Login STARTED ***************************");
        ApiResponseBuilder<LoginResponse> builder = new ApiResponseBuilder<>();
        AuthRequest authRequest = basicAuthentication(authorization);
        UserDetails userDetails= userDetailsService.loadUserByUsername(authRequest.getEmail());
        String jwt=jwtTokenUtil.generateToken(userDetails);

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
}
