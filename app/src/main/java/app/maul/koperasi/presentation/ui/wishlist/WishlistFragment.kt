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
    private lateinit var wishlistAdapter: WishlistAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWishlistBinding.inflate(inflater, container, false)
        wishlistViewModel.getAllWishlist(Preferences.getId(requireContext()))
        showWishlistRV()
        return binding.root
    }


    private fun showWishlistRV() {
        val mlayoutManager = GridLayoutManager(activity, 2)
        mlayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.rvProduct.layoutManager = mlayoutManager
        wishlistViewModel.wishlists.observe(viewLifecycleOwner, Observer { wishlist ->
            println(wishlist)
            wishlistAdapter = WishlistAdapter(wishlist, object : WishlistItemListener {
                override fun onItemClick(wishlist: Wishlist) {
                    startActivity(Intent(activity, DetailProductActivity::class.java).apply {
                        putExtra("product_id", wishlist.productId)
                    })
                }
            })
            binding.rvProduct.adapter = wishlistAdapter
        })
    }
}