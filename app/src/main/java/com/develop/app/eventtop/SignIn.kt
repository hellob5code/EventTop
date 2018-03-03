package com.develop.app.eventtop

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.Toolbar
import android.text.InputType
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.*
import android.widget.*
import com.develop.app.eventtop.api.ApiController
import com.develop.app.eventtop.api.BaseUrl
import com.develop.app.eventtop.api.VolleyService
import com.develop.app.eventtop.utils.ImageUtility
import com.develop.app.eventtop.utils.ProgressDialog
import com.facebook.*
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog
import org.json.JSONObject
import java.util.*

class SignIn : AppCompatActivity(), View.OnTouchListener {

    private lateinit var password: AutoCompleteTextView
    private var isClicked = false
    private var type = 1
    private lateinit var path: String
    private lateinit var callbackManager: CallbackManager
    private val TAG = "SignIn"
    private lateinit var finalParams: JSONObject
    private var limit = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        FacebookSdk.sdkInitialize(applicationContext)
        AppEventsLogger.activateApp(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)

        callbackManager = CallbackManager.Factory.create()

        finalParams = JSONObject()

        val title: TextView = findViewById(R.id.title)
        val typeface = Typeface.createFromAsset(assets, "STV_0.ttf")
        title.typeface = typeface
        title.text = "تسجيل دخول"

        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        val userName: AutoCompleteTextView = findViewById(R.id.autoCompleteTextView)
        password = findViewById(R.id.autoCompleteTextView2)

        password.setOnTouchListener(this)

        val radioUser: RadioButton = findViewById(R.id.radio_user)
        radioUser.typeface = typeface
        val radioCompany: RadioButton = findViewById(R.id.radio_company)
        radioCompany.typeface = typeface

        radioUser.setOnClickListener({ _ ->
            if (radioUser.isChecked) {
                type = 1
                radioUser.isChecked = true
                radioCompany.isChecked = false
            } else {
                type = 2
                radioUser.isChecked = false
                radioCompany.isChecked = true
            }
        })

        radioCompany.setOnClickListener({ _ ->
            if (radioCompany.isChecked) {
                type= 2
                radioUser.isChecked = false
                radioCompany.isChecked = true
            } else {
                type = 1
                radioUser.isChecked = true
                radioCompany.isChecked = false
            }
        })

        val viewGroup = findViewById<ViewGroup>(R.id.container)
        TextModifier.replaceTypeface(viewGroup, this)

        val signIn: Button = findViewById(R.id.sign_in)
        signIn.setOnClickListener({ view ->
            val userNameTxt = userName.text.toString().trim()
            val passwordTxt = password.text.toString().trim()

            for (i in 0 until viewGroup.childCount) {
                val v = viewGroup.getChildAt(i)
                if (v is AutoCompleteTextView) {
                    val childText = v.text.trim()

                    if (childText.isEmpty()) {
                        val message = v.hint.toString()
                        val mSnack: Snackbar = Snackbar.make(view, message + " مطلوب ", Snackbar.LENGTH_SHORT)
                        TextModifier.replaceTypeface(mSnack, this)
                        return@setOnClickListener
                    }
                }
            }

            val service = VolleyService()
            val api = ApiController(service)

            val params = JSONObject()
            params.put("username", userNameTxt)
            params.put("password", passwordTxt)

            val progress = ProgressDialog(this, this)
            progress.setMessage("جاري تسجيل الدخول")

            path = if (type == 1) {
                "/auth/user/login"
            } else {
                "/auth/user/company"
            }

            Log.d("pppp", path)

            api.postJsonObject(BaseUrl().url + path, params, completionHandler = { response ->
                if (response!!.getString("status") == "1") {
                    progress.dismiss()

                    val preferences = getSharedPreferences("token_preferences", Context.MODE_PRIVATE)
                    val editor: SharedPreferences.Editor = preferences.edit()
                    val obj = JSONObject(response.getString("data"))
                    editor.putString("login_token", obj.getString("login_token"))
                    editor.putString("is_loggedin", obj.getString("is_loggedin"))
                    editor.putString("user_id", obj.getString("id"))
                    editor.apply()

                    SweetAlertDialog(this@SignIn, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText(response.getString("message"))
                            .setConfirmClickListener { sweetAlertDialog ->
                                sweetAlertDialog.cancel()
                                startActivity(Intent(this, MainActivity::class.java))
                            }
                            .show()
                } else {
                    progress.dismiss()
                    val mSnack: Snackbar = Snackbar.make(view, response.getString("message"), Snackbar.LENGTH_SHORT)
                    TextModifier.replaceTypeface(mSnack, this)
                }
            })
        })

        val faceLoginButton: ImageButton = findViewById(R.id.imageButton)
        faceLoginButton.setOnClickListener({ _ ->
            loginWithFacebook()
        })

        val register1: Button = findViewById(R.id.register_user)
        register1.typeface = typeface
        register1.setOnClickListener({ _ ->
            startActivity(Intent(this, SignUp::class.java))
        })

        val register2: Button = findViewById(R.id.register_comp)
        register2.typeface = typeface
        register2.setOnClickListener({ _ ->
            startActivity(Intent(this, SignUpCompany::class.java))
        })
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            startActivity(Intent(this, MainActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        startActivity(Intent(this, MainActivity::class.java))
        super.onBackPressed()
    }

    override fun onTouch(view: View?, event: MotionEvent?): Boolean {
        // DRAWABLE_LEFT = 0
        // DRAWABLE_TOP = 1
        // DRAWABLE_RIGHT = 2
        // DRAWABLE_BOTTOM = 3

        if (event?.action == MotionEvent.ACTION_UP) {
            val diff = password.compoundDrawables[0].bounds.width() + 40
            if (event.rawX <= diff) {
                Log.d("event", "click " + event.rawX + " " + diff)
                view?.performClick()
                isClicked = !isClicked

                val typeface = Typeface.createFromAsset(assets, "STV_0.ttf")
                password.typeface = typeface

                if (isClicked)  {
                    val drawable = applicationContext.resources.getDrawable(R.drawable.ic_visibility_black_24dp)
                    password.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
                    password.inputType = InputType.TYPE_CLASS_TEXT
                    password.transformationMethod = HideReturnsTransformationMethod.getInstance()
                } else {
                    val drawable = applicationContext.resources.getDrawable(R.drawable.ic_visibility_off_black_24dp)
                    password.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
                    password.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
                    password.transformationMethod = PasswordTransformationMethod.getInstance()
                }

                return true
            }
        }
        return false
    }

    private fun loginWithFacebook() {
        val permissions = arrayOf("user_photos", "email", "public_profile", "user_location")

        LoginManager.getInstance().logInWithReadPermissions(this, permissions.toList())
        LoginManager.getInstance().logInWithPublishPermissions(this, Collections.singletonList("publish_actions"))

        val facebookCallback = object: FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                Log.d(TAG, result!!.accessToken.token + " - " + result.accessToken.userId)
                getUserInfo(result.accessToken)
            }

            override fun onCancel() {
                Log.d(TAG, "Cancelled")
            }

            override fun onError(error: FacebookException?) {
                Log.d(TAG, error!!.message)
            }
        }

        LoginManager.getInstance().registerCallback(callbackManager, facebookCallback)
    }

    private inner class ParamsService: FacebookParams {
        override fun getParams(params: JSONObject?) {
            if (limit == 1) {
                val avatar = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ImageUtility.downloadImageFromUrl(params!!.getString("avatar"), applicationContext)
                } else {
                    TODO("VERSION.SDK_INT < JELLY_BEAN")
                }

                finalParams.put("account_type", params.getString("account_type"))
                finalParams.put("full_name", params.getString("full_name"))
                finalParams.put("user_name", params.getString("user_name"))
                finalParams.put("password", params.getString("password"))
                finalParams.put("email", params.getString("email"))
                finalParams.put("phone_number", params.getString("phone_number"))
                finalParams.put("city", params.getString("city"))
                finalParams.put("gender", params.getString("gender"))
                finalParams.put("avatar", avatar)

                val service = VolleyService()
                val api = ApiController(service)

                val progress = ProgressDialog(this@SignIn, this@SignIn)
                progress.setMessage("جاري تسجيل الدخول")

                api.postJsonObject(BaseUrl().url + "/auth/user/login", finalParams, completionHandler = {response ->
                    Log.d("response_fb", response.toString())

                    if (response!!.getString("status") == "1") {
                        progress.dismiss()

                        val preferences = getSharedPreferences("token_preferences", Context.MODE_PRIVATE)
                        val editor: SharedPreferences.Editor = preferences.edit()
                        val obj = JSONObject(response.getString("data"))
                        editor.putString("login_token", obj.getString("login_token"))
                        editor.putString("is_loggedin", obj.getString("is_loggedin"))
                        editor.putString("user_id", obj.getString("id"))
                        editor.apply()

                        SweetAlertDialog(this@SignIn, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText(response.getString("message"))
                                .setConfirmClickListener { sweetAlertDialog ->
                                    sweetAlertDialog.cancel()
                                    startActivity(Intent(this@SignIn, MainActivity::class.java))
                                }
                                .show()
                    }
                })

                limit++
            }
        }
    }

    private fun getUserInfo(accessToken: AccessToken?) {
        val newParams = JSONObject()

        val graphCallback = GraphRequest.GraphJSONObjectCallback(function = {obj: JSONObject?, response: GraphResponse? ->
            Log.d("response", response.toString())
            val picture = JSONObject(obj!!.getString("picture"))
            val data = JSONObject(picture.getString("data"))
            val location = JSONObject(obj.getString("location"))

            newParams.put("account_type", "1")
            newParams.put("full_name", obj.getString("name"))
            newParams.put("user_name", obj.getString("email").substringBeforeLast('.'))
            newParams.put("password", UUID.randomUUID().toString())
            newParams.put("email", obj.getString("email"))
            newParams.put("phone_number", "0")
            newParams.put("city", location.getString("id"))

            if (obj.getString("gender") == "male") {
                newParams.put("gender", "1")
            } else {
                newParams.put("gender", "2")
            }

            newParams.put("avatar", data.getString("url"))

            val facebookParams = ParamsService()
            val controller = ParamsController(facebookParams)
            controller.getInfo(newParams)
        })

        val request: GraphRequest = GraphRequest.newMeRequest(accessToken, graphCallback)
        val params = Bundle()
        params.putString("fields", "id, name, email, picture, gender, location")
        request.parameters = params
        request.executeAsync()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun logoutFromFacebook() {
        if (AccessToken.getCurrentAccessToken() != null && Profile.getCurrentProfile() != null) {
            LoginManager.getInstance().logOut()
            Log.d(TAG, "Log out")
        }
    }

    private inner class ParamsController(var facebookParams: FacebookParams) {
        fun getInfo(params: JSONObject?) {
            facebookParams.getParams(params)
        }
    }
}
