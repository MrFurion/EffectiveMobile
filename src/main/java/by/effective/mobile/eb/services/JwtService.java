package by.effective.mobile.eb.services;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;
import java.util.function.Function;

/**
 * Service interface for managing cards. Provides methods for card operations.
 */
public interface JwtService {
    /**
     * Extracts the username from a given JWT token.
     *
     * @param token the JWT token
     * @return the username extracted from the token
     */
    String extractEmail(String token);

    /**
     * Extracts a specific claim from a given JWT token using the provided claims resolver function.
     *
     * @param <T> the type of the claim to be extracted
     * @param token the JWT token
     * @param claimsResolver the function to resolve the claim from the token's claims
     * @return the extracted claim
     */
    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);

    /**
     * Generates a JWT token for the given user details.
     *
     * @param userDetails the user details for which the token is to be generated
     * @return the generated JWT token
     */
    String generateToken(UserDetails userDetails);

    /**
     * Generates a JWT token with the provided extra claims for the given user details.
     *
     * @param extraClaims additional claims to be included in the token
     * @param userDetails the user details for which the token is to be generated
     * @return the generated JWT token
     */
    String generateToken(Map<String, Object> extraClaims, UserDetails userDetails);

    /**
     * Retrieves the expiration time of the JWT token.
     *
     * @return the expiration time of the JWT token in milliseconds
     */
    long getExpirationTime();

    /**
     * Checks if the provided JWT token is valid for the given user details.
     *
     * @param token       the JWT token to validate
     * @param userDetails the user details against which the token is validated
     * @return true if the token is valid for the provided user details, false otherwise
     */
    boolean isTokenValid(String token, UserDetails userDetails);
}
