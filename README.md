# MultiActionSwipeHelper
An Android RecyclerView Swipe Helper for handling multiple actions per direction. The helper allows you to have 4 items in total:

- Short left swipe
- Long left swipe
- Short right swipe
- Long right swipe


![demo](https://github.com/bufferapp/MultiActionSwipeHelper/blob/master/art/demo.gif?raw=true)

Sample app coming soon!

# Usage

The setup is fairly straightforward and requires little code. To begin with, you need to create a list of [SwipeAction](https://github.com/bufferapp/MultiActionSwipeHelper/blob/master/app/src/main/java/org/buffer/android/multiactionswipehelper/SwipeAction.kt) instances - these all provide information around the details for the display of the action (label, icon, color etc)

    val swipeActions = listOf<SwipeAction>()
  
 Next you need to create an instance of the [SwipeToPerformActionCallback](https://github.com/bufferapp/MultiActionSwipeHelper/blob/master/app/src/main/java/org/buffer/android/multiactionswipehelper/SwipeToPerformActionCallback.kt) class, this handles the magic around the display of the current action, as well as passing back which action should be performed when an item is swiped.

    val swipeHandler = SwipeToPerformActionCallback(swipeListener, some_margin_value, it)
    
Finally, create an instance of the [SwipePositionItemTouchHelper](https://github.com/bufferapp/MultiActionSwipeHelper/blob/master/app/src/main/java/org/buffer/android/multiactionswipehelper/SwipePositionItemTouchHelper.java) class and attach it to your recycler view:

    SwipePositionItemTouchHelper(swipeHandler).attachToRecyclerView(recycler_conversations)
