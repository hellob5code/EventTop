package com.develop.app.eventtop

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.develop.app.eventtop.api.ApiController
import com.develop.app.eventtop.api.BaseUrl
import com.develop.app.eventtop.api.VolleyService
import com.squareup.picasso.Picasso
import org.json.JSONObject

class ProfileFragment : Fragment() {

    private val TAG = "ProfileFragment"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewGroup = view.findViewById<ViewGroup>(R.id.container)
        TextModifier.replaceTypeface(viewGroup, context)

        val typeface = Typeface.createFromAsset(context!!.assets, "STV_0.ttf")

        val title1: TextView = view.findViewById(R.id.title1)
        title1.typeface = typeface

        val title11: TextView = view.findViewById(R.id.title11)
        title11.typeface = typeface
        val title12: TextView = view.findViewById(R.id.title12)
        title12.typeface = typeface
        val title21: TextView = view.findViewById(R.id.title21)
        title21.typeface = typeface
        val title22: TextView = view.findViewById(R.id.title22)
        title22.typeface = typeface
        val title31: TextView = view.findViewById(R.id.title31)
        title31.typeface = typeface
        val title32: TextView = view.findViewById(R.id.title32)
        title32.typeface = typeface
        val title41: TextView = view.findViewById(R.id.title41)
        title41.typeface = typeface
        val title42: TextView = view.findViewById(R.id.title42)
        title42.typeface = typeface

        val userPhoto: ImageView = view.findViewById(R.id.user_photo)

        val preferences = activity!!.getSharedPreferences("token_preferences", Context.MODE_PRIVATE)
        val token = preferences.getString("login_token", "0")

        val service = VolleyService()
        val api = ApiController(service)

        val params = JSONObject()
        params.put("login_token", token)

        api.postJsonObject(BaseUrl().url + "/auth/user/me", params, completionHandler = {response ->
            Log.d(TAG, response.toString())

            title12.text = response!!.getString("full_name")
            title22.text = response.getString("user_name")
            title32.text = response.getString("email")
            title42.text = response.getString("phone_number")

            Picasso.with(context).load("http://eventop.khalid.pro/" + response.getString("avatar")).resize(300, 300).centerCrop().into(userPhoto)
        })
    }
}
