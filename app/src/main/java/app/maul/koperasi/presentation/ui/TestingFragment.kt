package app.maul.koperasi.presentation.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.maul.koperasi.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TestingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TestingFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_testing, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TestingFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TestingFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    //    private fun doRegister(name : String,email : String,password : String){
//        authViewModel.doPostRegister(name, email, password)
//        authViewModel.doPostRegisterObserver().observe(this){ x ->
//            if(x != null){
//                startActivity(Intent(this, VerifyActivity::class.java).also{
//                    it.putExtra("email",binding.etEmail.text.toString().trim())
//                    it.putExtra("otp",x.otp)
//                })
//                Toast.makeText(this, "${x.otp}", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }

    ////cart
    //            // Create OrderRequest object
//            val orderRequest = OrderRequest(
//                id_user = userId,
//                total = total.toDouble(),
//                payment_type = "bank_transfer",
//                bank_transfer = "bri",
//                shipping_method = "pending",
//                orderDetails = orderDetails,
//                customer = "",
//                phone_number = "",
//                address = ""
//            )

    // Call createOrder with the constructed OrderRequest
//            orderViewModel.createOrder(orderRequest)
}