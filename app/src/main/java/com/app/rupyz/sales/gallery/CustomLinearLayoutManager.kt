package com.app.rupyz.sales.gallery

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


@SuppressLint("WrongConstant")
class CustomLinearLayoutManager(
    context: Context?, orientation: Int = VERTICAL, reverseLayout: Boolean = false
) : LinearLayoutManager(context, orientation, reverseLayout) {

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        try {
            super.onLayoutChildren(recycler, state)
        } catch (e: IndexOutOfBoundsException) {
            Log.e(TAG, "Inconsistency detected")
        }
    }
    override fun supportsPredictiveItemAnimations(): Boolean {
        return false
    }

    companion object {
        private const val TAG = "CustomLinearLayoutManager"
    }
}


