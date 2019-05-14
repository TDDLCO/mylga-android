package co.tddl.mylga

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import co.tddl.mylga.adapter.UpdateRecyclerviewAdapter
import co.tddl.mylga.model.Update
import co.tddl.mylga.util.SharedPreferenceHelper
import co.tddl.mylga.util.TimeAgoHelper
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.fragment_around_you.*



class AroundYou : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_around_you, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
        // fetchFeedsAroundYou()
    }

    override fun onResume() {
        super.onResume()
        fetchFeedsAroundYou()
    }

    companion object {
        fun newInstance(): YourFeed = YourFeed()
    }

    private fun initRecyclerView(){

    }

    private fun fetchFeedsAroundYou(){
        //1. Check pref to see if location exists
        //2. If location is null, show card, hide recyclerview and change text appropriately

        val updates = arrayListOf<Update>()
        val sharedPreferenceHelper = SharedPreferenceHelper(context)
        val lastLocation: String? = sharedPreferenceHelper.getLastLocation()

        if(lastLocation == null){
            noAroundFeedCardView.visibility = View.VISIBLE
            aroundFeedRecyclerView.visibility = View.GONE
            text_view_around_feed_msg.text = getString(R.string.text_enable_location)
            return
        }

        // do else here - fetch data from firebase and perform magic!
        val db = FirebaseFirestore.getInstance()
        val timeAgoHelper = TimeAgoHelper()

        db.collection("posts")
        .whereArrayContains("terms", "$lastLocation")
        .addSnapshotListener(EventListener<QuerySnapshot> { value, e ->
            if (e != null) {
                Log.w("FAILED", "Listen failed.", e)
                return@EventListener
            }

            for (document in value!!) {
                Log.d("Result", "${document.id} => ${document.data}")
                val createdAt = timeAgoHelper.getTimeAgo(document["createdAt"].toString().toLong())
                updates.add(Update(document["imageUrl"].toString(), document["description"].toString(), document["userId"].toString(), document["location"].toString(), createdAt))
            }

            aroundFeedRecyclerView.apply {
                layoutManager = LinearLayoutManager(activity)
                adapter = UpdateRecyclerviewAdapter(updates)
            }
        })
    }


}
