package io.theappx.simpletodo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.theappx.simpletodo.R;
import io.theappx.simpletodo.helper.ItemTouchHelperAdapter;
import io.theappx.simpletodo.model.TodoItem;
import io.theappx.simpletodo.utils.FormatUtils;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.ViewHolder>
        implements ItemTouchHelperAdapter {
    private List<TodoItem> mTodoItems;
    private OnItemClickListener mOnItemClickListener;
    private OnItemDismissListener onItemDismissListener;
    private Context context;

    public TodoAdapter(Context context) {
        mTodoItems = Collections.emptyList();
        this.context = context;
    }

    static class ViewHolder extends RecyclerView.ViewHolder
            {
        @Bind(R.id.tv_title)
        TextView titleTextView;
        @Bind(R.id.tv_description)
        TextView descriptionTextView;
        @Bind(R.id.date_textview)
        TextView dateTextView;
        @Bind(R.id.time_textview)
        TextView timeTextView;
        @Bind(R.id.timer_imageview)
        ImageView timerImageView;
        @Bind(R.id.timer_section_view)
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

        if (!TextUtils.isEmpty(lDescription)) {
            holder.descriptionTextView.setText(lDescription);
        } else holder.descriptionTextView.setVisibility(View.GONE);

        holder.timerView.setBackgroundColor(lTodoItem.getColor());
        if (lTodoItem.shouldBeReminded()) {
            holder.dateTextView.setVisibility(View.VISIBLE);
            holder.timeTextView.setVisibility(View.VISIBLE);
            holder.timerImageView.setVisibility(View.VISIBLE);

            Date dateInstance = lTodoItem.getDateInstance();
            holder.dateTextView.setText(FormatUtils.getCompatDateString(dateInstance));
            holder.timeTextView.setText(DateFormat.is24HourFormat(context) ?
                    FormatUtils.get24HourTimeStringFromDate(dateInstance) :
                    FormatUtils.getTimeStringFromDate(dateInstance));
        } else {
            holder.dateTextView.setVisibility(View.GONE);
            holder.timeTextView.setVisibility(View.GONE);
            holder.timerImageView.setVisibility(View.GONE);

        }
    }

    @Override
    public int getItemCount() {
        return mTodoItems.size();
    }

    public void setTodoItems(List<TodoItem> pTodoItems, boolean shouldReverse) {
        ArrayList<TodoItem> itemArrayList = new ArrayList<>(pTodoItems);
        if (shouldReverse)
            Collections.reverse(itemArrayList);
        mTodoItems = itemArrayList;
        notifyDataSetChanged();
    }

    public void addTodoItem(int position, TodoItem todoItem) {
        mTodoItems.add(position, todoItem);
        notifyItemInserted(position);
    }

    public void replaceTodoItem(int position, TodoItem todoItem) {
        mTodoItems.set(position, todoItem);
        notifyDataSetChanged();
    }

    public List<TodoItem> getCurrentAdapterList() {
        return mTodoItems;
    }

    @Override
    public void onItemDismiss(int position) {
        if (onItemDismissListener != null) {
            onItemDismissListener.onItemDismissed(position, mTodoItems.get(position));
        } else
            throw new NullPointerException("OnItemDismissListener not implemented");

        mTodoItems.remove(position);
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
