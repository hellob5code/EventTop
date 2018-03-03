package com.develop.app.eventtop

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.develop.app.eventtop.api.ApiController
import com.develop.app.eventtop.api.BaseUrl
import com.develop.app.eventtop.api.VolleyService
import com.develop.app.eventtop.utils.TimerViewPager
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.slider_layout.view.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class HomeFragment : Fragment() {

    private lateinit var list: MutableList<EventModel>
    private lateinit var viewPager: ViewPager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    // TODO: View has been created
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewGroup = view.findViewById<ViewGroup>(R.id.container)
        TextModifier.replaceTypeface(viewGroup, context)

        val recyclerView: RecyclerView = view.findViewById(R.id.first_slider)
        recyclerView.setHasFixedSize(true)
        recyclerView.isDrawingCacheEnabled = true
        recyclerView.setItemViewCacheSize(20)
        recyclerView.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_AUTO

        list = ArrayList()
        viewPager = view.findViewById(R.id.main_image)
        val viewPagerAdapter = Slider(context, list)

        val eventsList1: MutableList<Event> = ArrayList()

        val service = VolleyService()
        val api = ApiController(service)

        api.getJsonObject(BaseUrl().url + "/slider/home", completionHandler = {response ->
            val arr = JSONArray(response!!.getString("data"))

            for (i in 0 until (arr.length())) {
                val event = EventModel(
                        arr.getJSONObject(i).getString("id").toInt(),
                        arr.getJSONObject(i).getString("name"),
                        arr.getJSONObject(i).getString("username"),
                        arr.getJSONObject(i).getString("price").toFloat(),
                        arr.getJSONObject(i).getString("location"),
                        arr.getJSONObject(i).getString("city_id").toInt(),
                        arr.getJSONObject(i).getString("avatar"),
                        arr.getJSONObject(i).getString("cover_avatar"),
                        arr.getJSONObject(i).getString("created_by").toInt(),
                        arr.getJSONObject(i).getString("hosted_by").toInt(),
                        arr.getJSONObject(i).getString("description"),
                        arr.getJSONObject(i).getString("sponsers"),
                        arr.getJSONObject(i).getString("register_link"),
                        arr.getJSONObject(i).getString("views").toInt(),
                        arr.getJSONObject(i).getString("slider_type"),
                        arr.getJSONObject(i).getString("slider_order").toInt(),
                        arr.getJSONObject(i).getString("is_hidden").toInt(),
                        arr.getJSONObject(i).getString("start_date"),
                        arr.getJSONObject(i).getString("end_date"),
                        arr.getJSONObject(i).getString("start_time"),
                        arr.getJSONObject(i).getString("end_time"),
                        arr.getJSONObject(i).getString("created_at"),
                        arr.getJSONObject(i).getString("updated_at"))

                list.add(event)

                viewPager.adapter = viewPagerAdapter
                viewPager.offscreenPageLimit = list.size
                viewPagerAdapter.notifyDataSetChanged()

                if (viewPager.offscreenPageLimit == 4) {
                    val timer = Timer()
                    timer.scheduleAtFixedRate(TimerViewPager(activity, viewPager, viewPager.offscreenPageLimit - 1), 7000, 6000)
                }
            }
        })

        api.getJsonObject(BaseUrl().url + "/events/next-month") { response ->
            val adapter = Adapter(eventsList1)

            try {
                // progressBar.visibility = View.GONE
                val nextMonth = response!!.getString("events")
                val eventsObj = JSONObject(nextMonth)
                val arr = JSONArray(eventsObj.getString("data"))
                for (i in 0 until (arr.length())) {
                    val event = Event(arr.getJSONObject(i).getString("name"),
                            arr.getJSONObject(i).getString("start_date"),
                            arr.getJSONObject(i).getString("end_date"),
                            arr.getJSONObject(i).getString("location"),
                            arr.getJSONObject(i).getString("avatar"))
                    eventsList1.add(event)
                    adapter.notifyDataSetChanged()
                }

                val gridLayoutManager = GridLayoutManager(context, eventsList1.size, GridLayoutManager.HORIZONTAL, false)
                gridLayoutManager.orientation = GridLayoutManager.VERTICAL
                recyclerView.layoutManager = gridLayoutManager
                recyclerView.adapter = adapter

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
            val view = inflater?.inflate(R.layout.slider_layout, parent, false)
            return ViewHolder(view!!)
        }

        override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
            holder!!.bindItems(eventsList[position])
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            @SuppressLint("SetTextI18n")
            fun bindItems(event: Event) {
                itemView.title.text = event.name
                itemView.sub_title.text = " " + event.place + " - " + event.startDate

                if (event.image != "") {
                    Picasso.with(context).load(event.image).into(itemView.imageview)
                }
            }
        }
    }

    inner class Event(name: String, startDate: String, endDate: String, place: String, image: String) {
        val name: String? = name
        val startDate: String? = startDate
        val endDate: String? = endDate
        val place: String? = place
        val image: String? = image
    }
}
