package com.example.vonic

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.transition.ChangeBounds
import androidx.transition.Fade
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import com.example.vonic.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var hasVoted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWindowInsets()
        setupHeader()
        setupActivePoll()
        setupFaqAccordion()
        setupHistorySection()
        setupBottomNavigation()
    }

    /**
     * Mencegah overlap status bar / camera punch-hole dan gesture navigation bar.
     * Status bar tinggi diterapkan ke top padding headerBar,
     * dan navigation bar tinggi diterapkan ke bottom padding bottomNavigation.
     */
    private fun setupWindowInsets() {
        val originalHeaderPaddingTop = binding.headerBar.paddingTop
        val originalNavPaddingBottom = binding.bottomNavigation.paddingBottom

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.headerBar.updatePadding(
                top = originalHeaderPaddingTop + systemBars.top
            )
            binding.bottomNavigation.updatePadding(
                bottom = originalNavPaddingBottom + systemBars.bottom
            )
            insets
        }
    }

    private fun setupHeader() {
        // Format nama ringkas: kata pertama + inisial huruf pertama kata terakhir (misal "Melisa D.")
        binding.tvUserName.text = formatDisplayName("Melisa Danuar, S.Kom")

        // Kunci lebar dropdown secara dinamis agar 100% sama persis dengan lebar chip nama
        binding.userProfileChip.post {
            if (binding.userProfileChip.width > 0) {
                val params = binding.profileDropdownCard.layoutParams
                params.width = binding.userProfileChip.width
                binding.profileDropdownCard.layoutParams = params
            }
        }

        binding.btnNotification.setOnClickListener {
            hideProfileDropdown()
            AlertDialog.Builder(this)
                .setTitle("Notifikasi")
                .setMessage("Belum ada notifikasi baru untuk pemilihan ini.")
                .setPositiveButton("Tutup", null)
                .show()
        }

        // Klik pada chip nama/identitas untuk membuka/menutup dropdown Profile & Logout
        binding.userProfileChip.setOnClickListener {
            toggleProfileDropdown()
        }

        // Opsi 1: Profile
        binding.menuProfile.setOnClickListener {
            hideProfileDropdown()
            AlertDialog.Builder(this)
                .setTitle("Profil Pemilih")
                .setMessage("Nama: Melisa Danuar, S.Kom\nRole: Dosen / Tendik DTEDI UGM\nStatus: Terverifikasi Aktif")
                .setPositiveButton("OK", null)
                .show()
        }

        // Opsi 2: Log Out
        binding.menuLogout.setOnClickListener {
            hideProfileDropdown()
            AlertDialog.Builder(this)
                .setTitle("Konfirmasi Keluar")
                .setMessage("Apakah Anda yakin ingin keluar dari akun Vonic?")
                .setPositiveButton("Log Out") { _, _ ->
                    Toast.makeText(this, "Berhasil keluar dari akun Vonic.", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Batal", null)
                .show()
        }

        // Menutup dropdown otomatis jika pengguna scroll / klik area konten
        binding.scrollView.setOnTouchListener { _, _ ->
            if (binding.profileDropdownCard.visibility == View.VISIBLE) {
                hideProfileDropdown()
            }
            false
        }
    }

    /**
     * Helper untuk memformat nama tampilan:
     * Menghilangkan gelar (setelah koma), mengambil kata pertama, dan inisial kata terakhir.
     * Contoh: "Melisa Danuar, S.Kom" -> "Melisa D."
     */
    private fun formatDisplayName(fullName: String): String {
        val nameWithoutDegree = fullName.split(",")[0].trim()
        val words = nameWithoutDegree.split("\\s+".toRegex()).filter { it.isNotEmpty() }
        return when {
            words.size >= 2 -> "${words.first()} ${words.last().first().uppercaseChar()}."
            words.size == 1 -> words[0]
            else -> fullName
        }
    }

    private fun toggleProfileDropdown() {
        if (binding.profileDropdownCard.visibility == View.VISIBLE) {
            hideProfileDropdown()
        } else {
            showProfileDropdown()
        }
    }

    private fun showProfileDropdown() {
        // Kunci lebar dropdown secara dinamis agar 100% sama persis dengan lebar chip nama
        if (binding.userProfileChip.width > 0) {
            val params = binding.profileDropdownCard.layoutParams
            params.width = binding.userProfileChip.width
            binding.profileDropdownCard.layoutParams = params
        }

        binding.profileDropdownCard.visibility = View.VISIBLE
        binding.profileDropdownCard.alpha = 0f
        binding.profileDropdownCard.animate().alpha(1f).setDuration(150).start()
        binding.ivUserChevron.animate().rotation(180f).setDuration(150).start()
    }

    private fun hideProfileDropdown() {
        binding.profileDropdownCard.animate()
            .alpha(0f)
            .setDuration(150)
            .withEndAction {
                binding.profileDropdownCard.visibility = View.GONE
            }
            .start()
        binding.ivUserChevron.animate().rotation(0f).setDuration(150).start()
    }

    private fun setupActivePoll() {
        // Teks subtitle dengan format tebal pada "Piknik DTEDI Periode 2026"
        binding.tvPollSubtitle.text = HtmlCompat.fromHtml(
            "Terima kasih telah berpartisipasi dalam <b>Piknik DTEDI Periode 2026</b>",
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )

        binding.btnVoteNow.setOnClickListener {
            showVotingDialog()
        }
    }

    private fun showVotingDialog() {
        val options = arrayOf(
            "Pilihan 1: Kaos A — Pantai Kartini, Jepara",
            "Pilihan 2: Kaos B — Pegunungan Bromo"
        )

        AlertDialog.Builder(this)
            .setTitle(if (hasVoted) "Ubah Pilihan Voting" else "Berikan Suara Anda")
            .setSingleChoiceItems(options, 0, null)
            .setPositiveButton(if (hasVoted) "Simpan Perubahan" else "Kirim Suara") { dialog, _ ->
                hasVoted = true
                binding.tvVoteButtonText.text = "Ubah Pilihan Voting"
                binding.tvParticipants.text = "121"
                binding.tvParticipationStatus.text = "Selesai"
                binding.ivParticipationCheck.visibility = View.VISIBLE
                Toast.makeText(this, "Suara kamu berhasil dicatat di Vonic!", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    /**
     * FAQ Dropdown Accordion:
     * Menampilkan dan menyembunyikan box jawaban putih dengan animasi slide/fade halus (AutoTransition 250ms),
     * serta menganimasikan perputaran chevron panah:
     * - Keadaan tertutup: panah menghadap bawah (0 derajat)
     * - Keadaan terbuka: panah menghadap atas (180 derajat)
     */
    private fun setupFaqAccordion() {
        setupAccordionItem(binding.faqHeader1, binding.faqAnswer1, binding.ivFaqChevron1)
        setupAccordionItem(binding.faqHeader2, binding.faqAnswer2, binding.ivFaqChevron2)
        setupAccordionItem(binding.faqHeader3, binding.faqAnswer3, binding.ivFaqChevron3)
        setupAccordionItem(binding.faqHeader4, binding.faqAnswer4, binding.ivFaqChevron4)
    }

    private fun setupAccordionItem(header: View, answerBox: LinearLayout, chevron: ImageView) {
        // Klik pada header pertanyaan untuk membuka/menutup dropdown
        header.setOnClickListener {
            toggleFaqItem(answerBox, chevron)
        }

        // Klik pada box jawaban juga dapat menutup dropdown jika diinginkan
        answerBox.setOnClickListener {
            toggleFaqItem(answerBox, chevron)
        }
    }

    private fun toggleFaqItem(answerBox: LinearLayout, chevron: ImageView) {
        val isCurrentlyExpanded = answerBox.visibility == View.VISIBLE

        // Animasi transisi layout yang sinkron dan super mulus pada contentLayout (mencegah teks Riwayat menimpa)
        val transition = TransitionSet().apply {
            ordering = TransitionSet.ORDERING_TOGETHER
            addTransition(ChangeBounds())
            addTransition(Fade())
            duration = 220
            interpolator = FastOutSlowInInterpolator()
        }
        TransitionManager.beginDelayedTransition(binding.contentLayout, transition)

        if (isCurrentlyExpanded) {
            // Tutup dropdown (naik)
            answerBox.visibility = View.GONE
            chevron.animate().rotation(0f).setDuration(220).start()
        } else {
            // Buka dropdown (turun)
            answerBox.visibility = View.VISIBLE
            chevron.animate().rotation(180f).setDuration(220).start()
        }
    }

    private fun setupHistorySection() {
        binding.btnSeeAllHistory.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }

        binding.btnViewHistoryResult1.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Hasil Piknik DTEDI 2025")
                .setMessage("Destinasi Terpilih: Pantai Indrayanti, Yogyakarta\nTotal Partisipan: 200 orang")
                .setPositiveButton("Tutup", null)
                .show()
        }

        binding.btnViewHistoryResult2.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Hasil Piknik DTEDI 2024")
                .setMessage("Destinasi Terpilih: Kaliurang Park, Sleman\nTotal Partisipan: 185 orang")
                .setPositiveButton("Tutup", null)
                .show()
        }
    }

    private fun setupBottomNavigation() {
        binding.navBeranda.setOnClickListener {
            binding.scrollView.smoothScrollTo(0, 0)
        }

        binding.navRiwayat.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }
    }
}
