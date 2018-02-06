package com.austinabell8.canihackit.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.austinabell8.canihackit.R

import com.austinabell8.canihackit.utils.Constants
import org.jetbrains.anko.alert
import org.jetbrains.anko.yesButton
import android.support.v7.app.AlertDialog

class MainActivity : AppCompatActivity() {

    private lateinit var mNameTxt : EditText
    private lateinit var mDescriptionText : EditText
    private lateinit var mSearchButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mNameTxt = findViewById(R.id.input_app_name)
        mDescriptionText = findViewById(R.id.input_description)
        mSearchButton = findViewById(R.id.search_button)
        mSearchButton.setOnClickListener {
            val name = mNameTxt.text.toString().trim()
                    .replace("[^A-Za-z0-9 ]", "").toLowerCase()
            val description = mDescriptionText.text.toString().trim()
                    .replace("[^A-Za-z0-9 ]", "").toLowerCase()
            if (name == "" && description == "") {
                alert("A name of an app or description must be provided") {
                    title = "Invalid Input"
                    yesButton { }
                }.show()
            } else {

                // Build an AlertDialog
                val builder = AlertDialog.Builder(this@MainActivity)

                // String array for alert dialog multi choice items
                val sites = arrayOf("Github", "Google Play Store", "Devpost", "Product Hunt")

                // Boolean array for initial selected items
                val checkedSites = booleanArrayOf(true, // Github
                        true, // Google Play Store
                        true, //Devpost
                        true //Product Hunt
                )

                // Convert the color array to list
                builder.setMultiChoiceItems(sites, checkedSites, { _, which, isChecked ->
                    // Update the current focused item's checked status
                    checkedSites[which] = isChecked
                })

                // Specify the dialog is not cancelable
                builder.setCancelable(false)

                // Set a title for alert dialog
                builder.setTitle("Where would you like to check for this app")

                // Set the positive/yes button click listener
                builder.setPositiveButton("OK", { _, _ ->
                    // Do something when click positive button
                    val intent = Intent(this@MainActivity, SearchActivity::class.java)
                    intent.putExtra(Constants.INTENT_NAME, name)
                    intent.putExtra(Constants.INTENT_DESCRIPTION, description)
                    intent.putExtra(Constants.INTENT_SITES, checkedSites)
                    startActivity(intent)
                })

                // Set the neutral/cancel button click listener
                builder.setNeutralButton("Cancel", { _, _ ->
                })

                val dialog = builder.create()
                // Display the alert dialog on interface
                dialog.show()

            }
        }
        mNameTxt.clearFocus()
    }
}
