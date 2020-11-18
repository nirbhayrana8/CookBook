package com.zenger.cookbook.ui.fragments

import android.os.Bundle
import android.view.*
import android.widget.AutoCompleteTextView
import android.widget.CursorAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar
import com.zenger.cookbook.R
import com.zenger.cookbook.adapters.LoadStateAdapter
import com.zenger.cookbook.adapters.RecyclerAdapter
import com.zenger.cookbook.adapters.SuggestionsAdapter
import com.zenger.cookbook.api.classes.RandomObj
import com.zenger.cookbook.databinding.FragmentDiscoverBinding
import com.zenger.cookbook.viewmodels.DiscoverViewModel
import com.zenger.cookbook.viewmodels.factories.DiscoverViewModelFactory
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit


class DiscoverFragment : Fragment(), RecyclerAdapter.OnItemClickListener {

    private val factory by lazy { DiscoverViewModelFactory(requireActivity().application) }
    private val viewModel: DiscoverViewModel by viewModels { factory }
    private lateinit var binding: FragmentDiscoverBinding

    private val disposables by lazy { CompositeDisposable() }
    private val subject = PublishSubject.create<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_discover, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = RecyclerAdapter(this)

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


        val layout = binding.toolBar
        val toolbar = layout.findViewById<MaterialToolbar>(R.id.toolBar)
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.search_menu, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView


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
                subject.onComplete()
                binding.recyclerView.scrollToPosition(0)
                searchView.clearFocus()

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { subject.onNext(newText) }

                viewModel.cursor.observe(viewLifecycleOwner) {
                    try {
                        val adapter = SuggestionsAdapter(requireContext(), it!!, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER)
                        searchView.suggestionsAdapter = adapter
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
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

    override fun onItemClick(recipe: RandomObj) {
        val view = requireActivity().findViewById<View>(R.id.constraintLayout)
        val snackBar = Snackbar.make(view, "Clicked", Snackbar.LENGTH_LONG)
        snackBar.anchorView = view.findViewById(R.id.bottomNav)
        snackBar.show()

    }

    private fun observer() {
        subject.debounce(500, TimeUnit.MILLISECONDS)
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

}