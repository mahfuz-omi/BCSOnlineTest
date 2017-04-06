package com.example.omi.navigationdrawercommondemo.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.omi.navigationdrawercommondemo.Model.Error;
import com.example.omi.navigationdrawercommondemo.R;

import java.util.List;

/**
 * Created by omi on 5/19/2016.
 */
public class ErrorsShowDataAdapter extends RecyclerView.Adapter<ErrorsShowDataAdapter.MyViewHolder>
{

    public List<Error> errors;

    public ErrorsShowDataAdapter(List<Error> errors) {
        this.errors = errors;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.errors_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Error error = errors.get(position);
        holder.question.setText("Question: "+error.getQuestion());
        holder.given_answer.setText("Your answer: "+error.getGiven());
        holder.correct_answer.setText("Correct answer: "+error.getAnswer());
    }

    @Override
    public int getItemCount() {
        return this.errors.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        public TextView question, given_answer, correct_answer;

        public MyViewHolder(View view)
        {
            super(view);
            question = (TextView) view.findViewById(R.id.question);
            given_answer = (TextView) view.findViewById(R.id.given_answer);
            correct_answer = (TextView) view.findViewById(R.id.correct_answer);
        }
    }


}




