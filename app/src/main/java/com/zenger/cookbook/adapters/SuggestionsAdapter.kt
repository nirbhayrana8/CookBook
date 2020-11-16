package com.zenger.cookbook.adapters

import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cursoradapter.widget.CursorAdapter
import androidx.databinding.DataBindingUtil
import com.zenger.cookbook.R
import com.zenger.cookbook.databinding.SuggestionLayoutBinding

class SuggestionsAdapter(private val cntxt: Context,
                         cursor: Cursor,
                         flags: Int): CursorAdapter(cntxt, cursor, flags) {

    private lateinit var binding: SuggestionLayoutBinding

    override fun newView(context: Context?, cursor: Cursor?, parent: ViewGroup?): View {
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.suggestion_layout, parent, false)
        return binding.root
    }

    override fun bindView(view: View?, context: Context?, cursor: Cursor?) {
        val title = cursor?.let { cursor.getString(cursor.getColumnIndexOrThrow("title")) }
        binding.textView.text = title

    }
}