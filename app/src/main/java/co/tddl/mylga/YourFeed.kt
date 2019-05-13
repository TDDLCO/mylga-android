package co.tddl.mylga


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.tddl.mylga.adapter.FeedsRecyclerviewAdapter
import co.tddl.mylga.model.Feed
import co.tddl.mylga.util.TimeAgoHelper
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

    private var fetched = false

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
        // getYourFeeds()

        fab.visibility = View.GONE

        fab.setOnClickListener {
            val intent = Intent(activity?.applicationContext, ShareActivity::class.java) 
            activity?.startActivity(intent)
        }

        btn_share_first_update.setOnClickListener {
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
        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        var name = "User"
        val docRef = db.collection("users")
                        .whereEqualTo("id", auth.currentUser?.uid.toString())
                        .limit(1)//.document(auth.currentUser?.uid.toString())
        docRef.get()
            .addOnSuccessListener { document ->
                if(document.size() == 1){
                    name = document.first()["name"].toString()
                    text_view_welcome.text = "Hey $name, welcome to your feed"
                }
            }
            .addOnFailureListener { exception ->
                Log.d("Error", "get failed with ", exception)
            }

        if(feeds.isNullOrEmpty() && fetched){
            recyclerView?.visibility = View.GONE
            fab?.visibility = View.GONE
            noUpdateCardView?.visibility = View.VISIBLE
        }else if(feeds.isNullOrEmpty() && !fetched){
            recyclerView?.visibility = View.GONE
            fab?.visibility = View.GONE
            noUpdateCardView?.visibility = View.GONE
        }else{
            recyclerView?.visibility = View.VISIBLE
            fab?.visibility = View.VISIBLE
            noUpdateCardView?.visibility = View.GONE

            recyclerView?.apply {
                layoutManager = LinearLayoutManager(activity)
                adapter = FeedsRecyclerviewAdapter(feeds, context)
            }
        }
    }

    private fun getYourFeeds(){
        val feeds = arrayListOf<Feed>()
        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val timeAgoHelper = TimeAgoHelper()

        db.collection("posts")
            .whereEqualTo("userId", auth.currentUser?.uid.toString())
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val createdAt = timeAgoHelper.getTimeAgo(document["createdAt"].toString().toLong())
                    feeds.add(Feed(document.id, document["imageUrl"].toString(), document["description"].toString(), document["location"].toString(), createdAt, document["pathString"].toString()))
                }
                fetched = true
                initRecyclerView(feeds)
            }
            .addOnFailureListener { exception ->
                Log.w("Error", "Error getting documents: ", exception)
            }
    }
}
