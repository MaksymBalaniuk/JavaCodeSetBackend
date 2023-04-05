package com.javacodeset.config;

import com.javacodeset.dto.ShareDto;
import com.javacodeset.entity.ShareEntity;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.javacodeset.dto.CodeBlockDto;
import com.javacodeset.dto.CommentDto;
import com.javacodeset.dto.EstimateDto;
import com.javacodeset.entity.CodeBlockEntity;
import com.javacodeset.entity.CommentEntity;
import com.javacodeset.entity.EstimateEntity;

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
        mapper.typeMap(ShareEntity.class, ShareDto.class)
                .addMapping(shareEntity -> shareEntity.getToUser().getId(), ShareDto::setToUserId)
                .addMapping(shareEntity -> shareEntity.getFromUser().getId(), ShareDto::setFromUserId)
                .addMapping(shareEntity -> shareEntity.getCodeBlock().getId(), ShareDto::setCodeBlockId);

        return mapper;
    }
}
