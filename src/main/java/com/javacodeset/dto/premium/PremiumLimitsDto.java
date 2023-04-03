package com.javacodeset.dto.premium;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PremiumLimitsDto {
    private Integer codeBlocksLimit;
    private Integer codeBlockContentLimit;
    private Integer compilerContentLimit;
}
