package co.tddl.mylga.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.tddl.mylga.R
import co.tddl.mylga.model.Feed
import com.squareup.picasso.Picasso

class FeedsRecyclerviewAdapter(private val list: List<Feed>)
    : RecyclerView.Adapter<FeedsRecyclerviewAdapter.FeedViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return FeedViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        val feed: Feed = list[position]
        holder.bind(feed)
    }

    override fun getItemCount(): Int = list.size


    class FeedViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.single_feed_layout, parent, false)) {

        private var mImageView: ImageView? = null
        private var mDescriptionView: TextView? = null
        private var mLocationView: TextView? = null


        init {
            mImageView = itemView.findViewById(R.id.image_view_feed)
            mDescriptionView = itemView.findViewById(R.id.text_view_description)
            mLocationView = itemView.findViewById(R.id.text_view_location)
        }

        fun bind(feed: Feed) {
            // mImageView?.setImageResource(feed.image)
            Picasso.get()
                .load(feed.image)
                .placeholder(R.drawable.tokyo)
                .error(R.drawable.barcelona)
                .into(mImageView)
            mDescriptionView?.text = feed.description
            mLocationView?.text = feed.location
        }
    }
}