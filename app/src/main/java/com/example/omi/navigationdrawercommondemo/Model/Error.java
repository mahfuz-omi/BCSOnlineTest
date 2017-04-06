package com.example.omi.navigationdrawercommondemo.Model;

import java.io.Serializable;

/**
 * Created by omi on 5/18/2016.
 */
public class Error implements Serializable {
    String question;
    String answer;
    String given;


    public Error(String question, String answer, String given) {
        super();
        this.question = question;
        this.answer = answer;
        this.given = given;
    }

    public Error() {
        super();
    }

    public String getQuestion() {
        return question;
    }
    public void setQuestion(String question) {
        this.question = question;
    }
    public String getAnswer() {
        return answer;
    }
    public void setAnswer(String answer) {
        this.answer = answer;
    }
    public String getGiven() {
        return given;
    }
    public void setGiven(String given) {
        this.given = given;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub

        return (question+" "+answer+" "+given);
    }



}

