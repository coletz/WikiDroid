package com.coletz.droidwiki.ui.search

import android.app.Activity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.coletz.droidwiki.Homepage
import com.coletz.droidwiki.R
import com.coletz.droidwiki.EntryAdapter
import com.coletz.droidwiki.application.WikiApplication
import com.coletz.droidwiki.model.Entry
import com.coletz.droidwiki.utils.errorDialog
import kotlinx.android.synthetic.main.activity_search.*
import javax.inject.Inject
import android.widget.SearchView

class SearchActivity : Activity(), EntryView {

  @Inject lateinit var presenter: EntryPresenter
  @Inject lateinit var homepage: Homepage

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_search)

    (application as WikiApplication).wikiComponent.inject(this)

    actionBar?.setHomeAsUpIndicator(R.drawable.ic_home)
    actionBar?.setDisplayHomeAsUpEnabled(true)

    results_rv.layoutManager = LinearLayoutManager(this)

    presenter.setView(this)

  }

  // Create the menu entries
  override fun onOptionsItemSelected(item: MenuItem) =
      when (item.itemId) {
        android.R.id.home -> {
          onBackPressed()
          true
        }
        else -> {
          super.onOptionsItemSelected(item)
        }
      }

  // Bind menu entries with their actions
  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.search, menu)

    menu?.findItem(R.id.search)?.let { menuItem ->
      (menuItem.actionView as? SearchView)?.apply {
        setOnQueryTextListener(object : SearchView.OnQueryTextListener {
          override fun onQueryTextChange(query: String) = true
          override fun onQueryTextSubmit(query: String?): Boolean {
            presenter.getEntry(query ?: "")
            return true
          }
        })

        queryHint = getString(R.string.search_hint)
      }

      menuItem.expandActionView()
    }

    return super.onCreateOptionsMenu(menu)
  }

  // Implementation of EntryView

  override fun displayLoading() {
    wait_progress_bar.post {
      wait_progress_bar.visibility = View.VISIBLE
      results_rv.visibility = View.GONE
    }
  }

  override fun dismissLoading() {
    wait_progress_bar.post {
      wait_progress_bar.visibility = View.GONE
      results_rv.visibility = View.VISIBLE
    }
  }

  override fun displayEntries(results: List<Entry>) {
    results_rv.post {
      results_rv.adapter = EntryAdapter(this, results)
    }
  }

  override fun displayError(error: String?) {
    Log.e("ERROR", error)
    R.string.error.errorDialog(this)
  }
}
