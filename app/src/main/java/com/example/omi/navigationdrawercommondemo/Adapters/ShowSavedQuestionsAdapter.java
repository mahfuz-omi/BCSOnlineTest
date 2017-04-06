package com.example.omi.navigationdrawercommondemo.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.omi.navigationdrawercommondemo.Model.QuestionErrorPair;
import com.example.omi.navigationdrawercommondemo.R;

import java.util.List;

/**
 * Created by omi on 5/20/2016.
 */
public class ShowSavedQuestionsAdapter extends RecyclerView.Adapter<ShowSavedQuestionsAdapter.MyViewHolder>{


    public List<QuestionErrorPair> pairList;

    public ShowSavedQuestionsAdapter(List<QuestionErrorPair> pairList) {
        this.pairList = pairList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.saved_questions_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        QuestionErrorPair pair = pairList.get(position);
        holder.question.setText("Question" +(position+1)+": "+pair.getQuestion());
        holder.answer.setText("Aanswer: "+pair.getAnswer());
    }

    @Override
    public int getItemCount() {
        return this.pairList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        public TextView question, answer;

        public MyViewHolder(View view)
        {
            super(view);
            question = (TextView) view.findViewById(R.id.question);
            answer = (TextView) view.findViewById(R.id.answer);
        }
    }
}
