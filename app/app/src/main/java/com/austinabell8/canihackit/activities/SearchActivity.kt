package com.austinabell8.canihackit.activities

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.ImageView
import android.support.v4.util.Pair
import android.util.Log
import android.view.MenuItem
import android.view.Window
import android.widget.ProgressBar
import android.widget.TextView
import com.austinabell8.canihackit.R
import com.austinabell8.canihackit.adapters.SearchRecyclerAdapter
import com.austinabell8.canihackit.model.IdeaJSON
import com.austinabell8.canihackit.model.Request
import com.austinabell8.canihackit.model.ResultItem
import com.austinabell8.canihackit.model.Sites
import com.austinabell8.canihackit.utils.Constants
import com.austinabell8.canihackit.utils.SearchListListener
import com.github.kittinunf.fuel.*
import com.github.kittinunf.result.getAs
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.json.JSONObject


class SearchActivity : AppCompatActivity() {

    private lateinit var mName: String
    private lateinit var mDescription: String
    private val mResults: MutableList<ResultItem> = mutableListOf()

//    private val url: String = "https://doesitexist.azurewebsites.net/api/HttpTriggerPython31?code=7To0yqYQ1r5scPz1Zs8WlwNKIzqFMdBVsPYBBtdulcyIVywiXCW7jw=="
    private val url:String = "http://52.242.38.104:5000/score"

    private lateinit var mResultsRecyclerView: RecyclerView
    private lateinit var mSpinner: ProgressBar
    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var mSearchAdapter: SearchRecyclerAdapter
    private lateinit var mRecyclerViewListener: SearchListListener
    private lateinit var mSearchOptions: MutableList<String>
    private lateinit var mAsyncTask: RetrieveResultsAsync

//    private lateinit var mAsyncTask: RetrieveResultsAsync

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        mResultsRecyclerView = findViewById(R.id.rvResults)
        mSpinner = findViewById(R.id.progressBarSearch)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        initActionBar()

        val bundle = intent.extras
        if (bundle != null) {
            mName = bundle.getString(Constants.INTENT_NAME)
            mDescription = bundle.getString(Constants.INTENT_DESCRIPTION)

            mSearchOptions = mutableListOf("f", "f", "f", "f")
            val boolArray = bundle.getBooleanArray(Constants.INTENT_SITES)
            if (boolArray[0]){
                mSearchOptions[0]="t"
            }
            if (boolArray[1]){
                mSearchOptions[1]="t"
            }
            if (boolArray[2]){
                mSearchOptions[2]="t"
            }
            if (boolArray[3]){
                mSearchOptions[3]="t"
            }
        }

        mRecyclerViewListener = object : SearchListListener {
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun recyclerViewListClicked(v: View, position: Int) {
                val transitionIntent = newIntent(this@SearchActivity, position)
                val placeImage = v.findViewById<ImageView>(R.id.iv_result_item)
                val placeName = v.findViewById<TextView>(R.id.result_name)
                val statusBar = findViewById<View>(android.R.id.statusBarBackground)

                val imagePair = Pair.create(placeImage as View, "tImage")
                val namePair = Pair.create(placeName as View, "tName")

                val statusPair = Pair.create(statusBar, Window.STATUS_BAR_BACKGROUND_TRANSITION_NAME)

                val pairs = mutableListOf(imagePair, namePair, statusPair)

                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this@SearchActivity,
                        *pairs.toTypedArray())
                ActivityCompat.startActivity(this@SearchActivity, transitionIntent, options.toBundle())
            }
        }

//        mResults.add(ResultItem("One App IdeaJSON", "Google Play Store", "test description",
//                mutableListOf("test1", "test2", "Another tag", "So many tags wow", "Last one")))


//        mAsyncTask = RetrieveResultsAsync()
        mAsyncTask = RetrieveResultsAsync()
        mAsyncTask.execute()

        initRecyclerView()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putString("savedName", mName)
        outState?.putString("savedDescription", mDescription)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        var text = savedInstanceState?.getString("savedName")
        if(text!=null){
            mName = text
        }
        text = savedInstanceState?.getString("savedDescription")
        if(text!=null){
            mDescription = text
        }
    }



    private fun newIntent(context: Context, position: Int): Intent {
        val intent = Intent(context, ResultDetailsActivity::class.java)
        intent.putExtra(Constants.INTENT_RESULT, mResults[position])
        return intent
    }

    private fun initActionBar() {
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.title = "Search Results"
    }

    private fun initRecyclerView() {
        mLayoutManager = LinearLayoutManager(this)
        mLayoutManager.orientation = LinearLayoutManager.VERTICAL
        mResultsRecyclerView.layoutManager = mLayoutManager

        mSearchAdapter = SearchRecyclerAdapter(mResultsRecyclerView.context, mResults, mRecyclerViewListener)
        mResultsRecyclerView.adapter = mSearchAdapter
        if (mResults.size>0) {
            mSpinner.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //Cancel background thread task when activity is destroyed
        if(::mAsyncTask.isInitialized){
            mAsyncTask.cancel(true)
        }
    }
/**
    private inner class CheckDomainAsync: AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg p0: String): String? {
            val baseUrl = "https://domainr.p.mashape.com/v2/status?domain="
            val domain = mName.replace(" ", "")
            Fuel.get(baseUrl + domain).responseString { request, response, result ->
                /*result.success { json ->
                    Log.e("domain result: ", json.toString())
                }*/
                result.fold({
                    Log.e("SUCCESS", result.getAs())
                    return@responseString result.getAs()
                }, {
                    Log.e("FAILURE", result.toString())
                    return@responseString null
                })
            }

        }
    }**/

    /**
     * This Async task calls the products API Call and retrieves necessary fields
     */
    private inner class RetrieveResultsAsync : AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg strings: String): String? {

//            FuelManager.instance.baseHeaders = mapOf("Device" to "Android")
            val request = Request(name=mName, description=mDescription, sites =
            Sites(github = mSearchOptions[0], googlePlay = mSearchOptions[1],
                    devpost = mSearchOptions[2], productHunt = mSearchOptions[3]))
//            val jsonObject:JSONObject =
            var builder = GsonBuilder()
            val gson:Gson = builder.create()
            builder.disableHtmlEscaping()
            val json = JSONObject()
            val sites = JSONObject()
            sites.put("github", mSearchOptions[0])
            sites.put("googleplay", mSearchOptions[1])
            sites.put("devpost", mSearchOptions[2])
            sites.put("producthunt", mSearchOptions[3])
            json.put("name", mName)
            json.put("description", mDescription)
            json.put("sites", sites)
            if(mResults.size==0){
                Log.e("json request:", json.toString())
                val (ignoredRequest, ignoredResponse, result) = Fuel.post( "$url")
                        .header("Content-Type" to "application/json")
                        .body(json.toString())
                        .responseString()
                result.fold({
                    Log.e("SUCCESS", result.getAs())
                    return result.getAs()
                }, {
                    Log.e("FAILURE", result.toString())
                    return null
                })
            }
            else{
                return null
            }

        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if(result!=null && result!=""){
                Log.e("POSTEXECUTE", result.toString())
                val gson = Gson()
                val ideas : IdeaJSON? = gson.fromJson(result, IdeaJSON::class.java)
                if(ideas!=null){
                    mResults.addAll(ideas.results)
                }
                hideSpinner()
            }
            else {
                Log.e("PostExecute", "Result was null")
                Snackbar.make(mResultsRecyclerView, "Result: $result", Snackbar.LENGTH_LONG).show()
                hideSpinner()
            }
        }
    }

    private fun hideSpinner(){
        mSpinner.visibility = View.GONE
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
