package co.tddl.mylga


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import co.tddl.mylga.adapter.FeedsRecyclerviewAdapter
import co.tddl.mylga.model.Feed
import kotlinx.android.synthetic.main.fragment_your_feed.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class YourFeed : Fragment() {

    private val feeds = listOf(
        Feed(R.drawable.tokyo, "The Tokyo view", "Tokyo, Japan"),
        Feed(R.drawable.barcelona, "This is where T am currently", "Barcelona"),
        Feed(R.drawable.tokyo, "Another Tokyo view", "Tokyo"),
        Feed(R.drawable.barcelona, "Barcelona baby", "Barcelona")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_your_feed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = FeedsRecyclerviewAdapter(feeds)
        }
        fab.setOnClickListener {
            val intent = Intent(activity?.applicationContext, ShareActivity::class.java)
            activity?.startActivity(intent)
        }
    }

    companion object {
        fun newInstance(): YourFeed = YourFeed()
    }


}
