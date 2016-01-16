package io.theappx.simpletodo.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.theappx.simpletodo.R;
import io.theappx.simpletodo.model.TodoItem;
import io.theappx.simpletodo.utils.FormatUtils;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.ViewHolder> {
    private List<TodoItem> mTodoItems;

    public TodoAdapter() {
        mTodoItems = Collections.emptyList();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TodoItem lTodoItem = mTodoItems.get(position);

        holder.titleTextView.setText(lTodoItem.getTitle());

        String lDescription = lTodoItem.getDescription();
        if (lDescription != null) {
            holder.descriptionTextView.setText(lDescription);
        } else holder.descriptionTextView.setVisibility(View.GONE);

        if (lTodoItem.shouldBeReminded()) {
            String lDateTime = FormatUtils.getDayStringFromDate(lTodoItem.getCompleteDate());
            holder.timerTextView.setText(lDateTime);
        } else {
            holder.timerTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mTodoItems.size();
    }

    public void setTodoItems(List<TodoItem> pTodoItems) {
        mTodoItems = pTodoItems;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_title)
        TextView titleTextView;
        @Bind(R.id.tv_description)
        TextView descriptionTextView;
        @Bind(R.id.tv_timer)
        TextView timerTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
