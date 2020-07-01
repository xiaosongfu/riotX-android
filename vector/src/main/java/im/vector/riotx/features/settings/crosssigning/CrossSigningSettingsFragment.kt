/*
 * Copyright 2020 New Vector Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package im.vector.riotx.features.settings.crosssigning

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import im.vector.riotx.R
import im.vector.riotx.core.extensions.cleanup
import im.vector.riotx.core.extensions.configureWith
import im.vector.riotx.core.extensions.exhaustive
import im.vector.riotx.core.platform.VectorBaseActivity
import im.vector.riotx.core.platform.VectorBaseFragment
import kotlinx.android.synthetic.main.fragment_generic_recycler.*
import javax.inject.Inject

class CrossSigningSettingsFragment @Inject constructor(
        private val controller: CrossSigningSettingsController,
        val viewModelFactory: CrossSigningSettingsViewModel.Factory
) : VectorBaseFragment(), CrossSigningSettingsController.InteractionListener {

    override fun getLayoutResId() = R.layout.fragment_generic_recycler

    private val viewModel: CrossSigningSettingsViewModel by fragmentViewModel()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.observeViewEvents {
            when (it) {
                is CrossSigningSettingsViewEvents.Failure    -> {
                    AlertDialog.Builder(requireContext())
                            .setTitle(R.string.dialog_title_error)
                            .setMessage(errorFormatter.toHumanReadable(it.throwable))
                            .setPositiveButton(R.string.ok, null)
                            .show()
                    Unit
                }
                CrossSigningSettingsViewEvents.VerifySession -> {
                    navigator.waitSessionVerification(requireActivity())
                }
                CrossSigningSettingsViewEvents.SetUpRecovery -> {
                    navigator.upgradeSessionSecurity(requireActivity(), false)
                }
                CrossSigningSettingsViewEvents.SetupCrossSigning -> {
                    navigator.upgradeSessionSecurity(requireActivity(), true)
                }
            }.exhaustive
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as? VectorBaseActivity)?.supportActionBar?.setTitle(R.string.encryption_information_cross_signing_state)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    override fun invalidate() = withState(viewModel) { state ->
        controller.setData(state)
    }

    private fun setupRecyclerView() {
        recyclerView.configureWith(controller, hasFixedSize = false, disableItemAnimation = true)
        controller.interactionListener = this
    }

    override fun onDestroyView() {
        recyclerView.cleanup()
        controller.interactionListener = null
        super.onDestroyView()
    }

    override fun setupRecovery() {
        viewModel.handle(CrossSigningSettingsAction.SetUpRecovery)
    }

    override fun verifySession() {
        viewModel.handle(CrossSigningSettingsAction.VerifySession)
    }

    override fun initCrossSigning() {
        viewModel.handle(CrossSigningSettingsAction.SetupCrossSigning)
    }
}
