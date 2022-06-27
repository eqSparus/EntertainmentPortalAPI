package ru.portal.rest.auth;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.portal.entities.dto.Views;
import ru.portal.entities.dto.request.DtoUserRequest;
import ru.portal.entities.dto.response.DtoAuthenticationResponse;
import ru.portal.entities.dto.response.DtoSuccessAuthResponse;
import ru.portal.security.services.ConfirmationService;
import ru.portal.security.services.UserService;

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
    public DtoSuccessAuthResponse registrationUser(
            @RequestBody DtoUserRequest request
    ) {
        return userService.registrationUser(request);
    }

    @PostMapping(path = "/login", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @JsonView(Views.Login.class)
    public DtoAuthenticationResponse login(
            @RequestBody DtoUserRequest request
    ) {
        return userService.login(request);
    }

    @GetMapping(path = "/confirmation/{token}")
    public DtoSuccessAuthResponse confirmation(
            @PathVariable(name = "token") String token
    ) {
        return confirmationService.confirmation(token);
    }
}
