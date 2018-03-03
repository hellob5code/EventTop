package com.develop.app.eventtop.api

import android.app.Activity
import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

class ApiController(private val service: ServiceInterface) : ServiceInterface {

    override fun postJsonArray(path: String, params: JSONArray, completionHandler: (response: JSONArray?) -> Unit) {
        service.postJsonArray(path, params, completionHandler)
    }

    override fun getJsonArray(path: String, completionHandler: (response: JSONArray?) -> Unit) {
        service.getJsonArray(path, completionHandler)
    }

    override fun postJsonObject(path: String, params: JSONObject, completionHandler: (response: JSONObject?) -> Unit) {
        service.postJsonObject(path, params, completionHandler)
    }

    override fun getJsonObject(path: String, completionHandler: (response: JSONObject?) -> Unit) {
        service.getJsonObject(path, completionHandler)
    }

    override fun multipart(path: String, context: Context, activity: Activity, params: JSONObject, completionHandler: (response: String?) -> Unit) {
        service.multipart(path, context, activity, params, completionHandler)
    }
}
