package org.buffer.android.multiactionswipe

object ActionHelper {

    fun getFirstActionWithDirection(actions: List<SwipeAction>, swipeDirection: Int)
            : SwipeAction? {
        return handleAction(actions, swipeDirection, primaryPosition = 0, fallBackPosition = 1)
    }

    fun getSecondActionWithDirection(actions: List<SwipeAction>, swipeDirection: Int)
            : SwipeAction? {
        return handleAction(actions, swipeDirection, primaryPosition = 1, fallBackPosition = 0)
    }

    fun handleAction(actions: List<SwipeAction>, swipeDirection: Int,
                     primaryPosition: Int, fallBackPosition: Int): SwipeAction? {
        if (actions.size == 1) return actions[0]
        var action = actions.firstOrNull {
            it.actionPosition == primaryPosition && swipeDirection == it.swipeDirection
        }
        if (action == null) {
            action = actions.firstOrNull {
                it.actionPosition == fallBackPosition && swipeDirection == it.swipeDirection
            }
        }
        if (action == null) {
            action = actions.firstOrNull { it.actionPosition == primaryPosition }
        }
        if (action == null) {
            action = actions.firstOrNull { it.actionPosition == fallBackPosition }
        }
        return action
    }
}