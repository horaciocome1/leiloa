package io.github.horaciocome1.leiloa.ui.product.id

import android.os.Bundle
import android.text.InputFilter
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import io.github.horaciocome1.leiloa.R
import io.github.horaciocome1.leiloa.databinding.FragmentProductIdBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * fragment responsible for handling the informing productId step
 * it checks if productId is real
 * and navigates to the next step if it is
 */
class ProductIdFragment : Fragment() {

    private lateinit var binding: FragmentProductIdBinding

    private val viewModel: ProductIdViewModel by lazy {
        ViewModelProvider(this)[ProductIdViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProductIdBinding
            .inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.nextButton.setOnClickListener(this::navigate)
    }

    override fun onStart() {
        super.onStart()
        setCompanyDomainToViewModel()
        setRegisterButtonVisibility()
        viewModel.productId.observe(this, Observer { alertIfEmpty(it) })
        updateMaxTextLength()
    }

    private fun setCompanyDomainToViewModel() = arguments?.let {
        viewModel.companyDomain = ProductIdFragmentArgs.fromBundle(it)
            .companyDomain
    }

    private fun setRegisterButtonVisibility() =
        lifecycleScope.launchWhenStarted {
            val belongsToMe = viewModel.doDomainBelongToMeAsync()
                .await()
            if (belongsToMe)
                binding.registerButton.visibility = View.VISIBLE
        }

    private fun alertIfEmpty(companyDomain: String) {
        binding.productIdTextInputLayout.error =
            if (companyDomain.isNotBlank())
                null
            else
                getString(R.string.cannot_be_blank)
    }

    private fun updateMaxTextLength() = lifecycleScope.launchWhenStarted {
        val maxLength = viewModel.retrieveProductIdMaxLengthAsync()
            .await()
            .toInt()
        binding.productIdTextInputLayout.counterMaxLength = maxLength
        binding.productIdTextInputLayout.editText?.filters = arrayOf(
            InputFilter.LengthFilter(maxLength)
        )
    }

    private fun navigate(view: View) {
        if (viewModel.productId.value == null)
            return
        view.isEnabled = false
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launchWhenStarted {
            val isSuccessful = viewModel.navigateToProductAsync(view)
                .await()
            if (!isSuccessful)
                binding.productIdTextInputLayout.error =
                    getString(R.string.product_id_is_not_real)
            binding.progressBar.visibility = View.GONE
            view.isEnabled = true
        }
    }

}
