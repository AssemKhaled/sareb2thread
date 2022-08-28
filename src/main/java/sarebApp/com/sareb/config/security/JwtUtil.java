package sarebApp.com.sareb.config.security;
//
//
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {
//    @Autowired
//    private LoggedOutJwtTokenCache loggedOutJwtTokenCache;
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    private String SECRET_KEY = "secret";
    @Value("${jwt.token.expiration.in.seconds}")
    private Long expiration;


    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
//    public ResponseCookie getCleanJwtCookie() {
//        ResponseCookie cookie = ResponseCookie.from("mohamed", null).path("/app").build();
//        return cookie;
//    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }
//public String generateToken(Authentication authentication) {
//
//    UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
//
//    Date now = new Date();
//    Date expiryDate = new Date(now.getTime() + 3600000);
//
//    return Jwts.builder()
//            .setSubject((userPrincipal.getUsername()))
//            .setIssuer("Fuinco")
//            .setId(Long.toString(userPrincipal.getId()))
//            .setIssuedAt(new Date())
//            .setExpiration(expiryDate)
//            .signWith(SignatureAlgorithm.HS512, "HelloWorld")
//            .compact();
//}
    private String createToken(Map<String, Object> claims, String subject) {

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();
    }
    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public Date calculateExpirationDate(Date createdDate) {
        return new Date(createdDate.getTime() + expiration * 1000);
    }
//    public boolean validateJwtToken(String authToken) {
//        try {
//            Jwts.parser().setSigningKey("HelloWorld").parseClaimsJws(authToken);
//            validateTokenIsNotForALoggedOutDevice(authToken);
//            return true;
//        } catch (MalformedJwtException e) {
//            logger.error("Invalid JWT token -> Message: {}", e);
//        } catch (ExpiredJwtException e) {
//            logger.error("Expired JWT token -> Message: {}", e);
//        } catch (UnsupportedJwtException e) {
//            logger.error("Unsupported JWT token -> Message: {}", e);
//        } catch (IllegalArgumentException e) {
//            logger.error("JWT claims string is empty -> Message: {}", e);
//        }
//        return false;
//    }

//    private void validateTokenIsNotForALoggedOutDevice(String authToken) {
//        OnUserLogoutSuccessEvent previouslyLoggedOutEvent = loggedOutJwtTokenCache.getLogoutEventForToken(authToken);
//        if (previouslyLoggedOutEvent != null) {
//            String userEmail = previouslyLoggedOutEvent.getUserEmail();
//            Date logoutEventDate = previouslyLoggedOutEvent.getEventTime();
//            String errorMessage = String.format("Token corresponds to an already logged out user [%s] at [%s]. Please login again", userEmail, logoutEventDate);
//            throw new InvalidTokenRequestException("JWT", authToken, errorMessage);
//        }
//    }
}
