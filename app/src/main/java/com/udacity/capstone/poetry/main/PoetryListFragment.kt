package com.udacity.capstone.poetry.main

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavDeepLinkBuilder
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.auth.AuthUI
import com.udacity.capstone.poetry.R
import com.udacity.capstone.poetry.database.PoemsDisplayFilter
import com.udacity.capstone.poetry.databinding.FragmentPoetryListBinding
import com.udacity.capstone.poetry.domain.Poem
import com.udacity.capstone.poetry.util.sendNotification

/**
 * A simple [Fragment] subclass.
 * Use the [PoetryListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PoetryListFragment : Fragment() {

    private val viewModel: PoetryViewModel by lazy {
        ViewModelProvider(this).get(PoetryViewModel::class.java)
    }
    private var viewModelAdapter: PoetryAdapter? = null
    private var podTitle: String = ""
    private val REQUEST_CODE = 0

    private lateinit var notificationManager: NotificationManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentPoetryListBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        notificationManager = ContextCompat.getSystemService(
            requireActivity(),
            NotificationManager::class.java
        ) as NotificationManager

        createChannel(
            getString(R.string.poetryapp_notification_channel_id),
            getString(R.string.poetryapp_notification_channel_name)
        )
        LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(
            receiver,
            IntentFilter("com.udacity.capstone.poetry.POD_INTENT")
        )
        //Show status loading wheel until poems are loaded in the recycler view
        viewModel.showLoading.value = true
        viewModelAdapter = PoetryAdapter(PoetryAdapter.PoemClick {
            viewModel.displayPoem(it)
        })
        viewModel.navigateToSelectedPoem.observe(viewLifecycleOwner, Observer {
            if (null != it) {
                this.findNavController().navigate(PoetryListFragmentDirections.actionShowPoem(it))
                viewModel.displayPoemComplete()
            }
        })
        binding.statusLoadingWheel.progress = 0
        //Show Overflow Menu
        setHasOptionsMenu(true)
        binding.poetryList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = viewModelAdapter
        }
        return binding.root
    }

    //Check if the poem of the day has changed
    private fun checkPodUpdate() {
        val pref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        val savedTitle = pref.getString(getString(R.string.stored_title), "")
        if (savedTitle != podTitle) {
            val intent = Intent("com.udacity.capstone.poetry.POD_INTENT")
            LocalBroadcastManager.getInstance(requireActivity()).sendBroadcast(intent);
        }

    }

    //Save The Poem of The Day Title in preferences
    private fun savePodTitle(podTitle: String?) {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putString(getString(R.string.stored_title), podTitle)
            apply()
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.poemOfDayList.observe(viewLifecycleOwner) { podList ->
            podList?.apply {
                if (podList.any()) {
                    podTitle = podList.get(0).title
                    checkPodUpdate()
                }
            }
        }
        viewModel.poems.observe(viewLifecycleOwner) { poems ->
            poems?.apply {
                viewModelAdapter?.poems = poems
                if (poems.any()) {
                    //Hide status loading wheel once poems are loaded in the recycler view
                    viewModel.showLoading.value = false
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.logout_menu) {
            AuthUI.getInstance()
                .signOut(requireContext())
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
           return true
        }
        viewModel.updateFilter(
            when (item.itemId) {
                R.id.browse_all_poems_menu -> PoemsDisplayFilter.BROWSE_ALL_POEMS
                R.id.browse_favorites_menu -> PoemsDisplayFilter.BROWSE_FAVORITES
                else -> PoemsDisplayFilter.POEM_OF_THE_DAY
            }
        ).observe(viewLifecycleOwner, Observer<List<Poem>> { poems ->
            poems?.apply {
                viewModelAdapter?.poems = poems
                viewModel.showNoPoems.value = !poems.any()
            }
        })
        return true
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val bundle = bundleOf("selectedPoem" to viewModel.poemOfDayList.value?.get(0))
            val poemPendingIntent = NavDeepLinkBuilder(requireActivity())
                .setGraph(R.navigation.nav_graph)
                .setDestination(R.id.poemFragment)
                .setArguments(bundle)
                .createPendingIntent()

            notificationManager.sendNotification(
                getString(R.string.message_body),
                requireContext(),
                poemPendingIntent
            )
            savePodTitle(podTitle)
        }
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_LOW
            )

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)

            val notificationManager = getSystemService(
                requireActivity(),
                NotificationManager::class.java
            )
            notificationManager?.createNotificationChannel(notificationChannel)

        }

    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(receiver)
    }
}