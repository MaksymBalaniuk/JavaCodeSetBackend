package ua.nix.balaniuk.javacodeset.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ua.nix.balaniuk.javacodeset.util.JsonInstantSerialize;

import java.time.Instant;

@Configuration
public class ObjectMapperConfiguration {

    @Bean
    public ObjectMapper objectMapper() {
        return JsonMapper.builder()
                .addModule(new JavaTimeModule()
                        .addSerializer(Instant.class, new JsonInstantSerialize(Instant.class)))
                .build();
    }
}
