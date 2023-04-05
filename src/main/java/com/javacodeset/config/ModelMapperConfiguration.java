package com.javacodeset.config;

import com.javacodeset.dto.*;
import com.javacodeset.entity.*;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;

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

        Converter<Instant, Long> converterInstantToLong =
                context -> context.getSource() == null ? null : context.getSource().toEpochMilli();

        mapper.typeMap(CodeBlockEntity.class, CodeBlockDto.class)
                .addMapping(codeBlockEntity -> codeBlockEntity.getUser().getId(), CodeBlockDto::setUserId)
                .addMappings(expression -> expression.using(converterInstantToLong)
                        .map(CodeBlockEntity::getCreated, CodeBlockDto::setCreated))
                .addMappings(expression -> expression.using(converterInstantToLong)
                        .map(CodeBlockEntity::getUpdated, CodeBlockDto::setUpdated));

        mapper.typeMap(CommentEntity.class, CommentDto.class)
                .addMapping(commentEntity -> commentEntity.getUser().getId(), CommentDto::setUserId)
                .addMapping(commentEntity -> commentEntity.getCodeBlock().getId(), CommentDto::setCodeBlockId)
                .addMappings(expression -> expression.using(converterInstantToLong)
                        .map(CommentEntity::getCreated, CommentDto::setCreated))
                .addMappings(expression -> expression.using(converterInstantToLong)
                        .map(CommentEntity::getUpdated, CommentDto::setUpdated));

        mapper.typeMap(EstimateEntity.class, EstimateDto.class)
                .addMapping(estimateEntity -> estimateEntity.getUser().getId(), EstimateDto::setUserId)
                .addMapping(estimateEntity -> estimateEntity.getCodeBlock().getId(), EstimateDto::setCodeBlockId);
        mapper.typeMap(ShareEntity.class, ShareDto.class)
                .addMapping(shareEntity -> shareEntity.getToUser().getId(), ShareDto::setToUserId)
                .addMapping(shareEntity -> shareEntity.getFromUser().getId(), ShareDto::setFromUserId)
                .addMapping(shareEntity -> shareEntity.getCodeBlock().getId(), ShareDto::setCodeBlockId);

        mapper.typeMap(UserEntity.class, UserDto.class)
                .addMappings(expression -> expression.using(converterInstantToLong)
                        .map(UserEntity::getCreated, UserDto::setCreated))
                .addMappings(expression -> expression.using(converterInstantToLong)
                        .map(UserEntity::getUpdated, UserDto::setUpdated));

        return mapper;
    }
}
