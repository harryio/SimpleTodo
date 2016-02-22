package io.theappx.simpletodo.customview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Custom RecyclerView with support for empty view
 */
public class EmptyStateRecyclerView extends RecyclerView {
    private View emptyView;

    private AdapterDataObserver adapterDataObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            updateEmptyView();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            updateEmptyView();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            updateEmptyView();
        }
    };

    public EmptyStateRecyclerView(Context context) {
        super(context);
    }

    public EmptyStateRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public EmptyStateRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setAdapter(RecyclerView.Adapter adapter) {
        if (getAdapter() != null) {
            getAdapter().unregisterAdapterDataObserver(adapterDataObserver);
        }

        if (adapter != null) {
            adapter.registerAdapterDataObserver(adapterDataObserver);

        }
        super.setAdapter(adapter);
        updateEmptyView();
    }

    public void setEmptyView(View emptyView) {
        this.emptyView = emptyView;
    }

    private void updateEmptyView() {
        if (emptyView != null && getAdapter() != null) {
            boolean showEmptyView = getAdapter().getItemCount() == 0;
            if (showEmptyView) emptyView.setAlpha(0);
            emptyView.setVisibility(showEmptyView ? VISIBLE : GONE);
            if (showEmptyView) {
                emptyView.animate().alpha(1).setDuration(500).start();
            }
            setVisibility(showEmptyView ? GONE : VISIBLE);


        }
    }
}
