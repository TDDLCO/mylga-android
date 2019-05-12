package co.tddl.mylga


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import co.tddl.mylga.adapter.UpdateRecyclerviewAdapter
import co.tddl.mylga.model.Update
import co.tddl.mylga.util.SharedPreferenceHelper
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.fragment_around_you.*
import kotlinx.android.synthetic.main.fragment_your_feed.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class AroundYou : Fragment() {

    private val updates = listOf(
        Update(R.drawable.tokyo, "The Tokyo view", "Emeka Mordi", "Tokyo, Japan"),
        Update(R.drawable.barcelona, "Barcelona babyyyyyyy", "Emeka Mordi", "Barcelona")
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
        return inflater.inflate(R.layout.fragment_around_you, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        aroundFeedRecyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = UpdateRecyclerviewAdapter(updates)
        }

        fetchFeedsAroundYou()
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

        val sharedPreferenceHelper = SharedPreferenceHelper(context)
        val lastLocation: String? = sharedPreferenceHelper.getLastLocation()


        if(lastLocation == null){
            noAroundFeedCardView.visibility = View.VISIBLE
            aroundFeedRecyclerView.visibility = View.GONE
            text_view_around_feed_msg.text = getString(R.string.text_enable_location)
            //return
        }

        // do else here - fetch data from firebase and perform magic!
        val db = FirebaseFirestore.getInstance()

        db.collection("posts")
        .whereArrayContains("terms", "$lastLocation")
        .addSnapshotListener(EventListener<QuerySnapshot> { value, e ->
            if (e != null) {
                Log.w("FAILED", "Listen failed.", e)
                return@EventListener
            }

            Log.v("VALUE_SIZE", value?.size().toString())

            //val cities = ArrayList<String>()
            for (doc in value!!) {
                if (doc.getString("location") != null) {
                    Log.v("IN_LAGOS", doc.getString("location"))
                    // cities.add(doc.getString("name")!!)
                }
            }
        })
    }


}
