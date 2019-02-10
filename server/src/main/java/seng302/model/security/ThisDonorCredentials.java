package seng302.model.security;

import org.springframework.security.access.prepost.PreAuthorize;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Use this to annotate donor/receiver, clincian and administrator endpoints. Any admin or clinician will be authorised.
 * But only the donor that is authentication.name matches the nhi in the request will be authorized.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAuthority('CLINICIAN') or hasAuthority('ADMIN') or (#nhi == authentication.name)")
public @interface ThisDonorCredentials {
}

