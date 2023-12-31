package com.example.cityevents.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.cityevents.firebase.Firebase
import com.example.cityevents.SignInActivity
import com.example.cityevents.databinding.FragmentAccountBinding
import com.google.firebase.auth.FirebaseAuth

class AccountFragment : Fragment() {
    private lateinit var binding: FragmentAccountBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpUserPicture(binding.avatar, binding.username)
        val firebase = Firebase()
        firebase.getAccountTypeFromFirebase {
            role -> binding.role.text = role.name
        }
        buttonSignOut()
    }

    private fun setUpUserPicture(imageView: ImageView, userName: TextView) {
        Glide.with(this).load(FirebaseAuth.getInstance().currentUser?.photoUrl)
            .transform(RoundedCorners(300)).into(imageView)
        userName.text = FirebaseAuth.getInstance().currentUser!!.displayName
    }

    private fun buttonSignOut() {
        binding.exitBtn.setOnClickListener {
            val firebase = Firebase()
            firebase.signOut()
            val intent = Intent(activity, SignInActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            requireContext().getSharedPreferences("MY_APP", Context.MODE_PRIVATE).edit()
                .putBoolean("IS_LOGGED_IN", false).apply()
            startActivity(intent)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = AccountFragment()
    }
}