package com.pathtofblaquiz.pathtofbla

import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.resources_cells.view.*

/**
 * Resources Adapter allows for an easy creation of the list of resources that the users can chose to study from
 */
class ResourcesAdapter : RecyclerView.Adapter<ResourcesViewHolder>() {

    //list of available categories to study for
    val categoryTitles = listOf(
        "FBLA Organization",
        "Competitive Events",
        "Business Skills",
        "National Officers",
        "Parliamentary Procedure",
        "National Conference",
        "FBLA History",
        "FBLA Bylaws",
        "Creed, National Goals, and Ethics",
        "Community Service"
    )

    //list of links to te subsequent categories
    val resourcesLinks = listOf(
        "https://quizlet.com/381613945/path-to-fbla-fbla-organization-flash-cards/",
        "https://quizlet.com/381614150/path-to-fbla-competitive-events-flash-cards/",
        "https://quizlet.com/381615091/path-to-fbla-business-skills-flash-cards/",
        "https://quizlet.com/381615911/path-to-fbla-national-officers-flash-cards/",
        "https://quizlet.com/381616020/path-to-fbla-parliamentary-procedure-flash-cards/",
        "https://quizlet.com/381617614/path-to-fbla-national-conference-flash-cards/",
        "https://quizlet.com/381617893/path-to-fbla-fbla-history-flash-cards/",
        "https://quizlet.com/381617967/path-to-fbla-fbla-bylaws-flash-cards/",
        "https://quizlet.com/381617998/path-to-fbla-creed-national-goals-and-ethics-flash-cards/",
        "https://quizlet.com/381615638/path-to-fbla-community-service-flash-cards/"
    )

    //Pass in a view
    // Template view
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ResourcesViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context)
        val cellForRow = layoutInflater.inflate(R.layout.resources_cells, p0, false)
        return ResourcesViewHolder(cellForRow, "placeholder")
    }

    //Number of items
    override fun getItemCount(): Int {
        return categoryTitles.size
    }

    //This makes elements unique
    override fun onBindViewHolder(p0: ResourcesViewHolder, p1: Int) {
        p0.view.resourceCategory.text = categoryTitles[p1]
        p0.link = resourcesLinks[p1]
    }

}

/**
 * Resources View Holder works with Resource Adapter to show the selected category in a webview
 */
class ResourcesViewHolder(val view: View, var link: String) : RecyclerView.ViewHolder(view) {

    companion object {
        const val CATEGORY_LINK_KEY = "CATEGORY_TITLE"
    }

    init {
        view.setOnClickListener {
            val switchToWebView = Intent(view.context, ResourcesWebView::class.java)
            switchToWebView.putExtra(CATEGORY_LINK_KEY, link)
            view.context.startActivity(switchToWebView)
        }
    }
}