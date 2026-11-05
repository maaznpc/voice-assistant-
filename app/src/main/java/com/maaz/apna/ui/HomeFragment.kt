package com.maaz.apna.ui

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.maaz.apna.CrashReporter
import com.maaz.apna.R
import com.maaz.apna.Settings
import com.maaz.apna.databinding.FragmentHomeBinding
import com.maaz.apna.service.ApnaService
import com.maaz.apna.service.ApnaService.Companion.Phase

class HomeFragment : Fragment() {

    private var _ui: FragmentHomeBinding? = null
    private val ui get() = _ui!!
    private lateinit var settings: Settings

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View {
        _ui = FragmentHomeBinding.inflate(inflater, container, false)
        settings = Settings(requireContext())
        ui.powerButton.setOnClickListener {
            if (ApnaService.running) ApnaService.stop(requireContext()) else startApna()
            ui.root.postDelayed({ if (_ui != null) refresh() }, 400)
        }
        ui.abortButton.setOnClickListener { if (ApnaService.running) startApna(ApnaService.ACTION_STOP_TASK) }
        ui.talkButton.setOnClickListener { startApna(ApnaService.ACTION_LISTEN) }
        ui.sendButton.setOnClickListener { runTypedCommand() }
        ui.commandInput.setOnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_SEND) { runTypedCommand(); true } else false
        }
        ui.crashShare.setOnClickListener {
            val report = CrashReporter.pending(requireContext()) ?: return@setOnClickListener
            startActivity(android.content.Intent.createChooser(
                android.content.Intent(android.content.Intent.ACTION_SEND).setType("text/plain")
                    .putExtra(android.content.Intent.EXTRA_SUBJECT, "Apna crash report")
                    .putExtra(android.content.Intent.EXTRA_TEXT, report),
                "Share crash report",
            ))
            CrashReporter.dismiss(requireContext())
            refresh()
        }
        ui.crashDismiss.setOnClickListener { CrashReporter.dismiss(requireContext()); refresh() }
        ui.modelPill.setOnClickListener { (activity as? MainActivity)?.goTo(R.id.nav_models) }
        ui.setupBanner.setOnClickListener {
            val needsModel = settings.primary() == null
            val needsPermissions = Setup.remaining(requireContext()).any { it.required }
            (activity as? MainActivity)?.goTo(if (needsModel && !needsPermissions) R.id.nav_models else R.id.nav_settings)
        }
        return ui.root
    }

    override fun onResume() {
        super.onResume()
        if (_ui == null) return
        ui.reactor.animate = true
        ApnaService.statusListener = { if (_ui != null) refresh() }
        refresh()
    }

    override fun onPause() {
        super.onPause()
        ApnaService.statusListener = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _ui = null
    }

    private fun refresh() {
        val ctx = requireContext()
        val running = ApnaService.running
        val phase = if (running) ApnaService.phase else Phase.OFF
        ui.reactor.mode = when (phase) {
            Phase.OFF -> ArcReactorView.Mode.OFF
            Phase.IDLE -> ArcReactorView.Mode.IDLE
            Phase.LISTENING -> ArcReactorView.Mode.LISTENING
            Phase.WORKING -> ArcReactorView.Mode.WORKING
        }
        ui.statusLabel.text = when (phase) {
            Phase.OFF -> "Off"
            Phase.IDLE -> "Ready"
            Phase.LISTENING -> "Listening"
            Phase.WORKING -> "Working"
        }
        ui.statusText.text = if (running) ApnaService.status else "Turn Apna on to start listening"
        val last = ApnaService.lastResult
        ui.lastResult.visibility = if (last != null && phase != Phase.WORKING) View.VISIBLE else View.GONE
        ui.lastResult.text = "Last task: $last"
        ui.abortButton.visibility = if (phase == Phase.WORKING) View.VISIBLE else View.GONE

        ui.powerButton.text = if (running) "Turn off" else "Turn on"
        // Primary button when off; quiet outlined button when on.
        ui.powerButton.backgroundTintList = ColorStateList.valueOf(ctx.color(if (running) R.color.surface_high else R.color.accent))
        val fg = ctx.color(if (running) R.color.text else R.color.on_accent)
        ui.powerButton.setTextColor(fg)
        ui.powerButton.iconTint = ColorStateList.valueOf(fg)

        ui.crashCard.visibility = if (CrashReporter.pending(ctx) != null) View.VISIBLE else View.GONE

        val primary = settings.primary()
        ui.modelPill.text = primary?.name ?: "Add a model"

        val missing = Setup.remaining(ctx).filter { it.required }.map { it.title.lowercase() } +
            (if (primary == null) listOf("an AI model") else emptyList())
        ui.setupBanner.visibility = if (missing.isEmpty()) View.GONE else View.VISIBLE
        ui.setupBannerText.text = "Apna still needs ${missing.joinToString(" and ")}. Tap to finish setup."
    }

    private fun runTypedCommand() {
        val task = ui.commandInput.text.toString().trim()
        if (task.isEmpty()) return
        // Get out of the way so the agent can use the phone.
        if (startApna(ApnaService.ACTION_RUN, task)) {
            ui.commandInput.setText("")
            requireActivity().moveTaskToBack(true)
        }
    }

    private fun startApna(action: String? = null, task: String? = null): Boolean {
        val started = ApnaService.start(requireContext(), action, task)
        if (!started) {
            Toast.makeText(requireContext(), "Apna needs microphone permission first.", Toast.LENGTH_LONG).show()
            requireActivity().requestPermissions(Setup.PERMISSIONS, 1)
        }
        return started
    }
}
