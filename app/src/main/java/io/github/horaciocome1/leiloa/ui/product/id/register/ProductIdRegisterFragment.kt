package io.github.horaciocome1.leiloa.ui.product.id.register

import android.os.Bundle
import android.text.InputFilter
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.analytics.FirebaseAnalytics
import io.github.horaciocome1.leiloa.R
import io.github.horaciocome1.leiloa.databinding.FragmentProductIdRegisterBinding
import kotlinx.coroutines.Deferred

/**
 * Fragment responsible for handling inputs for product
 * and registering the product to the database
 */
class ProductIdRegisterFragment : Fragment() {

    companion object {

        private const val ANALYTICS_ITEM_ID = "registration"
        private const val ANALYTICS_ITEM_NAME = "Registering product"
        private const val ANALYTICS_CONTENT_TYPE = "registration"

    }

    private lateinit var binding: FragmentProductIdRegisterBinding

    private val viewModel: ProductIdRegisterViewModel by lazy {
        ViewModelProvider(this)[ProductIdRegisterViewModel::class.java]
    }

    private val analytics: FirebaseAnalytics by lazy {
        FirebaseAnalytics.getInstance(requireContext())
    }

    private val productIdRegisteredSnackbar: Snackbar by lazy {
        Snackbar.make(
            binding.root,
            R.string.product_id_registered,
            Snackbar.LENGTH_LONG
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
        updateActiveSwitchCheckedState()
    }

    private fun setCompanyDomainToViewModel() = arguments?.let {
        viewModel.companyDomain = ProductIdRegisterFragmentArgs.fromBundle(it)
            .companyDomain
    }

    private fun initTextFields() {
        updateMaxTextLength(
            binding.productIdTextInputLayout,
            viewModel.retrieveProductIdMaxLengthAsync()
        )
        viewModel.productId.observe(this, Observer {
            alertIfEmpty(binding.productIdTextInputLayout, it)
        })
        updateMaxTextLength(
            binding.termsAndConditionsTextInputLayout,
            viewModel.retrieveTermsAndConditionsMaxLengthAsync()
        )
        viewModel.termsAndConditions.observe(this, Observer {
            alertIfEmpty(binding.termsAndConditionsTextInputLayout, it)
        })
        updateMaxTextLength(
            binding.startOfferTextInputLayout,
            viewModel.retrieveStartPriceMaxLengthAsync()
        )
        viewModel.startOffer.observe(this, Observer {
            alertIfEmpty(binding.startOfferTextInputLayout, it)
        })
    }

    private fun updateMaxTextLength(
        textInputLayout: TextInputLayout,
        maxLengthDeferred: Deferred<Long>
    ) = lifecycleScope.launchWhenStarted {
            val maxLength = maxLengthDeferred.await()
                .toInt()
            textInputLayout.counterMaxLength = maxLength
            textInputLayout.editText?.filters = arrayOf(
                InputFilter.LengthFilter(maxLength)
            )
        }

    private fun alertIfEmpty(textInputLayout: TextInputLayout, companyDomain: String) {
        textInputLayout.error = if (companyDomain.isNotBlank())
            null
        else
            getString(R.string.cannot_be_blank)
    }

    private fun updateActiveSwitchCheckedState() =
        lifecycleScope.launchWhenStarted {
            val isActive = viewModel.retrieveStartActiveAsync()
                .await()
            binding.startEnabledSwitch.isChecked = isActive
        }

    private fun register(view: View) {
        if (
            binding.productIdTextInputLayout.editText?.text.isNullOrBlank() ||
            binding.termsAndConditionsTextInputLayout.editText?.text.isNullOrBlank() ||
            binding.startOfferTextInputLayout.editText?.text.isNullOrBlank()
        ) return
        view.isEnabled = false
        binding.progressBar.visibility = View.VISIBLE
        val startActive = binding.startEnabledSwitch.isChecked
        lifecycleScope.launchWhenStarted {
            val isSuccessful = viewModel.registerProductAsync(view, startActive)
                .await()
            if (!isSuccessful)
                binding.productIdTextInputLayout.error =
                    getString(R.string.product_id_is_not_available)
            else {
                productIdRegisteredSnackbar.show()
                logEvent()
            }
            binding.progressBar.visibility = View.GONE
            view.isEnabled = true
        }
    }

    private fun logEvent() = lifecycleScope.launchWhenStarted {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.ITEM_ID, ANALYTICS_ITEM_ID)
            putString(FirebaseAnalytics.Param.ITEM_NAME, ANALYTICS_ITEM_NAME)
            putString(FirebaseAnalytics.Param.CONTENT_TYPE, ANALYTICS_CONTENT_TYPE)
        }
        analytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, bundle)
    }

}
