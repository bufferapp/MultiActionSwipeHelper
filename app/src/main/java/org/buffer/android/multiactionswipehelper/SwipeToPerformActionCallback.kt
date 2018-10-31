package org.buffer.android.multiactionswipe

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper.LEFT
import android.support.v7.widget.helper.ItemTouchHelper.RIGHT

class SwipeToPerformActionCallback(private val swipeListener: SwipeActionListener,
                                   private val textPadding: Int = 0,
                                   var conversationActions: List<SwipeAction>)
    : org.buffer.android.multiactionswipe.SwipePositionItemTouchHelper.Callback() {

    override fun getMovementFlags(recyclerView: RecyclerView?,
                                  viewHolder: RecyclerView.ViewHolder?): Int {
        return makeMovementFlags(0, LEFT or RIGHT)
    }

    private val background = ColorDrawable()
    private val clearPaint = Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) }

    private var currentIcon: Drawable? = null
    private var currentLabel: String = ""

    override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?,
                        target: RecyclerView.ViewHolder?): Boolean {
        return false
    }

    override fun onChildDraw(canvas: Canvas?, recyclerView: RecyclerView,
                             viewHolder: RecyclerView.ViewHolder,
                             dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {

        val dragDirection = if (dX < 0) RIGHT else LEFT
        val parentWidth = recyclerView.width

        val itemView = viewHolder.itemView
        val itemHeight = itemView.bottom - itemView.top
        val itemCenter = (itemView.bottom + itemView.top) / 2f
        val isCanceled = dX == 0f && !isCurrentlyActive

        if (isCanceled) {
            clearCanvas(canvas, itemView.right + dX, itemView.top.toFloat(),
                    itemView.right.toFloat(), itemView.bottom.toFloat())
            super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY,
                    actionState, isCurrentlyActive)
            return
        }

        val isFirstHalf = Math.abs(dX) < (parentWidth / 2)
        val paint = Paint()

        if (isCurrentlyActive) {
            val action =
                    if (isFirstHalf) {
                        ActionHelper.getFirstActionWithDirection(conversationActions,
                                dragDirection)
                    } else {
                        ActionHelper.getSecondActionWithDirection(conversationActions,
                                dragDirection)
                    }
            action?.let {
                background.color = ContextCompat.getColor(recyclerView.context,
                        action.backgroundColor)
                currentIcon = ContextCompat.getDrawable(recyclerView.context, action.icon)
                paint.color = ContextCompat.getColor(recyclerView.context, action.labelColor)
                currentLabel = recyclerView.context.resources.getString(it.identifier)
            }
        }

        val intrinsicWidth = currentIcon?.intrinsicWidth ?: 0
        val intrinsicHeight = currentIcon?.intrinsicHeight ?: 0
        val currentIconTop = itemView.top + (itemHeight - intrinsicHeight) / 2
        val currentIconBottom = currentIconTop + intrinsicHeight
        val currentIconMargin = (itemHeight - intrinsicHeight) / 2

        val currentIconLeft: Int
        val currentIconRight: Int
        val textPositionX: Int
        val textPositionY: Float

        paint.textSize = recyclerView.context.resources
                .getDimensionPixelSize(R.dimen.text_large_body).toFloat()
        paint.textAlign = Paint.Align.LEFT
        paint.isAntiAlias = true
        paint.color = Color.WHITE

        val textBounds = Rect()
        paint.getTextBounds(currentLabel, 0, currentLabel.length, textBounds)
        val textWidth = textBounds.width()
        textPositionY = itemCenter - textBounds.exactCenterY()

        if (dragDirection == RIGHT) {
            background.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right,
                    itemView.bottom)

            currentIconLeft = itemView.right - currentIconMargin - intrinsicWidth
            currentIconRight = itemView.right - currentIconMargin
            currentIcon?.setBounds(currentIconLeft, currentIconTop, currentIconRight,
                    currentIconBottom)

            textPositionX = (itemView.right - currentIconMargin - intrinsicWidth -
                    textPadding - textWidth)
        } else {
            background.setBounds(itemView.left, itemView.top, itemView.left + dX.toInt(),
                    itemView.bottom)

            currentIconLeft = itemView.left + currentIconMargin
            currentIconRight = itemView.left + currentIconMargin + intrinsicWidth
            currentIcon?.setBounds(currentIconLeft, currentIconTop, currentIconRight,
                    currentIconBottom)

            textPositionX = itemView.left + currentIconMargin + intrinsicWidth + textPadding
        }
        canvas?.let {
            background.draw(it)
            currentIcon?.draw(it)
            it.drawText(currentLabel, textPositionX.toFloat(), textPositionY, paint)
        }


        super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int,
                          horizontalTouchPosition: Float) {
        val position =
                if (Math.abs(horizontalTouchPosition) < (viewHolder.itemView.width / 2)) 0 else 1
        val dragDirection = if (direction == LEFT) RIGHT else LEFT
        val fallback = if (position == 0) 1 else 0
        val action = ActionHelper.handleAction(conversationActions, dragDirection, position,
                fallback)
        swipeListener.onActionPerformed(viewHolder.adapterPosition, action)
    }

    private fun clearCanvas(c: Canvas?, left: Float, top: Float, right: Float, bottom: Float) {
        c?.drawRect(left, top, right, bottom, clearPaint)
    }
}
