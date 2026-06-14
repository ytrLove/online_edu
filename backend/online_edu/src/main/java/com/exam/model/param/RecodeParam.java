package com.exam.model.param;

import jakarta.validation.Valid;
import lombok.Data;
import lombok.NonNull;

import java.util.List;

@Data
public class RecodeParam {
    private Long recordId;
    private Long examId;

    @Valid
    private List<AnswerParam> answers;
}
