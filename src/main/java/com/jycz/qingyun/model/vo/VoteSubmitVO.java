package com.jycz.qingyun.model.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VoteSubmitVO {//

    private Long voteId;
    private String selectedOption;
    private Integer isCorrect;
    private String correctOption;
}