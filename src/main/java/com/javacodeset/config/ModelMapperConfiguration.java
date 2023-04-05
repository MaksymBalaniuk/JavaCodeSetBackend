package com.javacodeset.config;

import com.javacodeset.dto.*;
import com.javacodeset.entity.*;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.util.Objects;

import static org.modelmapper.config.Configuration.AccessLevel.PRIVATE;
import static org.modelmapper.convention.MatchingStrategies.STRICT;

@Configuration
public class ModelMapperConfiguration {

    private final ModelMapper mapper = new ModelMapper();
    private final Converter<Instant, Long> converterInstantToLong =
            context -> Objects.isNull(context.getSource()) ? null : context.getSource().toEpochMilli();

    @Bean
    public ModelMapper modelMapper() {
        mapper.getConfiguration()
                .setMatchingStrategy(STRICT)
                .setFieldMatchingEnabled(true)
                .setSkipNullEnabled(true)
                .setFieldAccessLevel(PRIVATE);

        configureMappingCodeBlockEntityToCodeBlockDto();
        configureMappingCommentEntityToCommentDto();
        configureMappingEstimateEntityToEstimateDto();
        configureMappingShareEntityToShareDto();
        configureMappingUserEntityToUserDto();

        return mapper;
    }

    private void configureMappingCodeBlockEntityToCodeBlockDto() {
        mapper.typeMap(CodeBlockEntity.class, CodeBlockDto.class)
                .addMapping(codeBlockEntity -> codeBlockEntity.getUser().getId(), CodeBlockDto::setUserId)
                .addMappings(expression -> expression.using(converterInstantToLong)
                        .map(CodeBlockEntity::getCreated, CodeBlockDto::setCreated))
                .addMappings(expression -> expression.using(converterInstantToLong)
                        .map(CodeBlockEntity::getUpdated, CodeBlockDto::setUpdated));
    }

    private void configureMappingCommentEntityToCommentDto() {
        mapper.typeMap(CommentEntity.class, CommentDto.class)
                .addMapping(commentEntity -> commentEntity.getUser().getId(), CommentDto::setUserId)
                .addMapping(commentEntity -> commentEntity.getCodeBlock().getId(), CommentDto::setCodeBlockId)
                .addMappings(expression -> expression.using(converterInstantToLong)
                        .map(CommentEntity::getCreated, CommentDto::setCreated))
                .addMappings(expression -> expression.using(converterInstantToLong)
                        .map(CommentEntity::getUpdated, CommentDto::setUpdated));
    }

    private void configureMappingEstimateEntityToEstimateDto() {
        mapper.typeMap(EstimateEntity.class, EstimateDto.class)
                .addMapping(estimateEntity -> estimateEntity.getUser().getId(), EstimateDto::setUserId)
                .addMapping(estimateEntity -> estimateEntity.getCodeBlock().getId(), EstimateDto::setCodeBlockId);
    }

    private void configureMappingShareEntityToShareDto() {
        mapper.typeMap(ShareEntity.class, ShareDto.class)
                .addMapping(shareEntity -> shareEntity.getToUser().getId(), ShareDto::setToUserId)
                .addMapping(shareEntity -> shareEntity.getFromUser().getId(), ShareDto::setFromUserId)
                .addMapping(shareEntity -> shareEntity.getCodeBlock().getId(), ShareDto::setCodeBlockId);
    }

    private void configureMappingUserEntityToUserDto() {
        mapper.typeMap(UserEntity.class, UserDto.class)
                .addMappings(expression -> expression.using(converterInstantToLong)
                        .map(UserEntity::getCreated, UserDto::setCreated))
                .addMappings(expression -> expression.using(converterInstantToLong)
                        .map(UserEntity::getUpdated, UserDto::setUpdated));
    }
}
