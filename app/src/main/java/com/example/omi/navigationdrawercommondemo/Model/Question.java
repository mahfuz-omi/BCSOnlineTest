package com.example.omi.navigationdrawercommondemo.Model;

/**
 * Created by omi on 5/18/2016.
 */
public class Question {
    public int question_id;
    public int set_id;
    public String question;
    public String a,b,c,d,e;
    /// a b c d are options...and e is the answer

    public Question()
    {

    }

    public Question(int question_id, int set_id, String question, String a, String b, String c, String d, String e) {
        this.question_id = question_id;
        this.set_id = set_id;
        this.question = question;
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.e = e;
    }
}
