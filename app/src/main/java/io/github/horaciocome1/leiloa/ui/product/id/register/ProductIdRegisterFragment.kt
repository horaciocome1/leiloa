package io.github.horaciocome1.leiloa.ui.product.id.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputLayout
import io.github.horaciocome1.leiloa.R
import io.github.horaciocome1.leiloa.databinding.FragmentProductIdRegisterBinding

/**
 * Fragment responsible for handling inputs for product
 * and registering the product to the database
 */
class ProductIdRegisterFragment : Fragment() {

    private lateinit var binding: FragmentProductIdRegisterBinding

    private val viewModel: ProductIdRegisterViewModel by lazy {
        ViewModelProvider(this)[ProductIdRegisterViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProductIdRegisterBinding
            .inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.registerButton.setOnClickListener(this::register)
    }

    override fun onStart() {
        super.onStart()
        setCompanyDomainToViewModel()
        initTextFields()
    }

    private fun setCompanyDomainToViewModel() = arguments?.let {
        viewModel.companyDomain = ProductIdRegisterFragmentArgs.fromBundle(it)
            .companyDomain
    }

    private fun initTextFields() {
        viewModel.productId.observe(this, Observer {
            alertIfEmpty(binding.productIdTextInputLayout, it)
        })
        viewModel.termsAndConditions.observe(this, Observer {
            alertIfEmpty(binding.productIdTextInputLayout, it)
        })
        viewModel.startPrice.observe(this, Observer {
            alertIfEmpty(binding.productIdTextInputLayout, it)
        })
    }

    private fun alertIfEmpty(textInputLayout: TextInputLayout, companyDomain: String) {
        textInputLayout.error = if (companyDomain.isNotBlank())
            null
        else
            getString(R.string.cannot_be_blank)
    }

    private fun register(view: View) {
        if (
            binding.productIdTextInputLayout.editText?.text.isNullOrBlank() ||
            binding.termsAndConditionsTextInputLayout.editText?.text.isNullOrBlank() ||
            binding.startPriceTextInputLayout.editText?.text.isNullOrBlank()
        ) return
        view.isEnabled = false
        binding.progressBar.visibility = View.VISIBLE
        val startActive = binding.startEnabledSwitch.isChecked
        lifecycleScope.launchWhenStarted {
            val isSuccessful = viewModel.registerProductAsync(view, startActive)
                .await()
            if (!isSuccessful) {
                binding.productIdTextInputLayout.error =
                    getString(R.string.product_id_is_not_available)
                binding.progressBar.visibility = View.GONE
                view.isEnabled = true
            }
        }
    }

}
