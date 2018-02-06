package com.austinabell8.canihackit.activities

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.austinabell8.canihackit.R
import com.austinabell8.canihackit.model.ResultItem
import com.austinabell8.canihackit.utils.Constants
import com.bumptech.glide.Glide

class ResultDetailsActivity : AppCompatActivity() {

    private lateinit var resultItem: ResultItem
    private lateinit var mNameTxt: TextView
    private lateinit var mDescriptionTxt: TextView
    private lateinit var mLocationTxt: TextView
    private lateinit var mImageView: ImageView
    private lateinit var mLinkButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result_details)

        mNameTxt = findViewById(R.id.tv_name)
        mDescriptionTxt = findViewById(R.id.tv_description)
        mLocationTxt = findViewById(R.id.tv_location)
        mImageView = findViewById(R.id.iv_details)
        mLinkButton = findViewById(R.id.link_button)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        initActionBar()

        val bundle = intent.extras
        if (bundle != null) {
            resultItem = bundle.getParcelable(Constants.INTENT_RESULT)
            mNameTxt.text = resultItem.name
            mDescriptionTxt.text = resultItem.description
            mLocationTxt.text = resultItem.location
            Log.e("Image url:", ""+ resultItem.imageUrl)
            Glide.with(mImageView.context).load(resultItem.imageUrl).into(mImageView)
            if (resultItem.url!=null && resultItem.url!=""){
                mLinkButton.visibility = View.VISIBLE
                mLinkButton.setOnClickListener {
                    val urlString:String = resultItem.url.toString().replace("//", "/")
                    val uris = Uri.parse(urlString)
                    val intents = Intent(Intent.ACTION_VIEW, uris)
                    val b = Bundle()
                    b.putBoolean("new_window", true)
                    intents.putExtras(b)
                    this@ResultDetailsActivity.startActivity(intents)
                }
            }
        }
    }

    private fun initActionBar() {
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.title = "Details"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return false
    }
}
