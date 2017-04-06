package com.example.omi.navigationdrawercommondemo.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.omi.navigationdrawercommondemo.Model.History;
import com.example.omi.navigationdrawercommondemo.R;

/**
 * Created by omi on 5/23/2016.
 */
public class HistoryShowAdapter extends RecyclerView.Adapter<HistoryShowAdapter.MyViewHolder>
{

    public History []histories;

    public HistoryShowAdapter(History []histories) {
        this.histories = histories;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        History history = this.histories[position];
        holder.set_id.setText(history.getSet_id());
        holder.got_marks.setText(history.getGot_marks());
        holder.date.setText(history.getDate());
    }

    @Override
    public int getItemCount() {
        return this.histories.length;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        public TextView set_id, got_marks, date;

        public MyViewHolder(View view)
        {
            super(view);
            set_id = (TextView) view.findViewById(R.id.set_id);
            got_marks = (TextView) view.findViewById(R.id.got_marks);
            date = (TextView) view.findViewById(R.id.date);
        }
    }


}


