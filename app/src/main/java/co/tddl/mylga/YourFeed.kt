package co.tddl.mylga


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import co.tddl.mylga.adapter.FeedsRecyclerviewAdapter
import co.tddl.mylga.model.Feed
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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

    private lateinit var name:String
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_your_feed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getYourFeeds()

        fab.setOnClickListener {
            val intent = Intent(activity?.applicationContext, ShareActivity::class.java) 
            activity?.startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        getYourFeeds()
    }

    companion object {
        fun newInstance(): YourFeed = YourFeed()
    }

    private fun initRecyclerView(feeds: ArrayList<Feed>){
        if(!feeds.isNullOrEmpty()){
            text_view_welcome.text = "Hey $name, welcome to your feed"
            recyclerView.visibility = View.GONE
            noUpdateCardView.visibility = View.VISIBLE
        }else{
            recyclerView.visibility = View.VISIBLE
            noUpdateCardView.visibility = View.GONE

            recyclerView.apply {
                layoutManager = LinearLayoutManager(activity)
                adapter = FeedsRecyclerviewAdapter(feeds)
            }
        }
    }

    private fun getYourFeeds(){
        val feeds = arrayListOf<Feed>()
        // val db = FirebaseFirestore.getInstance()
        // val auth = FirebaseAuth.getInstance()

        db.collection("posts")
            .whereEqualTo("userId", auth.currentUser?.uid.toString())
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d("Result", "${document.id} => ${document.data}")
                    feeds.add(Feed(document["imageUrl"].toString(), document["description"].toString(), document["location"].toString()))
                }
                initRecyclerView(feeds)
            }
            .addOnFailureListener { exception ->
                Log.w("Error", "Error getting documents: ", exception)
            }
    }
}
