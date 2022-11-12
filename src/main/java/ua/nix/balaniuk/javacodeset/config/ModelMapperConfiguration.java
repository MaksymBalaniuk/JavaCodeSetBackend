package ua.nix.balaniuk.javacodeset.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ua.nix.balaniuk.javacodeset.dto.CodeBlockDto;
import ua.nix.balaniuk.javacodeset.dto.CommentDto;
import ua.nix.balaniuk.javacodeset.dto.EstimateDto;
import ua.nix.balaniuk.javacodeset.entity.CodeBlockEntity;
import ua.nix.balaniuk.javacodeset.entity.CommentEntity;
import ua.nix.balaniuk.javacodeset.entity.EstimateEntity;

import static org.modelmapper.config.Configuration.AccessLevel.PRIVATE;
import static org.modelmapper.convention.MatchingStrategies.STRICT;

@Configuration
public class ModelMapperConfiguration {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration()
                .setMatchingStrategy(STRICT)
                .setFieldMatchingEnabled(true)
                .setSkipNullEnabled(true)
                .setFieldAccessLevel(PRIVATE);

        mapper.typeMap(CodeBlockEntity.class, CodeBlockDto.class)
                .addMapping(codeBlockEntity -> codeBlockEntity.getUser().getId(), CodeBlockDto::setUserId);
        mapper.typeMap(CommentEntity.class, CommentDto.class)
                .addMapping(commentEntity -> commentEntity.getUser().getId(), CommentDto::setUserId)
                .addMapping(commentEntity -> commentEntity.getCodeBlock().getId(), CommentDto::setCodeBlockId);
        mapper.typeMap(EstimateEntity.class, EstimateDto.class)
                .addMapping(estimateEntity -> estimateEntity.getUser().getId(), EstimateDto::setUserId)
                .addMapping(estimateEntity -> estimateEntity.getCodeBlock().getId(), EstimateDto::setCodeBlockId);

        return mapper;
    }
}
