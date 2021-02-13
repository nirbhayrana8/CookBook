package com.zenger.cookbook.ui.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.appbar.MaterialToolbar
import com.zenger.cookbook.R
import de.hdodenhof.circleimageview.CircleImageView
import timber.log.Timber

class AvatarImageBehaviour(context: Context, attributeSet: AttributeSet?) : CoordinatorLayout.Behavior<CircleImageView>() {

    private var mCustomStartHeight = 0f
    private var mCustomFinalHeight = 0f

    private var mStartToolbarPosition = 0f
    private var mChangeBehaviourPoint = 0f
    private var mStartYPosition = 0
    private var mFinalYPosition = 0
    private var mStartHeight = 0

    init {
        if (attributeSet != null) {
            val array = context.obtainStyledAttributes(attributeSet, R.styleable.AvatarImageBehavior)
            mCustomStartHeight = array.getDimension(R.styleable.AvatarImageBehavior_startHeight, 0f)
            mCustomFinalHeight = array.getDimension(R.styleable.AvatarImageBehavior_finalHeight, 0f)

            array.recycle()
        }
    }

    override fun layoutDependsOn(parent: CoordinatorLayout, child: CircleImageView, dependency: View): Boolean {

        return dependency is MaterialToolbar
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: CircleImageView, dependency: View): Boolean {
        maybeInitProperties(child, dependency)

        val maxScrollDistance = mStartToolbarPosition.toInt()
        val expandedPercentageFactor: Float = dependency.y / maxScrollDistance
        Timber.d("maxScrollDistance: $maxScrollDistance \n expandedPercentageFactor: $expandedPercentageFactor \n dependency.y: ${dependency.y}")

        if (expandedPercentageFactor < mChangeBehaviourPoint) {

            val heightFactor = (mChangeBehaviourPoint - expandedPercentageFactor) / mChangeBehaviourPoint

            val distanceYToSubtract = ((mStartYPosition - mFinalYPosition) *
                    (1f - expandedPercentageFactor)) + (child.height / 2)

            child.y = mStartYPosition - distanceYToSubtract

            val heightToSubtract = (mStartHeight - mCustomStartHeight) * heightFactor


            val layoutParams = child.layoutParams as CoordinatorLayout.LayoutParams
            layoutParams.width = (mStartHeight - heightToSubtract).toInt()
            layoutParams.height = (mStartHeight - heightToSubtract).toInt()
            child.layoutParams = layoutParams
        } else {

            val distanceYToSubtract = ((mStartYPosition - mFinalYPosition)
                    * (1f - expandedPercentageFactor)) + (mStartHeight / 2)

            child.y = mStartYPosition - distanceYToSubtract

            val layoutParams = child.layoutParams as CoordinatorLayout.LayoutParams
            layoutParams.width = mStartHeight
            layoutParams.height = mStartHeight
            child.layoutParams = layoutParams
        }

        return true
    }

    private fun maybeInitProperties(child: CircleImageView, dependency: View) {

        if (mStartYPosition == 0) mStartYPosition = dependency.y.toInt()

        if (mFinalYPosition == 0) mFinalYPosition = (dependency.height / 2)

        if (mStartHeight == 0) mStartHeight = child.height

        if (mStartToolbarPosition == 0f) mStartToolbarPosition = dependency.y

        if (mChangeBehaviourPoint == 0f)
            mChangeBehaviourPoint = (child.height - mCustomFinalHeight) / (2f * (mStartYPosition - mFinalYPosition))
    }

}