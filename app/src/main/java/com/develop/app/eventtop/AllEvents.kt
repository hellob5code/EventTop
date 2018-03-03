package com.develop.app.eventtop

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.all_events.view.*

class AllEvents : AppCompatActivity() {

    private val recyclerView: RecyclerView? = null
    private val eventsList: ArrayList<Event>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_events)
        recyclerView?.setHasFixedSize(true)
        val itemDecorator: RecyclerView.ItemDecoration? = null
        recyclerView?.addItemDecoration(itemDecorator)
        recyclerView?.setItemViewCacheSize(20)
        recyclerView?.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_AUTO
        recyclerView?.layoutManager = LinearLayoutManager(this)
        recyclerView?.adapter = Adapter()
    }

    private inner class Adapter : RecyclerView.Adapter<ViewHolder>() {
        override fun getItemCount(): Int {
            return eventsList!!.size
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
            val inflater: LayoutInflater? = null
            val view = inflater?.inflate(R.layout.all_events, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
            holder!!.bindItems(eventsList!![position])
        }
    }

    private inner class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(event: Event) {
            itemView.event_name.text = event.name
            Picasso.with(applicationContext).load(event.image).fit().into(itemView.event_image)
        }
    }

    private inner class Event(name: String, image: String) {
        val name: String? = name
        val image: String? = image
    }
}
