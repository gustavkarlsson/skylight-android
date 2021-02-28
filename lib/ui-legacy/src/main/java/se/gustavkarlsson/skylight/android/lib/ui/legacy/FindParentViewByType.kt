package se.gustavkarlsson.skylight.android.lib.ui.legacy

import android.view.View
import android.view.ViewParent

inline fun <reified T : View> View.findParentViewByType(): T =
    findParentViewInViewByType(T::class.java)

fun <T : View> View.findParentViewInViewByType(clazz: Class<T>): T {
    val parent = parent
    @Suppress("UNCHECKED_CAST")
    return when {
        clazz.isInstance(this) -> this as T
        parent is View -> parent.findParentViewInViewByType(clazz)
        parent is ViewParent -> parent.findParentViewInViewParentByType(clazz)
        else -> throw IllegalStateException("No parent view of type ${clazz.name} found")
    }
}

private fun <T : View> ViewParent.findParentViewInViewParentByType(clazz: Class<T>): T {
    val parent = parent
    @Suppress("UNCHECKED_CAST")
    return when {
        clazz.isInstance(this) -> this as T
        parent is View -> parent.findParentViewInViewByType(clazz)
        parent is ViewParent -> parent.findParentViewInViewParentByType(clazz)
        else -> throw IllegalStateException("No parent view of type ${clazz.name} found")
    }
}
