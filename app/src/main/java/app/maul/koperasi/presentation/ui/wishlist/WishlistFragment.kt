package app.maul.koperasi.presentation.ui.wishlist

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import app.maul.koperasi.databinding.FragmentWishlistBinding
import app.maul.koperasi.model.wishlist.Wishlist
import app.maul.koperasi.preference.Preferences
import app.maul.koperasi.presentation.ui.activity.DetailProductActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WishlistFragment : Fragment() {
    private val wishlistViewModel by viewModels<WishlistViewModel>()
    private var _binding: FragmentWishlistBinding? = null
    private val binding get() = _binding!!
    // [FIX 1] Inisialisasi adapter di sini
    private lateinit var wishlistAdapter: WishlistAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWishlistBinding.inflate(inflater, container, false)

        setupAdapter() // Panggil setup adapter sekali saja
        observeWishlist() // Ganti nama fungsi agar lebih jelas

        wishlistViewModel.getAllWishlist(Preferences.getId(requireContext()))

        return binding.root
    }

    // [FIX 2] Buat fungsi terpisah untuk setup adapter
    private fun setupAdapter() {
        wishlistAdapter = WishlistAdapter(emptyList(), object : WishlistItemListener {
            override fun onItemClick(wishlist: Wishlist) {
                startActivity(Intent(activity, DetailProductActivity::class.java).apply {
                    putExtra("product_id", wishlist.productId)
                })
            }
        })

        val mlayoutManager = GridLayoutManager(activity, 2)
        binding.rvProduct.layoutManager = mlayoutManager
        binding.rvProduct.adapter = wishlistAdapter
    }

    // [FIX 3] Di dalam observer, HANYA panggil fungsi updateData
    private fun observeWishlist() {
        wishlistViewModel.wishlists.observe(viewLifecycleOwner, Observer { wishlist ->
            wishlistAdapter.updateData(wishlist)
        })
    }
}