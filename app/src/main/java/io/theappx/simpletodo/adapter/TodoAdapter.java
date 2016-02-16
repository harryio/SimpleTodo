package io.theappx.simpletodo.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.theappx.simpletodo.R;
import io.theappx.simpletodo.databinding.ListItemLayoutBinding;
import io.theappx.simpletodo.helper.ItemTouchHelperAdapter;
import io.theappx.simpletodo.model.TodoItem;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.ViewHolder>
        implements ItemTouchHelperAdapter {
    private List<TodoItem> mTodoItems;
    private OnItemClickListener mOnItemClickListener;
    private OnItemDismissListener onItemDismissListener;

    public TodoAdapter() {
        mTodoItems = Collections.emptyList();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.checkbox)
        CheckBox checkBox;

        ListItemLayoutBinding listItemLayoutBinding;

        private TodoItem mTodoItem;
        private OnItemClickListener mOnItemClickListener;

        public ViewHolder(View itemView, OnItemClickListener pOnItemClickListener) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            mOnItemClickListener = pOnItemClickListener;
            listItemLayoutBinding = ListItemLayoutBinding.bind(itemView);

            setUpCheckBox();
        }

        public void setTodoItem(TodoItem pTodoItem) {
            mTodoItem = pTodoItem;
            listItemLayoutBinding.setTodoItem(pTodoItem);
        }

        @OnClick(R.id.item_root_view)
        public void onItemClick() {
            if (mOnItemClickListener == null)
                throw new NullPointerException("OnItemClickListener not set");

            mOnItemClickListener.onListItemClick(getAdapterPosition(), mTodoItem);
        }

        private void setUpCheckBox() {
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mOnItemClickListener.onCheckChanged(isChecked, mTodoItem);
                }
            });
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
        void onCheckChanged(boolean isChecked, TodoItem todoItem);
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
