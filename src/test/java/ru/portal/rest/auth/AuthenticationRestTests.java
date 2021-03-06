package ru.portal.rest.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import ru.portal.entities.dto.request.auth.DtoUserRequest;
import ru.portal.entities.dto.response.auth.DtoAuthenticationResponse;
import ru.portal.entities.dto.response.auth.DtoFailedResponse;
import ru.portal.entities.dto.response.auth.DtoSuccessRegResponse;

import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationRestTests {

    private static final Pattern REGEX_TOKEN_WITH_BEARER = Pattern
            .compile("Bearer_([\\w_=]+)\\.([\\w_=]+)\\.([\\w_\\-\\+\\/=]*)");
    private static final Pattern REGEX_REFRESH_TOKEN = Pattern.compile("\\w{32}");

    private final MockMvc mockMvc;
    private final ObjectMapper mapper;

    @Autowired
    AuthenticationRestTests(MockMvc mockMvc,
                            ObjectMapper mapper) {
        this.mockMvc = mockMvc;
        this.mapper = mapper;
    }

    @Sql(scripts = "/sql/cleaning.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void testRegistrationUserSuccess() throws Exception {

        var user = new DtoUserRequest("Sparus", "rf1991@smaisl.ru", "rootroot");
        var userJson = mapper.writeValueAsString(user);

        var response = mockMvc.perform(post("/registration")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content(userJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn().getResponse();

        var jsonResponse = response.getContentAsString(StandardCharsets.UTF_8);
        var dtoResponse = mapper.readValue(jsonResponse, DtoSuccessRegResponse.class);

        assertAll(
                () -> assertEquals(201, dtoResponse.getStatus(),
                        "???????????? ???????????? ???????? 201"),
                () -> assertNotNull(dtoResponse.getMessage(),
                        "?????????????????? ???????????? ????????????????????????????")
        );
    }

    @Sql(scripts = "/sql/user/user_await_test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/cleaning.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void testRegistrationUserFailsUserExists() throws Exception {

        var user = new DtoUserRequest("Sparus", "rf1991@mail.ru", "rootroot");
        var userJson = mapper.writeValueAsString(user);

        var response = mockMvc.perform(post("/registration")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content(userJson))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn().getResponse();

        var jsonResponse = response.getContentAsString(StandardCharsets.UTF_8);
        var dtoResponse = mapper.readValue(jsonResponse, DtoFailedResponse.class);

        assertAll(
                () -> assertEquals(409, dtoResponse.getStatus(),
                        "???????????? ???????????? ???????? 409"),
                () -> assertNotNull(dtoResponse.getMessage(),
                        "?????????????????? ???????????? ????????????????????????????")
        );
    }


    @Sql(scripts = {"/sql/user/user_active_test.sql", "/sql/auth/login_attempt_valid_test.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/cleaning.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void testLoginSuccess() throws Exception {

        var user = new DtoUserRequest("Sparus", "rf1991@mail.ru", "rootroot");
        var userJson = mapper.writeValueAsString(user);

        var response = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(userJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn().getResponse();

        var jsonResponse = response.getContentAsString(StandardCharsets.UTF_8);
        var dtoResponse = mapper.readValue(jsonResponse, DtoAuthenticationResponse.class);

        assertAll(
                () -> assertTrue(dtoResponse.getAuthorization().matches(REGEX_TOKEN_WITH_BEARER.toString()),
                        "?????????? ?????????????????????? ???????????? ?????????????????????????????? ?????????????????????? ??????????????????"),
                () -> assertTrue(dtoResponse.getRefreshToken().matches(REGEX_REFRESH_TOKEN.toString()),
                        "?????????? ???????????????????? ???????????? ?????????????????????????????? ?????????????????????? ??????????????????")
        );
    }

    @Sql(scripts = {"/sql/user/user_active_test.sql", "/sql/auth/login_attempt_valid_test.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/cleaning.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void testLoginFailCredentials() throws Exception {

        var user = new DtoUserRequest("Sparus", "rf1991@mail.ru", "fdgsdfgdfdf");
        var userJson = mapper.writeValueAsString(user);

        var response = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(userJson))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn().getResponse();

        var jsonResponse = response.getContentAsString(StandardCharsets.UTF_8);
        var dtoResponse = mapper.readValue(jsonResponse, DtoFailedResponse.class);

        assertAll(
                () -> assertEquals(409, dtoResponse.getStatus(),
                        "???????????? ???????????? ???????? 409"),
                () -> assertNotNull(dtoResponse.getMessage(),
                        "?????????????????? ???????????? ????????????????????????????")
        );
    }

    @Sql(scripts = {"/sql/user/user_block_test.sql", "/sql/auth/login_attempt_block_test.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/cleaning.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void testLoginFailBannedUser() throws Exception {

        var user = new DtoUserRequest("Sparus", "rf1991@mail.ru", "rootroot");
        var userJson = mapper.writeValueAsString(user);

        var response = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(userJson))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn().getResponse();

        var jsonResponse = response.getContentAsString(StandardCharsets.UTF_8);
        var dtoResponse = mapper.readValue(jsonResponse, DtoFailedResponse.class);

        assertAll(
                () -> assertEquals(409, dtoResponse.getStatus(),
                        "???????????? ???????????? ???????? 409"),
                () -> assertNotNull(dtoResponse.getMessage(),
                        "?????????????????? ???????????? ????????????????????????????")
        );
    }

    @Sql(scripts = "/sql/user/user_active_test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/cleaning.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void testCheckUsernameExist() throws Exception {
        var response = mockMvc.perform(get("/checkname")
                        .param("username", "Sparus"))
                .andExpect(status().isConflict())
                .andReturn().getResponse().getContentAsString();

        assertEquals("Username exist", response,
                "???????????????????????? ???????????? ????????????????????????");
    }

    @Test
    void testCheckUsernameNotExist() throws Exception {
        var response = mockMvc.perform(get("/checkname")
                        .param("username", "Sparus"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals("Username not exist", response,
                "???????????????????????? ???????????? ????????????????????????");
    }

    @Sql(scripts = "/sql/user/user_active_test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/cleaning.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void testCheckEmailExist() throws Exception {
        var response = mockMvc.perform(get("/checkemail")
                        .param("email", "rf1991@mail.ru"))
                .andExpect(status().isConflict())
                .andReturn().getResponse().getContentAsString();

        assertEquals("Email exist", response,
                "???????????????????????? ???????????? ????????????????????????");
    }


    @Test
    void testCheckEmailNotExist() throws Exception {
        var response = mockMvc.perform(get("/checkemail")
                        .param("email", "rf1991@mail.ru"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals("Email not exist", response,
                "???????????????????????? ???????????? ????????????????????????");
    }

    @Sql(scripts = {"/sql/user/user_await_test.sql", "/sql/auth/confirmation_token_valid_test.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/cleaning.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void testConfirmationUser() throws Exception {

        var token = "cb0eb42fe1fd4d6397143da246a4132b";

        var response = mockMvc.perform(get("/confirmation/{token}", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn().getResponse();

        var jsonResponse = response.getContentAsString();
        var dtoResponse = mapper.readValue(jsonResponse, DtoSuccessRegResponse.class);

        assertEquals(200, dtoResponse.getStatus(),
                "???????????? ???????????? ???????????? ???????? 200");
        assertNotNull(dtoResponse.getMessage(),
                "?????????????????? ???????????? ????????????????????????????");
    }

    @Sql(scripts = {"/sql/user/user_await_test.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/cleaning.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void testConfirmationTokenNotExist() throws Exception {

        var token = "cb0eb42fe1fd4d6397143da246a4132b";

        var response = mockMvc.perform(get("/confirmation/{token}", token))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn().getResponse();

        var jsonResponse = response.getContentAsString();
        var dtoResponse = mapper.readValue(jsonResponse, DtoFailedResponse.class);

        assertEquals(401, dtoResponse.getStatus(),
                "???????????? ???????????? ???????????? ???????? 401");
        assertNotNull(dtoResponse.getMessage(),
                "?????????????????? ???????????? ????????????????????????????");
    }

    @Sql(scripts = {"/sql/user/user_await_test.sql","/sql/auth/confirmation_token_not_valid_time_test.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/cleaning.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void testConfirmationTokenTimeExpired() throws Exception {

        var token = "cb0eb42fe1fd4d6397143da246a4132b";

        var response = mockMvc.perform(get("/confirmation/{token}", token))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn().getResponse();

        var jsonResponse = response.getContentAsString();
        var dtoResponse = mapper.readValue(jsonResponse, DtoFailedResponse.class);

        assertEquals(401, dtoResponse.getStatus(),
                "???????????? ???????????? ???????????? ???????? 401");
        assertNotNull(dtoResponse.getMessage(),
                "?????????????????? ???????????? ????????????????????????????");
    }

}
