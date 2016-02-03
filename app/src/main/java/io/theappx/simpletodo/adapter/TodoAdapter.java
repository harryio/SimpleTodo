package io.theappx.simpletodo.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.theappx.simpletodo.R;
import io.theappx.simpletodo.helper.ItemTouchHelperAdapter;
import io.theappx.simpletodo.helper.ItemTouchHelperViewHolder;
import io.theappx.simpletodo.model.TodoItem;
import io.theappx.simpletodo.utils.FormatUtils;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.ViewHolder>
        implements ItemTouchHelperAdapter {
    private List<TodoItem> mTodoItems;
    private OnItemClickListener mOnItemClickListener;
    private OnItemDismissListener onItemDismissListener;
    private Context context;
    private int listSize;

    public TodoAdapter(Context context) {
        mTodoItems = Collections.emptyList();
        this.context = context;
    }

    static class ViewHolder extends RecyclerView.ViewHolder
            implements ItemTouchHelperViewHolder {
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

            mOnItemClickListener.onListItemClick(getAdapterPosition(), mTodoItem);
        }

        public ViewHolder(View itemView, OnItemClickListener pOnItemClickListener) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            mOnItemClickListener = pOnItemClickListener;
        }

        public void setTodoItem(TodoItem pTodoItem) {
            mTodoItem = pTodoItem;
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_layout, parent, false), mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TodoItem lTodoItem = mTodoItems.get((listSize - position) - 1);

        holder.setTodoItem(lTodoItem);

        String title = lTodoItem.getTitle();
        String lDescription = lTodoItem.getDescription();

        holder.titleTextView.setText(title);

        if (lDescription != null) {
            holder.descriptionTextView.setText(lDescription);
        } else holder.descriptionTextView.setVisibility(View.GONE);

        if (lTodoItem.shouldBeReminded()) {
            String lDateTime = FormatUtils.getCompactStringFromDate(lTodoItem.getDateInstance(),
                    DateFormat.is24HourFormat(context));
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
        mTodoItems = new ArrayList<>(pTodoItems);
        listSize = pTodoItems.size();
        notifyDataSetChanged();
    }

    public void addTodoItem(int position, TodoItem todoItem) {
        mTodoItems.add(position, todoItem);
        listSize++;
        notifyItemInserted(position);
    }

    @Override
    public void onItemDismiss(int position) {
        if (onItemDismissListener != null) {
            onItemDismissListener.onItemDismissed(position, mTodoItems.get(position));
        } else
            throw new NullPointerException("OnItemDismissListener not implemented");

        mTodoItems.remove(position);
        listSize--;
        notifyItemRemoved(position);
    }

    public interface OnItemClickListener {
        void onListItemClick(int position, TodoItem pTodoItem);

    }
    public interface OnItemDismissListener {
        void onItemDismissed(int position, TodoItem todoItem);

    }

    public void setOnItemClickListener(OnItemClickListener pOnItemClickListener) {
        mOnItemClickListener = pOnItemClickListener;
    }

    public void setOnItemDismissListener(OnItemDismissListener onItemDismissListener) {
        this.onItemDismissListener = onItemDismissListener;
    }
}
