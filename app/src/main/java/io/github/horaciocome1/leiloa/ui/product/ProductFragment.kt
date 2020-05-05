package io.github.horaciocome1.leiloa.ui.product

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import io.github.horaciocome1.leiloa.R
import io.github.horaciocome1.leiloa.data.company.Company
import io.github.horaciocome1.leiloa.databinding.FragmentProductBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect

/**
 * Fragment responsible for displaying the product, listing participants
 * and handling participants interactions
 */
class ProductFragment : Fragment(), CompoundButton.OnCheckedChangeListener {

    companion object {

        private const val ANALYTICS_ITEM_ID = "share"
        private const val ANALYTICS_ITEM_NAME = "Sharing product"
        private const val ANALYTICS_CONTENT_TYPE = "share"

    }

    private lateinit var binding: FragmentProductBinding

    private val viewModel: ProductViewModel by lazy {
        ViewModelProvider(this)[ProductViewModel::class.java]
    }

    private val recyclerViewAdapter: ParticipantsAdapter by lazy {
        ParticipantsAdapter()
    }

    private val analytics: FirebaseAnalytics by lazy {
        FirebaseAnalytics.getInstance(requireContext())
    }

    private val auctionIsNotActiveSnackbar: Snackbar by lazy {
        Snackbar.make(
            binding.root,
            R.string.auction_not_active,
            Snackbar.LENGTH_INDEFINITE
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProductBinding
            .inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        initUI()
    }

    @ExperimentalCoroutinesApi
    override fun onStart() {
        super.onStart()
        setArgsToViewModel()
        binding.company = Company(id = viewModel.companyDomain)
        collectProduct()
        collectParticipants()
        enableSwitch()
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        lifecycleScope.launchWhenStarted {
            buttonView?.text = if (isChecked) {
                viewModel.setActiveStatusAsync()
                    .await()
                getString(R.string.active)
            } else {
                viewModel.setActiveStatusAsync(false)
                    .await()
                getString(R.string.inactive)
            }
        }
    }

    private fun initUI() {
        binding.contentInclude.recyclerView.adapter = recyclerViewAdapter
        binding.contentInclude.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.contentInclude.include.isActiveSwitch.setOnCheckedChangeListener(this)
        binding.contentInclude.include.shareButton.setOnClickListener(this::share)
    }

    private fun setArgsToViewModel() = arguments?.let {
        val args = ProductFragmentArgs.fromBundle(it)
        viewModel.companyDomain = args.companyDomain
        viewModel.productId = args.productId
        binding.viewModel = viewModel
    }

    @ExperimentalCoroutinesApi
    private fun collectProduct() =
        lifecycleScope.launchWhenStarted {
            viewModel.watchProduct().collect {
                if (it.active)
                    auctionIsNotActiveSnackbar.dismiss()
                else
                    auctionIsNotActiveSnackbar.show()
                viewModel.productActive = it.active
                binding.viewModel = viewModel
                binding.product = it
            }
        }

    @ExperimentalCoroutinesApi
    private fun collectParticipants() =
        lifecycleScope.launchWhenStarted {
            viewModel.watchParticipants().collect {
                recyclerViewAdapter.participants = it
            }
        }

    private fun enableSwitch() =
        lifecycleScope.launchWhenStarted {
            val belongsToMe = viewModel.doDomainBelongToMeAsync()
                    .await()
            binding.contentInclude.include.isActiveSwitch.isEnabled = belongsToMe
            if (belongsToMe)
                binding.contentInclude.include.shareButton.visibility = View.VISIBLE
        }

    private fun share(view: View) {
        view.isEnabled = false
        lifecycleScope.launchWhenStarted {
            val auth = FirebaseAuth.getInstance()
            var message = "${auth.currentUser!!.displayName} " +
                    "${getString(R.string.invite)}\n\n\n"
            binding.product?.let {
                message += "*${getString(R.string.domain)}:* " +
                        "${viewModel.companyDomain}\n"
                message += "*${getString(R.string.product_id)}:* " +
                        "${it.id}\n\n"
                message += "${getString(R.string.terms_and_conditions)}: " +
                        "${it.termsAndConditions}\n\n"
                message += "${getString(R.string.start_offer)}: " +
                        "${it.startOffer}"
                message += " | ${getString(R.string.actual_price)}: " +
                        "${it.topOffer}\n\n"
                message += "${getString(R.string.invite_to_app)} " +
                        getString(R.string.download_url)
            }
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, message)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, viewModel.productId)
            startActivity(shareIntent)
            logEvent()
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
