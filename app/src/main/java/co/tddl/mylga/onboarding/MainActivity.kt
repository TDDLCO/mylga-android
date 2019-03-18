package co.tddl.mylga.onboarding

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log
import android.view.*
import co.tddl.mylga.R

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var layouts: IntArray = intArrayOf(
        R.layout.introscreen1,
        R.layout.introscreen2,
        R.layout.introscreen3,
        R.layout.introscreen4
    )

    lateinit var mAdapter: OnboardingPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(Build.VERSION.SDK_INT >= 19){
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }else{
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }

        setContentView(R.layout.activity_main)

        mAdapter = OnboardingPagerAdapter(layouts, this)

        view_pager.adapter = mAdapter

        btn_next.setOnClickListener { loadNextSlide() }

        view_pager.addOnPageChangeListener(object : androidx.viewpager.widget.ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(p0: Int) {

            }

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {

            }

            override fun onPageSelected(p0: Int) {
                Log.v("CLICK", "YOU CLICKED ME AGAIN AT $p0")
                if(p0 == layouts.size - 1 ){
                    btn_next.text = "Let's explore!"
                }else{
                    btn_next.text = "Next"
                }

            }
        })
    }

    private fun loadNextSlide() {
        Log.v("CLICK", "YOU CLICKED ME ${layouts.size}")
        var nextSlide: Int = view_pager.currentItem + 1

        if(nextSlide < layouts.size){
            view_pager.currentItem = nextSlide
        }else{
            var intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }
        // else load the main activity
    }

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

}
