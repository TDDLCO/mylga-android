package co.tddl.mylga

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.view.View

import androidx.appcompat.app.AppCompatActivity


import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val shortAnimationDuration = resources.getInteger(android.R.integer.config_shortAnimTime)

        image_view_profile.setOnClickListener {

            appbar.visibility = View.GONE
            nested_scroll_view_content.visibility = View.GONE

            relative_layout_expanded_image.apply {
                alpha = 0.85f
                visibility = View.VISIBLE

                animate()
                    .alpha(1f)
                    .setDuration(shortAnimationDuration.toLong())
                    .setListener(null)
            }

            nested_scroll_view_content.animate()
                .alpha(1f)
                .setDuration(shortAnimationDuration.toLong())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        appbar.visibility = View.GONE
                        nested_scroll_view_content.visibility = View.GONE
                    }
                })
        }

        image_button_close.setOnClickListener {
            appbar.visibility = View.VISIBLE
            nested_scroll_view_content.visibility = View.VISIBLE
            relative_layout_expanded_image.visibility = View.GONE
        }
    }

}
