
package com.example.vonic

import android.widget.ImageView
import android.widget.FrameLayout
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val blue = Color.rgb(18, 99, 244)
    private val darkBlue = Color.rgb(14, 43, 105)
    private val paleBlue = Color.rgb(239, 245, 255)
    private val border = Color.rgb(210, 223, 247)
    private val ink = Color.rgb(28, 42, 69)
    private val muted = Color.rgb(105, 119, 145)
    private val green = Color.rgb(220, 248, 225)

    private var hasVoted = false
    private var tab = "Beranda"

    private lateinit var content: LinearLayout
    private lateinit var navigation: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = Color.WHITE
        window.navigationBarColor = Color.WHITE
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or
                    View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.rgb(250, 251, 255))
        }

        val scroll = ScrollView(this)

        content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(14), dp(18), dp(20))
        }

        scroll.addView(content)

        navigation = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setBackgroundColor(Color.WHITE)
            setPadding(dp(4), dp(6), dp(4), dp(6))
        }

        root.addView(scroll, LinearLayout.LayoutParams(-1, 0, 1f))
        root.addView(navigation, LinearLayout.LayoutParams(-1, dp(62)))

        setContentView(root)
        renderPage()
    }

    private fun renderPage() {
        content.removeAllViews()

        when (tab) {
            "Beranda" -> homePage()
            "Riwayat" -> historyPage()
            "Hasil" -> resultPage()
            "FAQ" -> faqPage()
            "Akun" -> accountPage()
        }

        renderNavigation()
    }


    private fun header() {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, dp(6), 0, dp(24))
        }

        // Logo Vonic berbentuk gambar
        val logo = ImageView(this).apply {
            setImageResource(R.drawable.logo_vonic)
            scaleType = ImageView.ScaleType.FIT_START
            contentDescription = "Logo Vonic"
        }

        row.addView(
            logo,
            LinearLayout.LayoutParams(dp(115), dp(38)).apply {
                weight = 1f
            }
        )

        // Tombol notifikasi di kanan
        val notification = FrameLayout(this).apply {
            background = rounded(Color.TRANSPARENT, 12)
            isClickable = true
            isFocusable = true
            contentDescription = "Notifikasi"
            setOnClickListener {
                AlertDialog.Builder(this@MainActivity)
                    .setTitle("Notifikasi")
                    .setMessage("Belum ada notifikasi baru.")
                    .setPositiveButton("Tutup", null)
                    .show()
            }
        }

        val bell = ImageView(this).apply {
            setImageResource(android.R.drawable.ic_popup_reminder)
            setColorFilter(darkBlue)
            contentDescription = "Ikon notifikasi"
            scaleType = ImageView.ScaleType.CENTER_INSIDE
        }

        notification.addView(
            bell,
            FrameLayout.LayoutParams(dp(28), dp(28), Gravity.CENTER)
        )

        row.addView(
            notification,
            LinearLayout.LayoutParams(dp(38), dp(38))
        )

        content.addView(row)
    }


    // BERANDA

    private fun homePage() {
        header()

        sectionTitle("Poll Aktif")

        if (hasVoted) {
            votedCard()
        } else {
            activePollCard()
        }

        space(22)

        sectionTitle("Pertanyaan Umum (FAQ)") {
            tab = "FAQ"
            renderPage()
        }

        faqItem("Mengapa saya harus vote?")
        faqItem("Bagaimana cara mengubah vote?")
        faqItem("Kapan hasil voting diumumkan?")

        space(22)

        sectionTitle("Riwayat Voting Saya") {
            tab = "Riwayat"
            renderPage()
        }

        historyItem("Piknik DTEDI Periode 2025", "10 Des 2025")
        historyItem("Piknik DTEDI Periode 2024", "5 Nov 2024")
    }

    private fun activePollCard() {
        val box = card()

        box.addView(text("Piknik DTEDI\nPeriode 2026", 23f, blue, true))
        spaceIn(box, 8)

        box.addView(
            text(
                "Ayo pilih desain kaos dan destinasi wisata favoritmu!",
                14f,
                ink
            )
        )

        spaceIn(box, 14)

        box.addView(
            pill(
                "● Berakhir dalam 23 : 48 : 15",
                Color.rgb(255, 232, 232),
                Color.rgb(195, 35, 35)
            )
        )

        spaceIn(box, 16)

        box.addView(infoRow())

        spaceIn(box, 14)

        box.addView(primaryButton("Berikan Vote") {
            showVotingDialog()
        })

        content.addView(box)
    }

    private fun votedCard() {
        val box = card()

        box.addView(text("Piknik DTEDI\nPeriode 2026", 23f, blue, true))
        spaceIn(box, 12)

        box.addView(
            message(
                "✓  Terima kasih!\nVote kamu sudah terekam.",
                green,
                Color.rgb(15, 110, 45)
            )
        )

        spaceIn(box, 10)

        box.addView(
            text(
                "Kamu masih bisa mengubah vote sebelum batas waktu berakhir.",
                14f,
                ink
            )
        )

        spaceIn(box, 14)
        box.addView(infoRow())
        spaceIn(box, 12)

        box.addView(outlineButton("✎  Ubah Vote") {
            showVotingDialog()
        })

        content.addView(box)
    }

    private fun infoRow(): View {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(dp(10), dp(12), dp(10), dp(12))
            background = rounded(paleBlue, 12)
        }

        row.addView(
            text("▦  Periode\n1 Sep – 12 Sep 2026", 12f, darkBlue),
            LinearLayout.LayoutParams(0, -2, 1f)
        )

        row.addView(
            text("♙  Partisipan\n120 orang", 12f, darkBlue),
            LinearLayout.LayoutParams(0, -2, 1f)
        )

        return row
    }

    // RIWAYAT VOTING

    private fun historyPage() {
        header()
        sectionTitle("Riwayat Voting Saya")

        historyItem("Piknik DTEDI Periode 2025", "10 Des 2025")
        historyItem("Piknik DTEDI Periode 2024", "5 Nov 2024")
        historyItem("Piknik DTEDI Periode 2023", "8 Des 2023")
    }

    private fun historyItem(name: String, date: String) {
        val box = card()

        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val details = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        details.addView(text(name, 14f, darkBlue, true))
        spaceIn(details, 5)
        details.addView(text("Hasil diumumkan • $date", 12f, muted))

        row.addView(details, LinearLayout.LayoutParams(0, -2, 1f))

        row.addView(
            pill("Selesai", green, Color.rgb(15, 110, 45))
        )

        box.addView(row)
        content.addView(box)
        space(8)
    }

    // HASIL VOTING

    private fun resultPage() {
        header()
        sectionTitle("Hasil Voting")

        val box = card()

        box.addView(text("Piknik DTEDI Periode 2026", 21f, darkBlue, true))
        spaceIn(box, 10)

        box.addView(
            pill("Hasil Telah Diumumkan", green, Color.rgb(15, 110, 45))
        )

        spaceIn(box, 18)

        box.addView(text("♧  Desain Kaos Terpilih", 16f, darkBlue, true))
        spaceIn(box, 8)

        box.addView(
            message("KAOS A\n\n             VONIC", paleBlue, blue)
        )

        spaceIn(box, 20)

        box.addView(text("♙  Destinasi Terpilih", 16f, darkBlue, true))
        spaceIn(box, 8)

        box.addView(text("Pantai Lorem Ipsum   ★ 4,8", 17f, darkBlue, true))
        spaceIn(box, 10)

        box.addView(
            message(
                "🏝\n\nPantai dengan pasir putih dan air laut biru",
                paleBlue,
                blue
            )
        )

        spaceIn(box, 14)
        box.addView(text("Lokasi", 12f, muted, true))
        box.addView(
            text("Pantai Kartini, Jepara, Jawa Tengah", 14f, ink)
        )

        spaceIn(box, 12)
        box.addView(text("Jarak: 120 Km", 14f, ink))
        box.addView(text("Lama perjalanan: 2 Hari", 14f, ink))

        spaceIn(box, 12)
        box.addView(text("Benefit", 13f, muted, true))
        box.addView(text("Bisa • Makan • Minum • Kamar 2 hari", 13f, ink))

        spaceIn(box, 16)

        box.addView(
            message(
                "Perkiraan Biaya per Orang\nRp 500.000",
                paleBlue,
                blue
            )
        )

        content.addView(box)
    }

    // FAQ

    private fun faqPage() {
        header()
        sectionTitle("Pertanyaan Umum (FAQ)")

        faqItem("Mengapa saya harus vote?")
        faqItem("Bagaimana cara memberikan suara?")
        faqItem("Apakah pilihan bisa diubah setelah dikirim?")
        faqItem("Kapan hasil voting diumumkan?")
        faqItem("Apa yang terjadi jika belum sempat memilih?")
    }

    private fun faqItem(question: String) {
        val box = card()
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        row.addView(text("ⓘ", 19f, blue, true))

        row.addView(
            text(question, 13f, ink),
            LinearLayout.LayoutParams(0, -2, 1f)
        )

        row.addView(text("⌄", 18f, darkBlue))
        box.addView(row)

        box.setOnClickListener {
            val answer = when {
                question.contains("mengubah") ||
                        question.contains("diubah") ->
                    "Pilihan dapat diubah selama periode voting masih berlangsung."

                question.contains("Kapan") ->
                    "Hasil voting diumumkan setelah periode voting berakhir."

                question.contains("belum") ->
                    "Voting akan ditutup otomatis saat tenggat waktu tercapai."

                else ->
                    "Pilih polling yang aktif, tentukan pilihanmu, lalu kirim vote."
            }

            AlertDialog.Builder(this)
                .setTitle(question)
                .setMessage(answer)
                .setPositiveButton("Mengerti", null)
                .show()
        }

        content.addView(box)
        space(7)
    }

    // AKUN

    private fun accountPage() {
        header()
        sectionTitle("Akun Saya")

        val box = card()

        box.addView(text("Melisa Danar, S.Kom", 20f, darkBlue, true))
        spaceIn(box, 8)
        box.addView(text("Pengguna Vonic", 14f, muted))

        content.addView(box)
    }

    // DIALOG VOTING

    private fun showVotingDialog() {
        val choices = arrayOf(
            "Kaos A — Pantai Lorem Ipsum",
            "Kaos B — Pegunungan"
        )

        AlertDialog.Builder(this)
            .setTitle("Pilih favoritmu")
            .setSingleChoiceItems(choices, 0, null)
            .setPositiveButton(
                if (hasVoted) "Simpan Perubahan" else "Kirim Vote"
            ) { _, _ ->
                hasVoted = true
                renderPage()

                AlertDialog.Builder(this)
                    .setMessage("Vote kamu berhasil disimpan!")
                    .setPositiveButton("OK", null)
                    .show()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    // BOTTOM NAVIGATION


    private fun renderNavigation() {
        navigation.removeAllViews()

        navigation.gravity = Gravity.CENTER
        navigation.orientation = LinearLayout.HORIZONTAL
        navigation.setPadding(dp(4), dp(5), dp(4), dp(5))
        navigation.setBackgroundColor(Color.WHITE)

        val tabs = listOf("Beranda", "Riwayat", "Hasil", "FAQ", "Akun")
        val icons = listOf("⌂", "▣", "▥", "?", "♙")

        tabs.forEachIndexed { index, name ->
            val selected = tab == name

            val item = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                setPadding(dp(2), dp(3), dp(2), dp(3))
                isClickable = true
                isFocusable = true

                setOnClickListener {
                    tab = name
                    renderPage()
                }
            }

            val icon = TextView(this).apply {
                text = icons[index]
                textSize = 21f
                gravity = Gravity.CENTER
                setTextColor(if (selected) blue else muted)
                setTypeface(
                    Typeface.DEFAULT,
                    if (selected) Typeface.BOLD else Typeface.NORMAL
                )
            }

            val label = TextView(this).apply {
                text = name
                textSize = 10f
                gravity = Gravity.CENTER
                setTextColor(if (selected) blue else muted)
                setPadding(0, dp(2), 0, 0)
            }

            item.addView(
                icon,
                LinearLayout.LayoutParams(-1, dp(25))
            )

            item.addView(
                label,
                LinearLayout.LayoutParams(-1, dp(17))
            )

            navigation.addView(
                item,
                LinearLayout.LayoutParams(0, -1, 1f)
            )
        }
    }

    // KOMPONEN UI

    private fun sectionTitle(
        title: String,
        action: (() -> Unit)? = null
    ) {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, dp(4), 0, dp(10))
        }

        row.addView(
            text(title, 16f, darkBlue, true),
            LinearLayout.LayoutParams(0, -2, 1f)
        )

        if (action != null) {
            val more = text("Lihat semua", 11f, blue)
            more.setOnClickListener { action() }
            row.addView(more)
        }

        content.addView(row)
    }

    private fun card(): LinearLayout =
        LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(14), dp(15), dp(14), dp(15))
            background = rounded(Color.WHITE, 16, border)
            elevation = dp(2).toFloat()

            layoutParams = LinearLayout.LayoutParams(-1, -2).apply {
                bottomMargin = dp(8)
            }
        }

    private fun primaryButton(
        title: String,
        action: () -> Unit
    ) = Button(this).apply {
        text = title
        isAllCaps = false
        textSize = 14f
        setTextColor(Color.WHITE)
        background = rounded(blue, 10)
        setOnClickListener { action() }
    }

    private fun outlineButton(
        title: String,
        action: () -> Unit
    ) = Button(this).apply {
        text = title
        isAllCaps = false
        textSize = 14f
        setTextColor(blue)
        background = rounded(Color.WHITE, 10, border)
        setOnClickListener { action() }
    }

    private fun pill(
        title: String,
        bg: Int,
        fg: Int
    ) = text(title, 11f, fg, true).apply {
        background = rounded(bg, 24)
        setPadding(dp(10), dp(7), dp(10), dp(7))
    }

    private fun message(
        title: String,
        bg: Int,
        fg: Int
    ) = text(title, 14f, fg).apply {
        background = rounded(bg, 12)
        setPadding(dp(12), dp(14), dp(12), dp(14))
    }

    private fun text(
        value: String,
        size: Float,
        color: Int,
        bold: Boolean = false
    ) = TextView(this).apply {
        text = value
        textSize = size
        setTextColor(color)
        if (bold) setTypeface(typeface, Typeface.BOLD)
        setLineSpacing(dp(2).toFloat(), 1f)
    }

    private fun rounded(
        color: Int,
        radius: Int,
        stroke: Int = Color.TRANSPARENT
    ) = GradientDrawable().apply {
        setColor(color)
        cornerRadius = dp(radius).toFloat()

        if (stroke != Color.TRANSPARENT) {
            setStroke(dp(1), stroke)
        }
    }

    private fun space(height: Int) {
        content.addView(View(this), LinearLayout.LayoutParams(1, dp(height)))
    }

    private fun spaceIn(parent: LinearLayout, height: Int) {
        parent.addView(View(this), LinearLayout.LayoutParams(1, dp(height)))
    }

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()
}

