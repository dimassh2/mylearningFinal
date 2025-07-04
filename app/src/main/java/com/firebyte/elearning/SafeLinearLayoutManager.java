package com.firebyte.elearning;

import android.content.Context;
import android.util.Log;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Custom LinearLayoutManager untuk mencegah crash "Inconsistency detected".
 * Ini menangani IndexOutOfBoundsException yang bisa terjadi saat data adapter
 * berubah selama proses layout RecyclerView.
 */
public class SafeLinearLayoutManager extends LinearLayoutManager {

    public SafeLinearLayoutManager(Context context) {
        super(context);
    }

    public SafeLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (IndexOutOfBoundsException e) {
            Log.e("SafeLayoutManager", "Inconsistency detected and handled.", e);
        }
    }
}