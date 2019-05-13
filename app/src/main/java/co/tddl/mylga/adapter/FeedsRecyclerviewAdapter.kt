package co.tddl.mylga.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import co.tddl.mylga.R
import co.tddl.mylga.model.Feed
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso

class FeedsRecyclerviewAdapter(private val list: MutableList<Feed>, private val context: Context)
    : RecyclerView.Adapter<FeedsRecyclerviewAdapter.FeedViewHolder>() {

    private val TAG = "FeedRecyclerviewAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return FeedViewHolder(inflater, parent, context)
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        val feed: Feed = list[position]
        holder.bind(feed, position)
        holder.mImageViewSeeMore?.setOnClickListener { showMenu(position, holder) }
    }

    override fun getItemCount(): Int = list.size

    fun showMenu(position: Int, holder: FeedViewHolder){
        val popup = PopupMenu(context, holder.itemView.findViewById(R.id.image_view_see_more))
        popup.inflate(R.menu.feeds_menu)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.delete -> { deleteFeed(position) }
            }
            false
        }
        popup.show()
    }

    private fun deleteFeed(position: Int){
        val feed: Feed = list[position]
        list.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position,list.size)
        deleteDbEntry(feed)
    }

    private fun deleteDbEntry(feed: Feed){
        val db = FirebaseFirestore.getInstance()

        db.collection("posts").document(feed.id)
            .delete()
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully deleted!")
                // delete image from storage
                deleteImageFromStorage(feed.image)
            }
            .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
    }

    private fun deleteImageFromStorage(imageUrl: String){
        var storageReference  = FirebaseStorage.getInstance().reference
        val feedImageRef = storageReference.child(imageUrl)

        // Delete the file
        feedImageRef.delete().addOnSuccessListener {
            Log.d(TAG, "Image successfully deleted!")
        }.addOnFailureListener { e -> Log.w(TAG, "Error deleting image", e) }
    }


    class FeedViewHolder(inflater: LayoutInflater, parent: ViewGroup, context: Context) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.single_feed_layout, parent, false)) {

        private var context: Context? = null
        private var mImageView: ImageView? = null
        private var mDescriptionView: TextView? = null
        private var mLocationView: TextView? = null
        var mImageViewSeeMore: ImageView? = null

        init {
            this.context = context
            mImageView = itemView.findViewById(R.id.image_view_feed)
            mDescriptionView = itemView.findViewById(R.id.text_view_description)
            mLocationView = itemView.findViewById(R.id.text_view_location)
            mImageViewSeeMore = itemView.findViewById(R.id.image_view_see_more)
        }

        fun bind(feed: Feed, position: Int) {
            Picasso.get()
                .load(feed.image)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(mImageView)
            mDescriptionView?.text = feed.description
            mLocationView?.text = feed.location
        }
    }
}