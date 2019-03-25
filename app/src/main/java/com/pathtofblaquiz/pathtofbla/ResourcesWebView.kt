package com.pathtofblaquiz.pathtofbla

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_resources_web_view.*

/**
 * Resources Web View Activity allows users to study a category resource in an inbuilt browser
 */
class ResourcesWebView : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resources_web_view)
        val categoryLink = intent.getStringExtra(ResourcesViewHolder.CATEGORY_LINK_KEY)

        resourceWebView.loadUrl(categoryLink)
    }
}
