package co.tddl.mylga.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.tddl.mylga.R
import co.tddl.mylga.model.Update
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class UpdateRecyclerviewAdapter(private val list: List<Update>)
    : RecyclerView.Adapter<UpdateRecyclerviewAdapter.FeedViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return FeedViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        val update: Update = list[position]
        holder.bind(update)
    }

    override fun getItemCount(): Int = list.size


    class FeedViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.single_update_layout, parent, false)) {

        private val TAG = "UpdateRecyclerAdapter"

        private var mImageView: ImageView? = null
        private var mDescriptionView: TextView? = null
        private var mLocationView: TextView? = null
        private var mAuthorView: TextView? = null
        private var mTimeAgoView: TextView? = null

        init {
            mImageView = itemView.findViewById(R.id.image_view_update)
            mDescriptionView = itemView.findViewById(R.id.text_view_description)
            mLocationView = itemView.findViewById(R.id.text_view_location)
            mAuthorView = itemView.findViewById(R.id.text_view_author)
            mTimeAgoView = itemView.findViewById(R.id.text_view_time_ago)
        }

        fun bind(update: Update) {
            val db = FirebaseFirestore.getInstance()

            Picasso.get()
                .load(update.image)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(mImageView)
            mDescriptionView?.text = update.description
            mLocationView?.text = update.location
            mTimeAgoView?.text = update.createdAt

            db.collection("users")
                .whereEqualTo("id", update.author)
                .get()
                .addOnSuccessListener { documents ->
                    val user = documents.first()
                    mAuthorView?.text = user["name"].toString()
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        }
    }
}