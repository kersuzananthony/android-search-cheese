package com.kersuzananthony.searchcheese;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

class CheeseAdapter extends RecyclerView.Adapter<CheeseAdapter.CheeseView> {

    private List<String> mCheeses;

    CheeseAdapter() {
        mCheeses = new ArrayList<>();
    }

    @Override
    public CheeseAdapter.CheeseView onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new CheeseView(view);
    }

    @Override
    public void onBindViewHolder(CheeseAdapter.CheeseView holder, int position) {
        if (mCheeses == null) return;

        holder.onBind(mCheeses.get(position));
    }

    @Override
    public int getItemCount() {
        return mCheeses == null ? 0 : mCheeses.size();
    }

    public void setCheeses(List<String> cheeses) {
        mCheeses = cheeses;

        notifyDataSetChanged();
    }

    static class CheeseView extends RecyclerView.ViewHolder {

        private TextView mTextView;

        public CheeseView(View itemView) {
            super(itemView);

            mTextView = (TextView) itemView.findViewById(R.id.textView);
        }

        public void onBind(String value) {
            mTextView.setText(value);
        }
    }
}
