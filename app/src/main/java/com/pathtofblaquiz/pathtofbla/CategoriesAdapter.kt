package com.pathtofblaquiz.pathtofbla

import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.categories_buttons.view.*

/**
 * Categories Adapter allows for an easy creation of the list of categories and adds logic for scene switching to the subsequent click
 */
class CategoriesAdapter : RecyclerView.Adapter<CategoryViewHolder>() {

    //list of the category titles available
    private val categoryTitles = listOf(
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

    //list of the icons that are showed for the subsequent category
    private val categoryIcons = listOf(
        "https://firebasestorage.googleapis.com/v0/b/practice-app-83d33.appspot.com/o/fbla_logo.png?alt=media&token=cc02abe9-d3af-403c-a274-1583b6cfe080",
        "https://img.icons8.com/color/150/000000/trophy.png",
        "https://img.icons8.com/color/150/000000/businessman.png",
        "https://img.icons8.com/color/150/000000/conference.png",
        "https://img.icons8.com/color/150/000000/law.png",
        "https://img.icons8.com/color/150/000000/marker.png",
        "https://img.icons8.com/color/150/000000/time-machine.png",
        "https://img.icons8.com/color/150/000000/user-manual.png",
        "https://img.icons8.com/color/150/000000/accuracy.png",
        "https://img.icons8.com/ios/150/c62828/handshake-heart-filled.png"
    )

    //Pass in a view
    // Template view
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): CategoryViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context)
        val cellForRow = layoutInflater.inflate(R.layout.categories_buttons, p0, false)
        return CategoryViewHolder(cellForRow, "placeholder")
    }

    //Number of items
    override fun getItemCount(): Int {
        return categoryTitles.size
    }

    //This makes elements unique
    override fun onBindViewHolder(p0: CategoryViewHolder, p1: Int) {
        val categoryTitles = categoryTitles[p1]
        val categoryIcons = categoryIcons[p1]
        p0.view.categoryName.text = categoryTitles
        Picasso.get().load(categoryIcons).into(p0.view.categoryImageView)
        p0.title = categoryTitles
    }

}

/**
 * Category View Holder works with Category Adapter and adds the correct button clicks to the buttons
 */
class CategoryViewHolder(val view: View, var title: String) : RecyclerView.ViewHolder(view) {

    companion object {
        const val CATEGORY_TITLE_KEY = "CATEGORY_TITLE"
    }

    init {
        view.setOnClickListener {
            val switchToQuestions = Intent(view.context, QuestionsActivity::class.java)
            switchToQuestions.putExtra(CATEGORY_TITLE_KEY, title)
            switchToQuestions.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            view.context.startActivity(switchToQuestions)
        }
    }
}