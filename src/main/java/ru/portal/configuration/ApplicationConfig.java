package ru.portal.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Configuration
public class ApplicationConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper(){
        var timeModule = new JavaTimeModule();
        return new ObjectMapper()
                .registerModule(timeModule);
    }

}
