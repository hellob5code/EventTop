package com.develop.app.eventtop

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import com.develop.app.eventtop.api.ApiController
import com.develop.app.eventtop.api.BaseUrl
import com.develop.app.eventtop.api.VolleyService
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.all_events.view.*
import org.json.JSONArray

class AllEventsFragment : Fragment() {

    private var recyclerView: RecyclerView? = null
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_events, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.all_events)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.isDrawingCacheEnabled = true
        recyclerView?.setItemViewCacheSize(20)
        recyclerView?.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH
        recyclerView?.layoutManager = LinearLayoutManager(context)

        progressBar = view.findViewById(R.id.progressBar)

        val eventsList: MutableList<Event> = ArrayList()

        val service = VolleyService()
        val api = ApiController(service)
        api.getJsonObject(BaseUrl().url + "/events/all") { response ->
            val adapter = Adapter(eventsList)
            recyclerView?.adapter = adapter

            try {
                progressBar.visibility = View.GONE
                val arr = JSONArray(response?.getString("data"))
                for (i in 0 until (arr.length())) {
                    val event = Event(arr.getJSONObject(i).getString("name"),
                            arr.getJSONObject(i).getString("start_time"),
                            arr.getJSONObject(i).getString("end_time"),
                            arr.getJSONObject(i).getString("location"),
                            arr.getJSONObject(i).getString("avatar"))
                    eventsList.add(event)
                    adapter.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    inner class Adapter(private val eventsList: MutableList<Event>) : RecyclerView.Adapter<Adapter.ViewHolder>() {

        override fun getItemCount(): Int {
            return eventsList.size
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
            val inflater = LayoutInflater.from(activity)
            val view = inflater?.inflate(R.layout.all_events, parent, false)
            return ViewHolder(view!!)
        }

        override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
            holder!!.bindItems(eventsList[position])
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            @SuppressLint("SetTextI18n")
            fun bindItems(event: Event) {
                itemView.event_name.text = event.name
                itemView.event_time.text = " " + event.startTime + " - " + event.endTime
                itemView.event_place.text = " " + event.place

                if (event.image != "") {
                    Picasso.with(context).load(event.image).into(itemView.event_image)
                }
            }
        }
    }

    inner class Event(name: String, startTime: String, endTime: String, place: String, image: String) {
        val name: String? = name
        val startTime: String? = startTime
        val endTime: String? = endTime
        val place: String? = place
        val image: String? = image
    }
}
