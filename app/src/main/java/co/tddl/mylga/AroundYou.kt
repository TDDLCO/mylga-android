package co.tddl.mylga


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import co.tddl.mylga.adapter.UpdateRecyclerviewAdapter
import co.tddl.mylga.model.Update
import kotlinx.android.synthetic.main.fragment_around_you.*


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
        recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = UpdateRecyclerviewAdapter(updates)
        }
    }

    companion object {
        fun newInstance(): YourFeed = YourFeed()
    }


}
