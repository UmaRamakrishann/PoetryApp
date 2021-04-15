package com.udacity.capstone.poetry.main

import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.udacity.capstone.poetry.R
import com.udacity.capstone.poetry.databinding.FragmentPoemBinding
import com.udacity.capstone.poetry.util.cancelNotifications


/**
 * A simple [Fragment] subclass.
 * Use the [PoemFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PoemFragment : Fragment() {
    private val viewModel: PoetryViewModel by lazy {
        ViewModelProvider(this).get(PoetryViewModel::class.java)
    }

    private lateinit var notificationManager: NotificationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentPoemBinding.inflate(inflater)
        binding.lifecycleOwner = this

        notificationManager = ContextCompat.getSystemService(
            requireActivity(),
            NotificationManager::class.java
        ) as NotificationManager


        val selectedPoem = PoemFragmentArgs.fromBundle(requireArguments()).selectedPoem
        binding.poem = selectedPoem
        if (selectedPoem.isFavorite) {
            binding.favoriteToggle.setImageResource(R.drawable.ic_red_heart)
            binding.favoriteToggle.setContentDescription(getString(R.string.remove_from_favorites))
        } else {
            binding.favoriteToggle.setImageResource(R.drawable.ic_grey_heart)
            binding.favoriteToggle.setContentDescription(getString(R.string.add_to_favorites))
        }

        binding.favoriteToggle.setOnClickListener {
            viewModel.poemToUpdate.value = selectedPoem
            if (selectedPoem.isFavorite) {
                binding.favoriteToggle.setImageResource(R.drawable.ic_grey_heart)
                binding.favoriteToggle.setContentDescription(getString(R.string.add_to_favorites))
                viewModel.removeFromFavorites()
            } else {
                binding.favoriteToggle.setImageResource(R.drawable.ic_red_heart)
                binding.favoriteToggle.setContentDescription(getString(R.string.remove_from_favorites))
                viewModel.addToFavorites()
            }
        }

        binding.shareIcon.setOnClickListener {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, selectedPoem.title)
            shareIntent.putExtra(Intent.EXTRA_TEXT, selectedPoem.text)
            shareIntent.type = "text/plain"
            startActivity(shareIntent)
        }

        viewModel.poemOfDayList.observe(viewLifecycleOwner) { podList ->
            podList?.apply {
                if (podList.any() && podList.get(0) == selectedPoem) {
                    notificationManager.cancelNotifications()
                }
            }
        }

        return binding.root
    }

}