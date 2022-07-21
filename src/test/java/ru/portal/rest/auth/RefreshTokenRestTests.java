package ru.portal.rest.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import ru.portal.entities.dto.response.auth.DtoAuthenticationResponse;
import ru.portal.entities.dto.response.auth.DtoFailedResponse;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RefreshTokenRestTests {

    private static final Pattern REGEX_TOKEN_WITH_BEARER = Pattern
            .compile("Bearer_([\\w_=]+)\\.([\\w_=]+)\\.([\\w_\\-\\+\\/=]*)");
    private static final Pattern REGEX_REFRESH_TOKEN = Pattern.compile("\\w{32}");

    private final ObjectMapper mapper;
    private final MockMvc mockMvc;

    @Autowired
    public RefreshTokenRestTests(ObjectMapper mapper, MockMvc mockMvc) {
        this.mapper = mapper;
        this.mockMvc = mockMvc;
    }
    
    @Sql(scripts = {"/sql/user_active_test.sql", "/sql/auth/refresh_token_repository_test.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/cleaning.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void testRefreshToken() throws Exception {

        var token = "cbbnb42fe1f4fgd697143da246a4132b";

        var response = mockMvc.perform(post("/refreshtoken")
                        .header("RefreshToken", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn().getResponse();

        var jsonToken = response.getContentAsString();
        var dtoToken = mapper.readValue(jsonToken, DtoAuthenticationResponse.class);

        assertTrue(dtoToken.getAuthorization().matches(REGEX_TOKEN_WITH_BEARER.toString()),
                "Токен авторизации должен соответствовать регулярному выражению");
        assertTrue(dtoToken.getRefreshToken().matches(REGEX_REFRESH_TOKEN.toString()),
                "Токен обновления должен соответствовать регулярному выражению");
    }

    @Sql(scripts = "/sql/user_active_test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/cleaning.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void testRefreshTokenNotExist() throws Exception {

        var token = "cbbnb42fe1f4fgd69123dgf3da246a4132b";

        var response = mockMvc.perform(post("/refreshtoken")
                        .header("RefreshToken", token))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn().getResponse();

        var jsonToken = response.getContentAsString();
        var dtoToken = mapper.readValue(jsonToken, DtoFailedResponse.class);

        assertEquals(401, dtoToken.getStatus(), "Статус должен быть 401");
        assertNotNull(dtoToken.getMessage(), "Сообщение должно существовать");
    }

    @Sql(scripts = {"/sql/user_active_test.sql", "/sql/auth/refresh_token_repository_not_valid_time_test.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/cleaning.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void testRefreshTokenTimeUp() throws Exception {

        var token = "cbbnb42fe1f4fgd697143da246a4132b";

        var response = mockMvc.perform(post("/refreshtoken")
                        .header("RefreshToken", token))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn().getResponse();

        var jsonToken = response.getContentAsString();
        var dtoToken = mapper.readValue(jsonToken, DtoFailedResponse.class);

        assertEquals(401, dtoToken.getStatus(), "Статус должен быть 401");
        assertNotNull(dtoToken.getMessage(), "Сообщение должно существовать");
    }
}
