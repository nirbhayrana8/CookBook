package com.zenger.cookbook.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.CursorAdapter
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.google.android.material.snackbar.Snackbar
import com.zenger.cookbook.R
import com.zenger.cookbook.adapters.LoadStateAdapter
import com.zenger.cookbook.adapters.SearchApiAdapter
import com.zenger.cookbook.adapters.SuggestionsAdapter
import com.zenger.cookbook.api.models.BaseRecipe
import com.zenger.cookbook.databinding.FragmentDiscoverBinding
import com.zenger.cookbook.room.tables.SearchResultsTable
import com.zenger.cookbook.viewmodels.DiscoverViewModel
import com.zenger.cookbook.viewmodels.factories.DiscoverViewModelFactory
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit


class DiscoverFragment : Fragment(), SearchApiAdapter.OnItemClickListener {

    private val factory by lazy { DiscoverViewModelFactory(requireActivity().application) }
    private val viewModel: DiscoverViewModel by navGraphViewModels(R.id.app_flow_nav) { factory }

    private lateinit var binding: FragmentDiscoverBinding
    private val disposables by lazy { CompositeDisposable() }

    private val subject = PublishSubject.create<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_discover, container, false)

        val adapter = SearchApiAdapter(this)

        binding.apply {
            recyclerView.setHasFixedSize(true)
            recyclerView.adapter = adapter.withLoadStateHeaderAndFooter(
                    header = LoadStateAdapter { adapter.retry() },
                    footer = LoadStateAdapter { adapter.retry() }
            )
        }

        viewModel.randomRecipes.observe(viewLifecycleOwner) {
            adapter.submitData(viewLifecycleOwner.lifecycle, it)

        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val searchView = binding.searchView

        val autoCompleteTextView = searchView.findViewById<AutoCompleteTextView>(R.id.search_src_text)
        autoCompleteTextView.setDropDownBackgroundResource(R.color.transparent)

        autoCompleteTextView.hint = getString(R.string.search_hint)

        // Add a observer for the stream from the SearchView
        observer()

        searchView.setOnSuggestionListener(object : SearchView.OnSuggestionListener {
            override fun onSuggestionSelect(position: Int): Boolean {
                return true
            }

            override fun onSuggestionClick(position: Int): Boolean {
                val ca = searchView.suggestionsAdapter
                val cursor = ca.cursor
                cursor.moveToPosition(position)

                searchView.setQuery(cursor.getString(cursor.getColumnIndex("title")), false)
                return true
            }
        })


        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (searchView.query.isNotEmpty()) {
                    subject.onComplete()
                    searchView.clearFocus()

                    query?.let {
                        findNavController().navigate(DiscoverFragmentDirections.actionDiscoverFragmentToSearchApiFragment(query))
                    }
                } else {
                    Snackbar.make(binding.root, "Enter a search query", Snackbar.LENGTH_SHORT)
                            .setAnchorView(binding.root)
                            .show()
                }

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { subject.onNext(newText) }

                observeCursor(searchView)
                return true
            }
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (viewModel.listState != null) {
            binding.recyclerView.layoutManager?.onRestoreInstanceState(viewModel.listState)
            viewModel.listState = null
        }

    }

    override fun onDestroyView() {
        viewModel.listState = binding.recyclerView.layoutManager?.onSaveInstanceState()
        disposables.clear()
        super.onDestroyView()

    }

    override fun onItemClick(searchResults: SearchResultsTable) {

        val item = BaseRecipe(id = searchResults.itemId, title = searchResults.title, imageUrl = searchResults.imageUrl)
        val action = DiscoverFragmentDirections.actionDiscoverFragmentToDetailFragment(item)

        findNavController().navigate(action)

    }

    private fun observer() {
        subject.debounce(400, TimeUnit.MILLISECONDS)
                .filter { it.isNotEmpty() }
                .distinctUntilChanged()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<String> {

                    override fun onSubscribe(d: Disposable?) {
                        disposables.add(d)
                    }

                    override fun onNext(string: String?) {
                        string?.let { viewModel.getSuggestions(it) }
                    }

                    override fun onError(e: Throwable?) {}

                    override fun onComplete() {}
                })
    }

    private fun observeCursor(searchView: SearchView) {
        if (view != null) {
            viewModel.cursor.observe(viewLifecycleOwner) {
                try {
                    val adapter = SuggestionsAdapter(requireContext(), it!!, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER)
                    searchView.suggestionsAdapter = adapter
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

}