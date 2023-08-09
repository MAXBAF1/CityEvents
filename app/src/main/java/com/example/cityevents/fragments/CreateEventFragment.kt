package com.example.cityevents.fragments

import android.app.Activity
import android.content.ClipData
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cityevents.adapters.ImageAdapter
import com.example.cityevents.databinding.FragmentCreateEventBinding
import java.util.*

class CreateEventFragment : Fragment() {
    private lateinit var binding: FragmentCreateEventBinding
    private val selectedImages = mutableListOf<Uri>()
    private val imageAdapter: ImageAdapter by lazy { ImageAdapter(selectedImages) }

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intentData: Intent? = result.data
                if (intentData?.clipData != null) {
                    handleClipDataImages(intentData.clipData!!)
                } else if (intentData?.data != null) {
                    handleSingleImage(intentData.data!!)
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAddPhotosButton()
        setupPhotosRecyclerView()
    }

    private fun setupAddPhotosButton() {
        val pickImageIntent = Intent(Intent.ACTION_GET_CONTENT)
        pickImageIntent.type = "image/*"
        pickImageIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)

        binding.addPhotosBtn.setOnClickListener {
            galleryLauncher.launch(pickImageIntent)
        }
    }

    private fun setupPhotosRecyclerView() {
        binding.photosRcView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.photosRcView.adapter = imageAdapter

        val itemTouchHelper = ItemTouchHelper(createItemTouchHelperCallback())
        itemTouchHelper.attachToRecyclerView(binding.photosRcView)
    }

    private fun createItemTouchHelperCallback(): ItemTouchHelper.SimpleCallback {
        return object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val fromPosition = viewHolder.absoluteAdapterPosition
                val toPosition = target.absoluteAdapterPosition

                Collections.swap(selectedImages, fromPosition, toPosition)
                imageAdapter.notifyItemMoved(fromPosition, toPosition)

                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.absoluteAdapterPosition
                selectedImages.removeAt(position)
                imageAdapter.notifyItemRemoved(position)
            }
        }
    }

    private fun handleClipDataImages(clipData: ClipData) {
        val startingPosition = selectedImages.size
        for (i in 0 until clipData.itemCount) {
            val uri = clipData.getItemAt(i).uri
            selectedImages.add(uri)
        }
        imageAdapter.notifyItemRangeInserted(
            startingPosition, selectedImages.size - startingPosition
        )
    }

    private fun handleSingleImage(uri: Uri) {
        selectedImages.add(uri)
        imageAdapter.notifyItemInserted(selectedImages.size - 1)
    }

    companion object {
        @JvmStatic
        fun newInstance() = CreateEventFragment()
    }
}