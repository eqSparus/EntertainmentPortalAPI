package ru.portal.rest.auth;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.portal.entities.dto.Views;
import ru.portal.entities.dto.request.auth.DtoUserRequest;
import ru.portal.entities.dto.response.auth.DtoAuthenticationResponse;
import ru.portal.entities.dto.response.auth.DtoSuccessRegResponse;
import ru.portal.security.services.ConfirmationService;
import ru.portal.security.services.UserService;

/**
 * Конечные точки для регистрации и аутентификации пользователя.
 *
 * @author Федорышин К.В.
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
public class AuthenticationRest {

    UserService userService;
    ConfirmationService confirmationService;

    @Autowired
    public AuthenticationRest(UserService userService,
                              ConfirmationService confirmationService) {
        this.userService = userService;
        this.confirmationService = confirmationService;
    }

    @PostMapping(path = "/registration", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(code = HttpStatus.CREATED)
    public DtoSuccessRegResponse registrationUser(
            @Valid @RequestBody DtoUserRequest request
    ) {
        return userService.registrationUser(request);
    }

    @PostMapping(path = "/login", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @JsonView(Views.Login.class)
    public DtoAuthenticationResponse login(
            @Valid @RequestBody DtoUserRequest request
    ) {
        return userService.login(request);
    }

    @GetMapping(path = "/confirmation/{token}")
    public DtoSuccessRegResponse confirmation(
            @PathVariable(name = "token") String token
    ) {
        return confirmationService.confirmation(token);
    }

    @GetMapping(path = "/checkname", params = "username")
    public ResponseEntity<String> checkUsername(
            @RequestParam(name = "username") String username
    ) {
        if (userService.checkUsername(username)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username exist");
        }
        return ResponseEntity.ok("Username not exist");
    }

    @GetMapping(path = "/checkemail", params = "email")
    public ResponseEntity<String> checkEmail(
            @RequestParam(name = "email") String email
    ) {
        if (userService.checkEmail(email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email exist");
        }
        return ResponseEntity.ok("Email not exist");
    }
}
