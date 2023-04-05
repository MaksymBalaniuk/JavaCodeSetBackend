package com.javacodeset.config;

import com.javacodeset.dto.*;
import com.javacodeset.entity.*;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
                .addMapping(codeBlockEntity -> codeBlockEntity.getUser().getId(), CodeBlockDto::setUserId)
                .addMapping(codeBlockEntity -> codeBlockEntity.getCreated().toEpochMilli(), CodeBlockDto::setCreated)
                .addMapping(codeBlockEntity -> codeBlockEntity.getUpdated().toEpochMilli(), CodeBlockDto::setUpdated);
        mapper.typeMap(CommentEntity.class, CommentDto.class)
                .addMapping(commentEntity -> commentEntity.getUser().getId(), CommentDto::setUserId)
                .addMapping(commentEntity -> commentEntity.getCodeBlock().getId(), CommentDto::setCodeBlockId)
                .addMapping(commentEntity -> commentEntity.getCreated().toEpochMilli(), CommentDto::setCreated)
                .addMapping(commentEntity -> commentEntity.getUpdated().toEpochMilli(), CommentDto::setUpdated);
        mapper.typeMap(EstimateEntity.class, EstimateDto.class)
                .addMapping(estimateEntity -> estimateEntity.getUser().getId(), EstimateDto::setUserId)
                .addMapping(estimateEntity -> estimateEntity.getCodeBlock().getId(), EstimateDto::setCodeBlockId);
        mapper.typeMap(ShareEntity.class, ShareDto.class)
                .addMapping(shareEntity -> shareEntity.getToUser().getId(), ShareDto::setToUserId)
                .addMapping(shareEntity -> shareEntity.getFromUser().getId(), ShareDto::setFromUserId)
                .addMapping(shareEntity -> shareEntity.getCodeBlock().getId(), ShareDto::setCodeBlockId);
        mapper.typeMap(UserEntity.class, UserDto.class)
                .addMapping(userEntity -> userEntity.getCreated().toEpochMilli(), UserDto::setCreated)
                .addMapping(userEntity -> userEntity.getUpdated().toEpochMilli(), UserDto::setUpdated);

        return mapper;
    }
}
