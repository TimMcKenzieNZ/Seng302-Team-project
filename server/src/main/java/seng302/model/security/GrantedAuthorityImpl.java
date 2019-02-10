package seng302.model.security;


import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.security.core.GrantedAuthority;
import java.util.ArrayList;
import java.util.List;

/**
 * An enum for granted authority used by AuthenticationToken.
 */
public enum GrantedAuthorityImpl implements GrantedAuthority {
    @JsonProperty("admin")
    ADMIN,
    @JsonProperty("clinician")
    CLINICIAN,
    @JsonProperty("donor")
    DONOR;


    @Override
    public String getAuthority() {
        return name();
    }


    /**
     * AuthenticationToken requires a collection of GrantedAuthority. This can be used as valueOf() is
     * but returns the single GrantedAuthorityImpl in a List.
     * @param name the name of the GrantedAuthorityImpl
     * @return List containing the corresponding GrantedAuthorityImpl
     */
    public static List<GrantedAuthorityImpl> valueOfList(String name){
        List<GrantedAuthorityImpl> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(GrantedAuthorityImpl.valueOf(name.toUpperCase()));
        return grantedAuthorities;
    }
}
