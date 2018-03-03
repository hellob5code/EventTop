package com.develop.app.eventtop

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.AppCompatSpinner
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

class SignUp : AppCompatActivity(), View.OnTouchListener {

    private lateinit var password: AutoCompleteTextView
    private var isClicked = false
    private var gender = 1
    lateinit var spinner: Spinner
    private var city = 0

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)

        val title: TextView = findViewById(R.id.title)
        val typeface = Typeface.createFromAsset(assets, "STV_0.ttf")
        title.typeface = typeface
        title.text = "مستخدم جديد"

        val cityTitle: TextView = findViewById(R.id.city_title)
        cityTitle.typeface = typeface
        val maleRadio: RadioButton = findViewById(R.id.radio_male)
        maleRadio.typeface = typeface
        val femaleRadio: RadioButton = findViewById(R.id.radio_female)
        femaleRadio.typeface = typeface

        maleRadio.setOnClickListener({ view ->
            if (maleRadio.isChecked) {
                gender = 1
                maleRadio.isChecked = true
                femaleRadio.isChecked = false
            } else {
                gender = 2
                maleRadio.isChecked = false
                femaleRadio.isChecked = true
            }
        })

        femaleRadio.setOnClickListener({ view ->
            if (femaleRadio.isChecked) {
                gender = 2
                maleRadio.isChecked = false
                femaleRadio.isChecked = true
            } else {
                gender = 1
                maleRadio.isChecked = true
                femaleRadio.isChecked = false
            }
        })

        val service = VolleyService()
        val api = ApiController(service)

        val cityList: MutableList<City> = ArrayList()

        spinner = findViewById(R.id.spinner_city)

        api.getJsonArray(BaseUrl().url + "/cities/all", completionHandler = { response ->
            if (response != null) {
                Log.d("cities", response.toString())

                (0 until response.length())
                        .map { response.getJSONObject(it) }
                        .mapTo(cityList) { City(it.getString("name"), it.getString("id").toInt()) }

                val spinnerAdp = SpinnerCityAdapter(this, android.R.layout.simple_spinner_item, cityList)
                spinnerAdp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = spinnerAdp
            }
        })

        spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                city = cityList.get(position).value
                // Toast.makeText(applicationContext, city.toString(), Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                city = cityList.get(0).value
                // Toast.makeText(applicationContext, city.toString(), Toast.LENGTH_SHORT).show()
            }
        }

        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        val viewGroup = findViewById<ViewGroup>(R.id.container)
        TextModifier.replaceTypeface(viewGroup, this)

        val signUp: Button = findViewById(R.id.sign_up)
        val fullName: AutoCompleteTextView = findViewById(R.id.autoCompleteTextView5)
        val userName: AutoCompleteTextView = findViewById(R.id.autoCompleteTextView)
        password = findViewById(R.id.autoCompleteTextView2)
        val email: AutoCompleteTextView = findViewById(R.id.autoCompleteTextView3)
        val phone: AutoCompleteTextView = findViewById(R.id.autoCompleteTextView4)

        password.setOnTouchListener(this)

        signUp.setOnClickListener { view ->
            val fullNameTxt = fullName.text.toString().trim()
            val userNameTxt = userName.text.toString().trim()
            val passwordTxt = password.text!!.toString().trim()
            val emailTxt = email.text.toString().trim()
            val phoneTxt = phone.text.toString().trim()

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

            val preferences = getSharedPreferences("signup_preferences", Context.MODE_PRIVATE)
            var editor: SharedPreferences.Editor = preferences.edit()
            editor.putString("fullNameTxt", fullNameTxt)
            editor.putString("userNameTxt", userNameTxt)
            editor.putString("passwordTxt", passwordTxt)
            editor.putString("emailTxt", emailTxt)
            editor.putString("phoneTxt", phoneTxt)
            editor.putString("gender", gender.toString())
            editor.putString("city", city.toString())
            editor.apply()

            val preferences2 = getSharedPreferences("window_preferences", Context.MODE_PRIVATE)
            editor = preferences2.edit()
            editor.putBoolean("isFromGallery", false)
            editor.apply()

            val preferences3 = getSharedPreferences("account_type_preferences", Context.MODE_PRIVATE)
            editor = preferences3.edit()
            editor.putInt("account_type", 1)
            editor.apply()

            val intent = Intent(this, ProgressSignUp::class.java)
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            startActivity(Intent(this, SignIn::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, SignIn::class.java))
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
}
