package com.example.vonic

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.vonic.databinding.ActivityHistoryBinding

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private var currentPage = 1
    private val maxPages = 6

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWindowInsets()
        setupHeader()
        setupPagination()
        setupHistoryCards()
        setupBottomNavigation()
    }

    /**
     * Mencegah overlap status bar / camera punch-hole dan gesture navigation bar.
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
        // Format nama ringkas: kata pertama + inisial huruf pertama kata terakhir (Melisa D.)
        binding.tvUserName.text = formatDisplayName("Melisa Danuar, S.Kom")

        // Sinkronisasi lebar dropdown agar selalu sama persis dengan lebar chip nama
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

        binding.userProfileChip.setOnClickListener {
            toggleProfileDropdown()
        }

        binding.menuProfile.setOnClickListener {
            hideProfileDropdown()
            AlertDialog.Builder(this)
                .setTitle("Profil Pemilih")
                .setMessage("Nama: Melisa Danuar, S.Kom\nRole: Dosen / Tendik DTEDI UGM\nStatus: Terverifikasi Aktif")
                .setPositiveButton("OK", null)
                .show()
        }

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

        binding.scrollView.setOnTouchListener { _, _ ->
            if (binding.profileDropdownCard.visibility == View.VISIBLE) {
                hideProfileDropdown()
            }
            false
        }
    }

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

    private fun setupPagination() {
        updatePaginationUI()

        binding.btnPrevPage.setOnClickListener {
            if (currentPage > 1) {
                currentPage--
                updatePaginationUI()
                binding.scrollView.smoothScrollTo(0, 0)
                Toast.makeText(this, "Halaman $currentPage dari $maxPages", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Anda sudah berada di halaman pertama.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnNextPage.setOnClickListener {
            if (currentPage < maxPages) {
                currentPage++
                updatePaginationUI()
                binding.scrollView.smoothScrollTo(0, 0)
                Toast.makeText(this, "Halaman $currentPage dari $maxPages", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Anda sudah berada di halaman terakhir.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updatePaginationUI() {
        binding.tvPageBadge.text = "$currentPage/$maxPages"
        binding.btnPrevPage.alpha = if (currentPage == 1) 0.5f else 1.0f
        binding.btnNextPage.alpha = if (currentPage == maxPages) 0.5f else 1.0f
    }

    private fun setupHistoryCards() {
        binding.btnViewHistory1.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Hasil Piknik DTEDI 2025")
                .setMessage("Destinasi Terpilih: Pantai Indrayanti, Yogyakarta\nTotal Partisipan: 200 orang\nStatus: Selesai")
                .setPositiveButton("Tutup", null)
                .show()
        }

        binding.btnViewHistory2.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Hasil Piknik DTEDI 2024")
                .setMessage("Destinasi Terpilih: Kaliurang Park, Sleman\nTotal Partisipan: 185 orang\nStatus: Selesai")
                .setPositiveButton("Tutup", null)
                .show()
        }

        binding.btnViewHistory3.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Hasil Piknik DTEDI 2023")
                .setMessage("Destinasi Terpilih: Pantai Parangtritis & Mangunan, Bantul\nTotal Partisipan: 192 orang\nStatus: Selesai")
                .setPositiveButton("Tutup", null)
                .show()
        }
    }

    private fun setupBottomNavigation() {
        // Tab Beranda: Kembali ke MainActivity
        binding.navBeranda.setOnClickListener {
            finish()
        }

        // Tab Riwayat: Scroll ke paling atas halaman riwayat ini
        binding.navRiwayat.setOnClickListener {
            binding.scrollView.smoothScrollTo(0, 0)
        }
    }
}
