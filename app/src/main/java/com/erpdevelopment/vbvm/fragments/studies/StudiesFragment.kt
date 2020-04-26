package com.erpdevelopment.vbvm.fragments.studies

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Parcelable
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.erpdevelopment.vbvm.R
import com.erpdevelopment.vbvm.api.APIManager
import com.erpdevelopment.vbvm.application.MainActivity
import com.erpdevelopment.vbvm.fragments.shared.AbstractFragment
import com.erpdevelopment.vbvm.fragments.studies.study.StudyKey
import com.erpdevelopment.vbvm.util.ServiceLocator
import com.erpdevelopment.vbvm.views.LoadingView
import com.google.android.material.tabs.TabLayout
import com.raizlabs.android.dbflow.sql.language.SQLite
import com.zhuinden.simplestack.BackstackDelegate
import org.versebyverseministry.models.Category
import org.versebyverseministry.models.Study
import org.versebyverseministry.models.Study_Table
import org.versebyverseministry.models.`Category$$Parcelable`
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * A simple [Fragment] subclass.
 * to handle interaction events.
 * Use the [StudiesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class StudiesFragment : AbstractFragment() {
    /**
     * The [PagerAdapter] that will provide
     * fragments for each of the sections. We use a
     * [FragmentPagerAdapter] derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * [FragmentStatePagerAdapter].
     */
    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null
    /**
     * The [ViewPager] that will host the section contents.
     */
    private lateinit var mViewPager: ViewPager
    private lateinit var loadingView: LoadingView
    override fun shouldBitmapUI(): Boolean {
        return true
    }

    private fun configureCategoryPager() {
        val categories = Category.allCategories()
        if (Study.countOf() > 0) {
            mSectionsPagerAdapter!!.setCategories(categories)
            mViewPager.visibility = View.VISIBLE
            loadingView.visibility = View.GONE
            Log.d(TAG, "configureCategoryPager: $this")
        }
    }

    private fun hasContent(): Boolean {
        return Study.countOf() > 0 && Category.countOf() > 0
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? { // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_studies_root, container, false)
        val toolbar: Toolbar = view.findViewById(R.id.studiesToolar)
        val tabLayout: TabLayout = view.findViewById(R.id.studiesTabs)
        loadingView = view.findViewById(R.id.loading_view)
        mViewPager = view.findViewById(R.id.studiesContainer)

        toolbar.setTitle(R.string.title_studies)
        mSectionsPagerAdapter = SectionsPagerAdapter(this.childFragmentManager)
        mViewPager.setAdapter(mSectionsPagerAdapter)
        configureCategoryPager()
        tabLayout.setupWithViewPager(mViewPager)
        if (!hasContent()) {
            loadingView.setVisibility(View.VISIBLE)
            mViewPager.setVisibility(View.GONE)
        }
        if (lastRequestDate == null || TimeUnit.MILLISECONDS.toSeconds(Date().time - lastRequestDate!!.time) > 300) {
            categoriesCompleted = false
            studiesCompleted = false
            lastRequestDate = Date()
            Log.d(TAG, "onCreateView: ")
            APIManager.getInstance().downloadCategories { success: Boolean ->
                // Categories downloaded... refresh that screen!
                if (success) { //configureCategoryPager();
                    categoriesCompleted = true
                    if (studiesCompleted && categoriesCompleted && (activeRunner != null)) {
                        activeRunner!!.run()
                    }
                }
            }
            APIManager.getInstance().downloadStudies { success: Boolean ->
                // Studies downloaded... refresh!
                if (success) {
                    studiesCompleted = true
                    if (studiesCompleted && categoriesCompleted && (activeRunner != null)) {
                        activeRunner!!.run()
                    }
                }
            }
        }
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "onAttach: $this")
        activeRunner = Runnable { if (!isDetached) configureCategoryPager() }
    }

    override fun onDetach() {
        super.onDetach()
        mSectionsPagerAdapter = null
        Log.d(TAG, "onDetach: $this")
        activeRunner = null
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    class StudiesListFragment : Fragment() {
        private var category: org.versebyverseministry.models.Category? = null
        private var view: RecyclerView? = null
        private var studiesReceiver: BroadcastReceiver? = null

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                  savedInstanceState: Bundle?): View? {
            val rootView = inflater.inflate(R.layout.fragment_studies, container, false)
            val category = (arguments!!.getParcelable<Parcelable>(ARG_CATEGORY) as `Category$$Parcelable`).parcel
            this.category = category
            val studies = Study.fetchStudiesWithCategory(category)
            val displayMetrics = DisplayMetrics()
            (context!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getMetrics(displayMetrics)
            Log.d("Placeholder", "onCreateView: found " + studies.size + " studies")
            val dpWidth = displayMetrics.widthPixels / displayMetrics.density
            var imageWidth = Math.max(dpWidth / 4 - 1, 120f)
            val dpHeight = displayMetrics.heightPixels / displayMetrics.density
            val imageHeight = dpHeight / 3
            imageWidth = Math.min(imageHeight, imageWidth)
            val numberOfColumns = (dpWidth / imageWidth).toInt()
            // on a large device go with 4 columns?
            if (rootView is RecyclerView) {
                val context = rootView.getContext()
                val recyclerView = rootView
                view = recyclerView
                recyclerView.layoutManager = GridLayoutManager(context, numberOfColumns)
                recyclerView.adapter = MyStudyRecyclerViewAdapter(studies, numberOfColumns, MyStudyRecyclerViewAdapter.OnStudyInteractionListener { study ->
                    APIManager.getInstance().downloadLessons(study.id, { success: Boolean -> Log.d(TAG, "Downloaded the lessons successfully $success") })
                    (ServiceLocator.getService<Any>(getContext(), MainActivity.StackType.STUDIES.name) as BackstackDelegate).backstack.goTo(StudyKey.create(study.id))
                })
            }
            val mainHandler = Handler(context!!.mainLooper)
            val receiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    mainHandler.post { reloadData() }
                }
            }
            studiesReceiver = receiver
            LocalBroadcastManager.getInstance(context!!).registerReceiver(receiver, IntentFilter(org.versebyverseministry.models.Study.updated()))
            //DatabaseManager.observer.addOnTableChangedListener(tableChangedListener);
            return rootView
        }

        fun reloadData() {
            val adapter = view!!.adapter as MyStudyRecyclerViewAdapter?
            val studies = SQLite.select().from(org.versebyverseministry.models.Study::class.java).where(Study_Table.category.eq(category!!.id)).orderBy(Study_Table.bibleIndex, true).queryList()
            adapter!!.setStudies(studies)
        }

        override fun onDetach() {
            super.onDetach()
            //DatabaseManager.observer.removeTableChangedListener(tableChangedListener);
            Log.d("STUDIESFRAGMENT", "removed table change listener")
            studiesReceiver?.let {
                LocalBroadcastManager.getInstance(context!!).unregisterReceiver(it)
                studiesReceiver = null
            }
        }

        companion object {
            /**
             * The fragment argument representing the section number for this
             * fragment.
             */
            private const val ARG_SECTION_NUMBER = "section_number"
            private const val ARG_CATEGORY = "ARG_CATEGORY"
            /**
             * Returns a new instance of this fragment for the given section
             * number.
             */
            fun newInstance(category: org.versebyverseministry.models.Category?): StudiesListFragment {
                val fragment = StudiesListFragment()
                val args = Bundle()
                args.putParcelable(ARG_CATEGORY, `Category$$Parcelable`(category))
                fragment.arguments = args
                return fragment
            }
        }
    }

    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    private inner class SectionsPagerAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm!!) {
        private var categories: List<org.versebyverseministry.models.Category> = ArrayList()

        fun setCategories(categories: List<org.versebyverseministry.models.Category>?) {
            val myCategories = categories
            if (myCategories == null) {
                notifyDataSetChanged()
                return
            }
            this.categories = myCategories
            notifyDataSetChanged()
        }

        override fun getItem(position: Int): Fragment {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return StudiesListFragment.newInstance(categories[position])
        }

        override fun getCount(): Int {
            return categories.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            val category = categories[position]
            return category.name
        }
    }

    companion object {
        private const val TAG = "StudiesFragment"
        private var categoriesCompleted = false
        private var studiesCompleted = false
        private var lastRequestDate: Date? = null
        private var activeRunner: Runnable? = null
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment StudiesView.
         */
        @JvmStatic
        fun newInstance(): StudiesFragment {
            val fragment = StudiesFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}