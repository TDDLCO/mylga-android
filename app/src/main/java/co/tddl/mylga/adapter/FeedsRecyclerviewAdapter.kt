package co.tddl.mylga.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import co.tddl.mylga.R
import co.tddl.mylga.model.Feed
import com.squareup.picasso.Picasso

class FeedsRecyclerviewAdapter(private val list: List<Feed>, private val context: Context)
    : RecyclerView.Adapter<FeedsRecyclerviewAdapter.FeedViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return FeedViewHolder(inflater, parent, context)
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        val feed: Feed = list[position]
        holder.bind(feed, position)
    }

    override fun getItemCount(): Int = list.size


    class FeedViewHolder(inflater: LayoutInflater, parent: ViewGroup, context: Context) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.single_feed_layout, parent, false)) {

        private var context: Context? = null
        private var mImageView: ImageView? = null
        private var mDescriptionView: TextView? = null
        private var mLocationView: TextView? = null
        private var mImageViewSeeMore: ImageView? = null

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
            mImageViewSeeMore?.setOnClickListener { showMenu(position) }
        }

        private fun showMenu(position: Int){
            //creating a popup menu
            val popup = PopupMenu(context!!, itemView.findViewById(R.id.image_view_see_more))
            //inflating menu from xml resource
            popup.inflate(R.menu.feeds_menu)
            //adding click listener
            popup.setOnMenuItemClickListener { item ->
                when (item.getItemId()) {
                    R.id.delete -> { deleteFeed(position) }
                }
                false
            }
            //displaying the popup
            popup.show()
        }

        private fun deleteFeed(position: Int){

        }
    }
}