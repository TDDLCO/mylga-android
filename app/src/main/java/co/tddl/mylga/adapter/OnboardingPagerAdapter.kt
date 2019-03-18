package co.tddl.mylga.adapter

import android.content.Context
import androidx.viewpager.widget.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class OnboardingPagerAdapter : androidx.viewpager.widget.PagerAdapter {

    private var layouts: IntArray

    private var inflater: LayoutInflater

    private var ctx: Context

    constructor(layouts: IntArray, ctx: Context) : super() {
        this.layouts = layouts
        this.ctx = ctx
        this.inflater = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }


    override fun getCount(): Int {
        return this.layouts.size
    }

    override fun isViewFromObject(p0: View, p1: Any): Boolean {
        return p0 == p1
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        var view: View = inflater.inflate(layouts[position], container, false)
        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        var view: View = `object` as View
        container.removeView(view)

    }
}