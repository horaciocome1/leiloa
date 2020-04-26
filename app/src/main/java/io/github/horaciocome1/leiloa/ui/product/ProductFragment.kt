package io.github.horaciocome1.leiloa.ui.product

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
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

    private lateinit var binding: FragmentProductBinding

    private val viewModel: ProductViewModel by lazy {
        ViewModelProvider(this)[ProductViewModel::class.java]
    }

    private val recyclerViewAdapter: ParticipantsAdapter by lazy {
        ParticipantsAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
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
        buttonView?.isEnabled = false
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
            buttonView?.isEnabled = true
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
                message += "${getString(R.string.start_price)}: " +
                        "${it.startPrice}"
                message += " | ${getString(R.string.actual_price)}: " +
                        "${it.price}\n\n"
                message += "${getString(R.string.invite_to_app)} " +
                        getString(R.string.project_url)
            }
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, message)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, viewModel.productId)
            startActivity(shareIntent)
            view.isEnabled = true
        }
    }

}
