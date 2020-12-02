package com.zenger.cookbook.ui.widgets

import android.content.Context
import android.util.AttributeSet
import android.widget.Checkable
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CheckableFloatingActionButton : FloatingActionButton, Checkable {

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(context, attributeSet, defStyleAttr)

    private var mChecked = false

    companion object {
        val CHECKED_STATE_SET: IntArray = intArrayOf(android.R.attr.state_checked)
    }

    override fun setChecked(checked: Boolean) {
        mChecked = checked
        refreshDrawableState()
    }

    override fun isChecked(): Boolean {
        return mChecked
    }

    override fun toggle() {
        mChecked = !mChecked
        refreshDrawableState()
    }

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val drawableState = super.onCreateDrawableState(extraSpace + 1)
        if (isChecked) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET)
        }
        return drawableState
    }

    override fun performClick(): Boolean {
        toggle()
        return super.performClick()
    }
}