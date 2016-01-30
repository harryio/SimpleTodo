package io.theappx.simpletodo.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.theappx.simpletodo.R;
import io.theappx.simpletodo.model.TodoItem;
import io.theappx.simpletodo.utils.FormatUtils;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.ViewHolder> {
    private List<TodoItem> mTodoItems;
    private OnItemClickListener mOnItemClickListener;

    public TodoAdapter() {
        mTodoItems = Collections.emptyList();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_layout, parent, false), mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TodoItem lTodoItem = mTodoItems.get(position);

        holder.setTodoItem(lTodoItem);

        String title = lTodoItem.getTitle();
        String lDescription = lTodoItem.getDescription();

        holder.titleTextView.setText(title);

        if (lDescription != null) {
            holder.descriptionTextView.setText(lDescription);
        } else holder.descriptionTextView.setVisibility(View.GONE);

        if (lTodoItem.shouldBeReminded()) {
            String lDateTime = FormatUtils.getStringFromDate(lTodoItem.getCompleteDate());
            holder.timerView.setVisibility(View.VISIBLE);
            holder.timerTextView.setText(lDateTime);
        } else {
            holder.timerView.setVisibility(View.GONE);
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
        @Bind(R.id.v_timer)
        LinearLayout timerView;

        private TodoItem mTodoItem;
        private OnItemClickListener mOnItemClickListener;

        @OnClick(R.id.item_root_view)
        public void onItemClick() {
            if (mOnItemClickListener == null)
                throw new NullPointerException("OnItemClickListener not set");

            mOnItemClickListener.onListItemClick(mTodoItem);
        }

        public ViewHolder(View itemView, OnItemClickListener pOnItemClickListener) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            mOnItemClickListener = pOnItemClickListener;
        }

        public void setTodoItem(TodoItem pTodoItem) {
            mTodoItem = pTodoItem;
        }
    }

    public interface OnItemClickListener {
        void onListItemClick(TodoItem pTodoItem);
    }

    public void setOnItemClickListener(OnItemClickListener pOnItemClickListener) {
        mOnItemClickListener = pOnItemClickListener;
    }
}
