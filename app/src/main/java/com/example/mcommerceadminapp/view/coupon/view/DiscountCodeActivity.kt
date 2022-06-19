package com.example.mcommerceadminapp.view.coupon.view

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.R
import androidx.lifecycle.ViewModelProvider
import com.example.mcommerceadminapp.databinding.ActivityDiscountCodeBinding
import com.example.mcommerceadminapp.model.remote_source.coupon.CouponRemoteSource
import com.example.mcommerceadminapp.model.shopify_repository.coupon.CouponRepo
import com.example.mcommerceadminapp.pojo.coupon.price_rule.PriceRules
import com.example.mcommerceadminapp.view.coupon.view.adapter.DiscountCodeAdapter
import com.example.mcommerceadminapp.view.coupon.view.adapter.OnClickListner
import com.example.mcommerceadminapp.view.coupon.viewmodel.DiscountCodeViewModel
import com.example.mcommerceadminapp.view.coupon.viewmodel.DiscountCodeViewModelFactory
import com.example.mcommerceadminapp.MainActivity
import com.example.mcommerceadminapp.network.MyConnectivityManager
import com.example.mcommerceadminapp.pojo.coupon.discount_code.DiscountCodes


class DiscountCodeActivity : OnClickListner, AppCompatActivity() {


    private lateinit var binding: ActivityDiscountCodeBinding
    private lateinit var discountCodeVM: DiscountCodeViewModel
    private lateinit var discountCodeVMFactory: DiscountCodeViewModelFactory
    private lateinit var discountCodeAdapter : DiscountCodeAdapter
    private lateinit var idIntent :String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDiscountCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        idIntent = intent.getStringExtra("PRICERULE_ID").toString()

        discountCodeVM.allDiscountCode.observe(this){
            binding.loadingProgressBar.visibility = View.INVISIBLE
            discountCodeAdapter.setData(it)
            binding.discountCodeRecycler.adapter = discountCodeAdapter
        }

        binding.addBtn.setOnClickListener{
            showDialog()
        }

        MyConnectivityManager.state.observe(this) {

            if (it) {
                Toast.makeText(this, "Connection is restored", Toast.LENGTH_SHORT).show()
                discountCodeVM.getAllDiscountCode(idIntent)
                binding.loadingProgressBar.visibility = View.VISIBLE
                binding.noNetworkLayout.visibility = View.INVISIBLE
                binding.loadingProgressBar.visibility = View.VISIBLE
                binding.discountCodeRecycler.visibility = View.VISIBLE
            } else {
                Toast.makeText(this, "Connection is lost", Toast.LENGTH_SHORT).show()
                binding.noNetworkLayout.visibility = View.VISIBLE
                binding.loadingProgressBar.visibility = View.INVISIBLE
                binding.loadingProgressBar.visibility = View.INVISIBLE
                binding.discountCodeRecycler.visibility = View.INVISIBLE
            }
        }


    }

   private fun showDialog(){

       val alertDialog: AlertDialog.Builder = AlertDialog.Builder(this@DiscountCodeActivity)
       alertDialog.setTitle("Add DiscountCode")
       alertDialog.setMessage("Enter Code")
       val input = EditText(this@DiscountCodeActivity)
       val lp = LinearLayout.LayoutParams(
           LinearLayout.LayoutParams.MATCH_PARENT,
           LinearLayout.LayoutParams.MATCH_PARENT
       )
       input.layoutParams = lp
       alertDialog.setView(input)
       alertDialog
           .setCancelable(true)
           .setPositiveButton("OK") { dialog, id -> // get user input and set it to result
               // edit text
               val code = input.getText().toString()
               discountCodeVM.createDiscountCode(idIntent, DiscountCodes(code = code))

           }
           .setNegativeButton(
               "Cancel"
           ) { dialog, id -> dialog.cancel() }

       alertDialog.show()
   }

    private fun init(){
        discountCodeAdapter = DiscountCodeAdapter( this)
        binding.discountCodeRecycler.adapter = discountCodeAdapter
        discountCodeVMFactory = DiscountCodeViewModelFactory(
            CouponRepo.getInstance(CouponRemoteSource()),
        )
        discountCodeVM = ViewModelProvider(this, discountCodeVMFactory)[DiscountCodeViewModel::class.java]

    }

    override fun onClick(id: String?,type:String) {
        discountCodeVM.deleteDiscountCodeID(this.idIntent , id.toString())
    }

    override fun <T> onClickEdit(typeObject: T, type: String) {
        val discountCode = typeObject as DiscountCodes
        discountCodeVM.updateDiscountCode(this.idIntent, discountCode.id.toString(),discountCode)

    }

}