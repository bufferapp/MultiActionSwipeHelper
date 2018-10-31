package org.buffer.android.multiactionswipe

interface SwipeActionListener {
    fun onActionPerformed(itemPosition: Int, action: SwipeAction?)
}