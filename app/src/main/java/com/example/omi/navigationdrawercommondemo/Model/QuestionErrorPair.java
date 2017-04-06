package com.example.omi.navigationdrawercommondemo.Model;

/**
 * Created by omi on 5/20/2016.
 */
public class QuestionErrorPair {
    public String question;
    public String answer;


    public QuestionErrorPair()
    {

    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setQuestion(String question) {

        this.question = question;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public QuestionErrorPair(String question, String answer) {
        this.question = question;
        this.answer = answer;

    }
}
