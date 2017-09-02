package com.erpdevelopment.vbvm.model.pojo;

import com.erpdevelopment.vbvm.model.Answer;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by thomascarey on 2/09/17.
 */

public class Answers {
    @SerializedName("answers")
    @Expose
    private List<Answer> answers = null;

    public List<Answer> getAnswers() { return answers; }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }
}
