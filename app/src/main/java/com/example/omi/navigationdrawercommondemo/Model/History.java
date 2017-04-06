package com.example.omi.navigationdrawercommondemo.Model;

import java.io.Serializable;

/**
 * Created by omi on 5/23/2016.
 */
public class History implements Serializable {

    public String set_id;
    public String got_marks;
    public String date;

    public History()
    {

    }

    public void setSet_id(String set_id) {
        this.set_id = set_id;
    }

    public void setGot_marks(String got_marks) {
        this.got_marks = got_marks;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSet_id() {

        return set_id;
    }

    public String getGot_marks() {
        return got_marks;
    }

    public String getDate() {
        return date;
    }

    public History(String set_id, String got_marks, String date) {

        this.set_id = set_id;
        this.got_marks = got_marks;
        this.date = date;
    }
}
