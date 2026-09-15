package com.shofiqul.test_app;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // Preferences Keys
    private static final String PREFS_NAME = "HisabPrefs";
    private static final String KEY_CURRENCY = "currency_symbol";
    private static final String KEY_SHOP_NAME = "shop_name";
    private static final String KEY_SHOP_CONTACT = "shop_contact";

    // Core Database
    private DatabaseHelper dbHelper;
    private SharedPreferences prefs;
    private String currentCurrency = "৳";
    private String shopName = "আমার ব্যবসা প্রতিষ্ঠান";

    // Header Views
    private TextView tvHeaderTitle, tvHeaderSubtitle, tvActiveCurrencyBadge;

    // Navigation Tabs
    private LinearLayout navTabCalculator, navTabHistory, navTabKhata, navTabSettings;
    private ImageView ivNavCalculator, ivNavHistory, ivNavKhata, ivNavSettings;
    private TextView tvNavCalculator, tvNavHistory, tvNavKhata, tvNavSettings;
    private ScrollView layoutTabCalculator, layoutTabSettings;
    private LinearLayout layoutTabHistory, layoutTabKhata;

    // --- TAB 1: CALCULATOR VIEWS ---
    private EditText edbuy, edsell; // Required original IDs
    private Button hisabbutton;     // Required original ID
    private TextView textdisplay;   // Required original ID
    private EditText etProductTitle, etQuantity, etExtraCost, etVatPercent, etDiscountPercent;
    private TextView tvStatusBadge, tvSummaryBuy, tvSummarySell, tvSummaryMargin, tvSummaryMarkup, tvInsight;
    private LinearLayout layoutMetricGrid, layoutInsight, layoutActions;
    private ImageView ivInsight, btnResetHeader;
    private View btnReset, btnSaveTx, btnGeneratePdf, btnCopy, btnShare;
    private TextView chip10, chip15, chip20, chip25, chip30, chip50;

    private TransactionItem currentCalculatedItem = null;
    private String lastGeneratedReport = "";

    // --- TAB 2: HISTORY VIEWS ---
    private TextView tvTotalHistoryProfit, tvTotalHistoryCount, tvEmptyHistory;
    private EditText etSearchHistory;
    private RecyclerView rvHistory;
    private TransactionAdapter transactionAdapter;
    private List<TransactionItem> historyList = new ArrayList<>();

    // --- TAB 3: KHATA VIEWS ---
    private TextView tvTotalDueAmount, tvEmptyKhata;
    private EditText etSearchKhata;
    private View btnAddDue;
    private RecyclerView rvKhata;
    private DueAdapter dueAdapter;
    private List<DueItem> dueList = new ArrayList<>();

    // --- TAB 4: SETTINGS VIEWS ---
    private TextView btnCurrencyBdt, btnCurrencyUsd, btnCurrencyInr, btnCurrencySar;
    private EditText etShopName, etShopContact;
    private View btnSaveSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        dbHelper = new DatabaseHelper(this);
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        loadPreferences();

        initViews();
        setupNavigation();
        setupCalculator();
        setupHistory();
        setupKhata();
        setupSettings();

        switchTab(0); // Start on Calculator
    }

    private void loadPreferences() {
        currentCurrency = prefs.getString(KEY_CURRENCY, "৳");
        shopName = prefs.getString(KEY_SHOP_NAME, "আমার ব্যবসা প্রতিষ্ঠান");
    }

    private void initViews() {
        // Header
        tvHeaderTitle = findViewById(R.id.tvHeaderTitle);
        tvHeaderSubtitle = findViewById(R.id.tvHeaderSubtitle);
        tvActiveCurrencyBadge = findViewById(R.id.tvActiveCurrencyBadge);

        // Tab Containers
        layoutTabCalculator = findViewById(R.id.layoutTabCalculator);
        layoutTabHistory = findViewById(R.id.layoutTabHistory);
        layoutTabKhata = findViewById(R.id.layoutTabKhata);
        layoutTabSettings = findViewById(R.id.layoutTabSettings);

        // Bottom Nav Items
        navTabCalculator = findViewById(R.id.navTabCalculator);
        navTabHistory = findViewById(R.id.navTabHistory);
        navTabKhata = findViewById(R.id.navTabKhata);
        navTabSettings = findViewById(R.id.navTabSettings);

        ivNavCalculator = findViewById(R.id.ivNavCalculator);
        ivNavHistory = findViewById(R.id.ivNavHistory);
        ivNavKhata = findViewById(R.id.ivNavKhata);
        ivNavSettings = findViewById(R.id.ivNavSettings);

        tvNavCalculator = findViewById(R.id.tvNavCalculator);
        tvNavHistory = findViewById(R.id.tvNavHistory);
        tvNavKhata = findViewById(R.id.tvNavKhata);
        tvNavSettings = findViewById(R.id.tvNavSettings);

        // Tab 1 Views
        edbuy = findViewById(R.id.edbuy);
        edsell = findViewById(R.id.edsell);
        hisabbutton = findViewById(R.id.hisabbutton);
        textdisplay = findViewById(R.id.textdisplay);

        etProductTitle = findViewById(R.id.etProductTitle);
        etQuantity = findViewById(R.id.etQuantity);
        etExtraCost = findViewById(R.id.etExtraCost);
        etVatPercent = findViewById(R.id.etVatPercent);
        etDiscountPercent = findViewById(R.id.etDiscountPercent);

        tvStatusBadge = findViewById(R.id.tvStatusBadge);
        tvSummaryBuy = findViewById(R.id.tvSummaryBuy);
        tvSummarySell = findViewById(R.id.tvSummarySell);
        tvSummaryMargin = findViewById(R.id.tvSummaryMargin);
        tvSummaryMarkup = findViewById(R.id.tvSummaryMarkup);
        tvInsight = findViewById(R.id.tvInsight);
        ivInsight = findViewById(R.id.ivInsight);

        layoutMetricGrid = findViewById(R.id.layoutMetricGrid);
        layoutInsight = findViewById(R.id.layoutInsight);
        layoutActions = findViewById(R.id.layoutActions);

        btnResetHeader = findViewById(R.id.btnResetHeader);
        btnReset = findViewById(R.id.btnReset);
        btnSaveTx = findViewById(R.id.btnSaveTx);
        btnGeneratePdf = findViewById(R.id.btnGeneratePdf);
        btnCopy = findViewById(R.id.btnCopy);
        btnShare = findViewById(R.id.btnShare);

        chip10 = findViewById(R.id.chip10);
        chip15 = findViewById(R.id.chip15);
        chip20 = findViewById(R.id.chip20);
        chip25 = findViewById(R.id.chip25);
        chip30 = findViewById(R.id.chip30);
        chip50 = findViewById(R.id.chip50);

        // Tab 2 Views
        tvTotalHistoryProfit = findViewById(R.id.tvTotalHistoryProfit);
        tvTotalHistoryCount = findViewById(R.id.tvTotalHistoryCount);
        tvEmptyHistory = findViewById(R.id.tvEmptyHistory);
        etSearchHistory = findViewById(R.id.etSearchHistory);
        rvHistory = findViewById(R.id.rvHistory);

        // Tab 3 Views
        tvTotalDueAmount = findViewById(R.id.tvTotalDueAmount);
        tvEmptyKhata = findViewById(R.id.tvEmptyKhata);
        etSearchKhata = findViewById(R.id.etSearchKhata);
        btnAddDue = findViewById(R.id.btnAddDue);
        rvKhata = findViewById(R.id.rvKhata);

        // Tab 4 Views
        btnCurrencyBdt = findViewById(R.id.btnCurrencyBdt);
        btnCurrencyUsd = findViewById(R.id.btnCurrencyUsd);
        btnCurrencyInr = findViewById(R.id.btnCurrencyInr);
        btnCurrencySar = findViewById(R.id.btnCurrencySar);
        etShopName = findViewById(R.id.etShopName);
        etShopContact = findViewById(R.id.etShopContact);
        btnSaveSettings = findViewById(R.id.btnSaveSettings);

        updateCurrencyUi();
    }

    // --- NAVIGATION MANAGEMENT ---

    private void setupNavigation() {
        navTabCalculator.setOnClickListener(v -> switchTab(0));
        navTabHistory.setOnClickListener(v -> switchTab(1));
        navTabKhata.setOnClickListener(v -> switchTab(2));
        navTabSettings.setOnClickListener(v -> switchTab(3));
    }

    private void switchTab(int tabIndex) {
        layoutTabCalculator.setVisibility(tabIndex == 0 ? View.VISIBLE : View.GONE);
        layoutTabHistory.setVisibility(tabIndex == 1 ? View.VISIBLE : View.GONE);
        layoutTabKhata.setVisibility(tabIndex == 2 ? View.VISIBLE : View.GONE);
        layoutTabSettings.setVisibility(tabIndex == 3 ? View.VISIBLE : View.GONE);

        int activeColor = ContextCompat.getColor(this, R.color.primary);
        int inactiveColor = ContextCompat.getColor(this, R.color.text_muted);

        ivNavCalculator.setColorFilter(tabIndex == 0 ? activeColor : inactiveColor);
        tvNavCalculator.setTextColor(tabIndex == 0 ? activeColor : inactiveColor);

        ivNavHistory.setColorFilter(tabIndex == 1 ? activeColor : inactiveColor);
        tvNavHistory.setTextColor(tabIndex == 1 ? activeColor : inactiveColor);

        ivNavKhata.setColorFilter(tabIndex == 2 ? activeColor : inactiveColor);
        tvNavKhata.setTextColor(tabIndex == 2 ? activeColor : inactiveColor);

        ivNavSettings.setColorFilter(tabIndex == 3 ? activeColor : inactiveColor);
        tvNavSettings.setTextColor(tabIndex == 3 ? activeColor : inactiveColor);

        switch (tabIndex) {
            case 0:
                tvHeaderTitle.setText("স্মার্ট মার্জিন ও মুনাফা ক্যালকুলেটর");
                tvHeaderSubtitle.setText("সঠিক বাণিজ্যিক হিসাব ও লাভ-ক্ষতির পূর্ণাঙ্গ সমাধান");
                break;
            case 1:
                tvHeaderTitle.setText("সংরক্ষিত হিসাব ও হিস্ট্রি ড্যাশবোর্ড");
                tvHeaderSubtitle.setText("আপনার পূর্ববর্তী সকল লেনদেনের পূর্ণাঙ্গ রেকর্ড ও সামারি");
                loadHistoryData("");
                break;
            case 2:
                tvHeaderTitle.setText("ডিজিটাল বাকির খাতা ও পাওনা রিমাইন্ডার");
                tvHeaderSubtitle.setText("কাস্টমারদের বকেয়া হিসাব ও WhatsApp তাগাদা মেসেজ");
                loadKhataData("");
                break;
            case 3:
                tvHeaderTitle.setText("বিজনেস প্রোফাইল ও অ্যাপ সেটিংস");
                tvHeaderSubtitle.setText("মুদ্রা পরিবর্তন, মেমো ব্র্যান্ডিং ও ব্যক্তিগত পছন্দসমূহ");
                loadSettingsValues();
                break;
        }
    }

    // --- TAB 1: CALCULATOR SUITE ---

    private void setupCalculator() {
        applyChipPreset(chip10, 10f);
        applyChipPreset(chip15, 15f);
        applyChipPreset(chip20, 20f);
        applyChipPreset(chip25, 25f);
        applyChipPreset(chip30, 30f);
        applyChipPreset(chip50, 50f);

        hisabbutton.setOnClickListener(v -> performComprehensiveCalculation());

        View.OnClickListener resetListener = v -> resetCalculator();
        btnReset.setOnClickListener(resetListener);
        if (btnResetHeader != null) btnResetHeader.setOnClickListener(resetListener);

        btnSaveTx.setOnClickListener(v -> {
            if (currentCalculatedItem == null) {
                Toast.makeText(this, "আগে হিসাব সম্পন্ন করুন", Toast.LENGTH_SHORT).show();
                return;
            }
            long id = dbHelper.insertTransaction(currentCalculatedItem);
            if (id > 0) {
                Toast.makeText(this, "✅ হিসাব সফলভাবে সেভ করা হয়েছে!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "সেভ করা যায়নি", Toast.LENGTH_SHORT).show();
            }
        });

        btnGeneratePdf.setOnClickListener(v -> {
            if (currentCalculatedItem == null) {
                Toast.makeText(this, "আগে হিসাব সম্পন্ন করুন", Toast.LENGTH_SHORT).show();
                return;
            }
            File pdf = PdfInvoiceGenerator.generateInvoicePdf(this, currentCalculatedItem, shopName, currentCurrency);
            if (pdf != null) {
                PdfInvoiceGenerator.sharePdf(this, pdf, "ইনভয়েস - " + currentCalculatedItem.getTitle());
            } else {
                Toast.makeText(this, "পিডিএফ তৈরিতে সমস্যা হয়েছে", Toast.LENGTH_SHORT).show();
            }
        });

        btnCopy.setOnClickListener(v -> {
            if (lastGeneratedReport.isEmpty()) {
                Toast.makeText(this, "কপি করার মতো কোনো ডাটা নেই", Toast.LENGTH_SHORT).show();
                return;
            }
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Hisab Report", lastGeneratedReport);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "📋 রিপোর্ট ক্লিপবোর্ডে কপি হয়েছে!", Toast.LENGTH_SHORT).show();
        });

        btnShare.setOnClickListener(v -> {
            if (lastGeneratedReport.isEmpty()) {
                Toast.makeText(this, "শেয়ার করার মতো কোনো ডাটা নেই", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "মার্জিন রিপোর্ট");
            shareIntent.putExtra(Intent.EXTRA_TEXT, lastGeneratedReport);
            startActivity(Intent.createChooser(shareIntent, "রিপোর্ট শেয়ার করুন"));
        });
    }

    private void applyChipPreset(TextView chip, float percent) {
        chip.setOnClickListener(v -> {
            String sBuy = edbuy.getText().toString().trim();
            if (sBuy.isEmpty()) {
                edbuy.setError("প্রথমে ক্রয়মূল্য লিখুন");
                edbuy.requestFocus();
                return;
            }

            try {
                float buy = Float.parseFloat(sBuy);
                if (buy <= 0) {
                    edbuy.setError("ক্রয়মূল্য ০ এর বেশি হতে হবে");
                    edbuy.requestFocus();
                    return;
                }

                float sell = buy * (1f + (percent / 100f));
                edsell.setText(String.format(Locale.US, "%.2f", sell));
                performComprehensiveCalculation();
            } catch (NumberFormatException e) {
                edbuy.setError("সঠিক সংখ্যা দিন");
            }
        });
    }

    private void performComprehensiveCalculation() {
        String sBuy = edbuy.getText().toString().trim();
        String sSell = edsell.getText().toString().trim();

        if (sBuy.isEmpty()) {
            edbuy.setError("ক্রয়মূল্য লিখুন");
            edbuy.requestFocus();
            return;
        }

        if (sSell.isEmpty()) {
            edsell.setError("বিক্রয়মূল্য লিখুন");
            edsell.requestFocus();
            return;
        }

        try {
            float unitBuy = Float.parseFloat(sBuy);
            float unitSell = Float.parseFloat(sSell);

            if (unitBuy <= 0) {
                edbuy.setError("ক্রয়মূল্য ০ এর বেশি হতে হবে");
                edbuy.requestFocus();
                return;
            }

            int qty = 1;
            String sQty = etQuantity.getText().toString().trim();
            if (!sQty.isEmpty()) {
                qty = Math.max(1, Integer.parseInt(sQty));
            }

            float extraCost = 0f;
            String sExtra = etExtraCost.getText().toString().trim();
            if (!sExtra.isEmpty()) extraCost = Float.parseFloat(sExtra);

            float vatPercent = 0f;
            String sVat = etVatPercent.getText().toString().trim();
            if (!sVat.isEmpty()) vatPercent = Float.parseFloat(sVat);

            float discountPercent = 0f;
            String sDisc = etDiscountPercent.getText().toString().trim();
            if (!sDisc.isEmpty()) discountPercent = Float.parseFloat(sDisc);

            String title = etProductTitle.getText().toString().trim();
            if (title.isEmpty()) title = "পণ্য হিসাব #" + (System.currentTimeMillis() % 10000);

            // Calculation Core
            float totalCost = (unitBuy * qty) + extraCost;
            float subtotalSell = unitSell * qty;
            float discountAmount = subtotalSell * (discountPercent / 100f);
            float afterDiscount = subtotalSell - discountAmount;
            float vatAmount = afterDiscount * (vatPercent / 100f);
            float grossRevenue = afterDiscount + vatAmount;

            float netProfit = grossRevenue - totalCost;
            float markupPercent = totalCost > 0 ? (netProfit / totalCost) * 100f : 0f;
            float marginPercent = grossRevenue > 0 ? (netProfit / grossRevenue) * 100f : 0f;

            String dateStr = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(new Date());

            currentCalculatedItem = new TransactionItem(
                    0, title, unitBuy, unitSell, qty, extraCost, vatPercent, discountPercent,
                    netProfit, marginPercent, dateStr, System.currentTimeMillis()
            );

            // Populate Result UI
            layoutMetricGrid.setVisibility(View.VISIBLE);
            layoutInsight.setVisibility(View.VISIBLE);
            layoutActions.setVisibility(View.VISIBLE);

            tvSummaryBuy.setText(String.format(Locale.getDefault(), "%s %.2f", currentCurrency, totalCost));
            tvSummarySell.setText(String.format(Locale.getDefault(), "%s %.2f", currentCurrency, grossRevenue));
            tvSummaryMargin.setText(String.format(Locale.getDefault(), "%+.2f%%", marginPercent));
            tvSummaryMarkup.setText(String.format(Locale.getDefault(), "%+.2f%%", markupPercent));

            if (netProfit > 0) {
                int profitColor = ContextCompat.getColor(this, R.color.status_profit);
                textdisplay.setTextColor(profitColor);
                tvSummaryMargin.setTextColor(profitColor);
                tvStatusBadge.setText("✅ লাভজনক");
                tvStatusBadge.setTextColor(profitColor);
                if (ivInsight != null) ivInsight.setColorFilter(profitColor);

                String heroText = String.format(Locale.getDefault(),
                        "🎉 মোট নিট লাভ: %s %.2f\nমার্জিন: +%.2f%%  |  মার্কআপ: +%.2f%%",
                        currentCurrency, netProfit, marginPercent, markupPercent);
                textdisplay.setText(heroText);

                if (marginPercent >= 30f) {
                    tvInsight.setText("🔥 চমৎকার মার্জিন! আপনার ব্যবসা অত্যন্ত শক্তিশালী এবং সম্প্রসারণের অনুকূল।");
                } else if (marginPercent >= 15f) {
                    tvInsight.setText("✅ স্বাস্থ্যকর মার্জিন। নিয়মিত ক্যাশফ্লো ও অপারেটিং ব্যয় সমন্বয়ের জন্য আদর্শ।");
                } else {
                    tvInsight.setText("⚠️ মার্জিন কিছুটা কম। অপারেটিং খরচ কমাতে বা বিক্রয়মূল্য সামান্য বাড়াতে বিবেচনা করুন।");
                }

                lastGeneratedReport = String.format(Locale.getDefault(),
                        "📊 【%s মার্জিন রিপোর্ট】\n" +
                        "──────────────────────\n" +
                        "• পণ্য: %s (পরিমাণ: %d)\n" +
                        "• সর্বমোট খরচ: %s %.2f\n" +
                        "• সর্বমোট বিক্রয়: %s %.2f\n" +
                        "• নিট লাভ: %s %.2f\n" +
                        "• প্রফিট মার্জিন: +%.2f%%\n" +
                        "• স্ট্যাটাস: লাভজনক ✅\n" +
                        "──────────────────────\n" +
                        "তারিখ: %s",
                        shopName, title, qty, currentCurrency, totalCost, currentCurrency, grossRevenue, currentCurrency, netProfit, marginPercent, dateStr);

            } else if (netProfit < 0) {
                int lossColor = ContextCompat.getColor(this, R.color.status_loss);
                textdisplay.setTextColor(lossColor);
                tvSummaryMargin.setTextColor(lossColor);
                tvStatusBadge.setText("⚠️ লোকসান");
                tvStatusBadge.setTextColor(lossColor);
                if (ivInsight != null) ivInsight.setColorFilter(lossColor);

                String heroText = String.format(Locale.getDefault(),
                        "🚨 লোকসান চিহ্নিত: %s %.2f\nক্ষতির হার: %.2f%%",
                        currentCurrency, Math.abs(netProfit), marginPercent);
                textdisplay.setText(heroText);

                tvInsight.setText("🚨 লোকসান হচ্ছে! অবিলম্বে সরবরাহ খরচ কমান অথবা বিক্রয়মূল্য পুনঃনির্ধারণ করুন।");

                lastGeneratedReport = String.format(Locale.getDefault(),
                        "📊 【%s মার্জিন রিপোর্ট】\n" +
                        "──────────────────────\n" +
                        "• পণ্য: %s (পরিমাণ: %d)\n" +
                        "• সর্বমোট খরচ: %s %.2f\n" +
                        "• সর্বমোট বিক্রয়: %s %.2f\n" +
                        "• নিট লোকসান: -%s %.2f\n" +
                        "• ক্ষতির হার: %.2f%%\n" +
                        "• স্ট্যাটাস: লোকসান ⚠️\n" +
                        "──────────────────────\n" +
                        "তারিখ: %s",
                        shopName, title, qty, currentCurrency, totalCost, currentCurrency, grossRevenue, currentCurrency, Math.abs(netProfit), marginPercent, dateStr);

            } else {
                int neutralColor = ContextCompat.getColor(this, R.color.text_headline);
                textdisplay.setTextColor(neutralColor);
                tvSummaryMargin.setTextColor(neutralColor);
                tvStatusBadge.setText("⚖️ ব্রেক-ইভেন");
                tvStatusBadge.setTextColor(neutralColor);

                textdisplay.setText("⚖️ সমান সমান (Break-even)\nকোনো লাভ বা ক্ষতি হয়নি (০.০০%)");
                tvInsight.setText("⚖️ ব্রেক-ইভেন পয়েন্টে আছেন। অপারেটিং খরচ যোগ করলে এটি লোকসানে রূপ নিতে পারে।");

                lastGeneratedReport = String.format(Locale.getDefault(),
                        "📊 【%s মার্জিন রিপোর্ট】\n" +
                        "──────────────────────\n" +
                        "• পণ্য: %s\n" +
                        "• খরচ ও বিক্রয়: %s %.2f\n" +
                        "• স্ট্যাটাস: ব্রেক-ইভেন (০ লাভ / ০ ক্ষতি)\n" +
                        "──────────────────────",
                        shopName, title, currentCurrency, totalCost);
            }

        } catch (NumberFormatException e) {
            Toast.makeText(this, "ইনপুট বক্সে সঠিক সংখ্যা লিখুন", Toast.LENGTH_SHORT).show();
        }
    }

    private void resetCalculator() {
        etProductTitle.setText("");
        edbuy.setText("");
        edsell.setText("");
        etQuantity.setText("1");
        etExtraCost.setText("0");
        etVatPercent.setText("0");
        etDiscountPercent.setText("0");

        edbuy.setError(null);
        edsell.setError(null);
        edbuy.requestFocus();

        layoutMetricGrid.setVisibility(View.GONE);
        layoutInsight.setVisibility(View.GONE);
        layoutActions.setVisibility(View.GONE);

        tvStatusBadge.setText("অপেক্ষমান");
        tvStatusBadge.setTextColor(ContextCompat.getColor(this, R.color.text_muted));

        textdisplay.setTextColor(ContextCompat.getColor(this, R.color.text_muted));
        textdisplay.setText("ফলাফল দেখতে মান প্রদান করে 'হিসাব করুন' বাটনে চাপুন");

        currentCalculatedItem = null;
        lastGeneratedReport = "";
        Toast.makeText(this, "সব তথ্য রিসেট করা হয়েছে", Toast.LENGTH_SHORT).show();
    }

    // --- TAB 2: HISTORY SUITE ---

    private void setupHistory() {
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        transactionAdapter = new TransactionAdapter(this, historyList, currentCurrency, new TransactionAdapter.OnItemActionListener() {
            @Override
            public void onPdfClick(TransactionItem item) {
                File pdf = PdfInvoiceGenerator.generateInvoicePdf(MainActivity.this, item, shopName, currentCurrency);
                if (pdf != null) {
                    PdfInvoiceGenerator.sharePdf(MainActivity.this, pdf, "ইনভয়েস - " + item.getTitle());
                }
            }

            @Override
            public void onDeleteClick(TransactionItem item) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("হিসাব ডিলিট")
                        .setMessage("আপনি কি এই রেকর্ডটি স্থায়ীভাবে ডিলিট করতে চান?")
                        .setPositiveButton("হ্যাঁ, ডিলিট", (dialog, which) -> {
                            dbHelper.deleteTransaction(item.getId());
                            loadHistoryData(etSearchHistory.getText().toString());
                            Toast.makeText(MainActivity.this, "রেকর্ড ডিলিট হয়েছে", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("না", null)
                        .show();
            }
        });
        rvHistory.setAdapter(transactionAdapter);

        etSearchHistory.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loadHistoryData(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadHistoryData(String keyword) {
        historyList.clear();
        historyList.addAll(dbHelper.getAllTransactions(keyword));
        if (transactionAdapter != null) {
            transactionAdapter.setCurrencySymbol(currentCurrency);
        }

        float totalProfit = dbHelper.getTotalNetProfit();
        tvTotalHistoryProfit.setText(String.format(Locale.getDefault(), "%s %.2f", currentCurrency, totalProfit));
        tvTotalHistoryCount.setText(historyList.size() + " টি রেকর্ড");

        tvEmptyHistory.setVisibility(historyList.isEmpty() ? View.VISIBLE : View.GONE);
    }

    // --- TAB 3: KHATA (CUSTOMER DUE) SUITE ---

    private void setupKhata() {
        rvKhata.setLayoutManager(new LinearLayoutManager(this));
        dueAdapter = new DueAdapter(this, dueList, currentCurrency, new DueAdapter.OnDueActionListener() {
            @Override
            public void onTogglePaid(DueItem item) {
                String newStatus = "PAID".equalsIgnoreCase(item.getStatus()) ? "DUE" : "PAID";
                dbHelper.updateDueStatus(item.getId(), newStatus);
                loadKhataData(etSearchKhata.getText().toString());
                Toast.makeText(MainActivity.this, "স্ট্যাটাস পরিবর্তন হয়েছে", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDelete(DueItem item) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("বাকি রেকর্ড ডিলিট")
                        .setMessage("আপনি কি এই কাস্টমারের হিসাব মুছে ফেলতে চান?")
                        .setPositiveButton("হ্যাঁ, ডিলিট", (dialog, which) -> {
                            dbHelper.deleteDue(item.getId());
                            loadKhataData(etSearchKhata.getText().toString());
                            Toast.makeText(MainActivity.this, "হিসাব ডিলিট হয়েছে", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("না", null)
                        .show();
            }
        });
        rvKhata.setAdapter(dueAdapter);

        btnAddDue.setOnClickListener(v -> showAddDueDialog());

        etSearchKhata.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loadKhataData(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadKhataData(String keyword) {
        dueList.clear();
        dueList.addAll(dbHelper.getAllDues(keyword));
        if (dueAdapter != null) {
            dueAdapter.setCurrencySymbol(currentCurrency);
        }

        float totalDue = dbHelper.getTotalDueAmount();
        tvTotalDueAmount.setText(String.format(Locale.getDefault(), "%s %.2f", currentCurrency, totalDue));
        tvEmptyKhata.setVisibility(dueList.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void showAddDueDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_due, null);
        TextInputEditText etCustomerName = dialogView.findViewById(R.id.etDialogCustomerName);
        TextInputEditText etCustomerPhone = dialogView.findViewById(R.id.etDialogCustomerPhone);
        TextInputEditText etDueAmount = dialogView.findViewById(R.id.etDialogDueAmount);
        TextInputEditText etDueNotes = dialogView.findViewById(R.id.etDialogDueNotes);

        new AlertDialog.Builder(this)
                .setView(dialogView)
                .setPositiveButton("সংরক্ষণ", (dialog, which) -> {
                    String name = etCustomerName.getText().toString().trim();
                    String phone = etCustomerPhone.getText().toString().trim();
                    String sAmount = etDueAmount.getText().toString().trim();
                    String notes = etDueNotes.getText().toString().trim();

                    if (name.isEmpty() || sAmount.isEmpty()) {
                        Toast.makeText(MainActivity.this, "নাম ও টাকার পরিমাণ বাধ্যতামূলক", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        float amount = Float.parseFloat(sAmount);
                        String dateStr = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date());
                        DueItem due = new DueItem(0, name, phone, amount, notes, dateStr, "DUE");
                        dbHelper.insertDue(due);
                        loadKhataData("");
                        Toast.makeText(MainActivity.this, "✅ নতুন বাকি যুক্ত হয়েছে!", Toast.LENGTH_SHORT).show();
                    } catch (NumberFormatException e) {
                        Toast.makeText(MainActivity.this, "সঠিক সংখ্যা দিন", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("বাতিল", null)
                .show();
    }

    // --- TAB 4: SETTINGS & PREFERENCES SUITE ---

    private void setupSettings() {
        btnCurrencyBdt.setOnClickListener(v -> setCurrency("৳", "BDT"));
        btnCurrencyUsd.setOnClickListener(v -> setCurrency("$", "USD"));
        btnCurrencyInr.setOnClickListener(v -> setCurrency("₹", "INR"));
        btnCurrencySar.setOnClickListener(v -> setCurrency("﷼", "SAR"));

        btnSaveSettings.setOnClickListener(v -> {
            String name = etShopName.getText().toString().trim();
            String contact = etShopContact.getText().toString().trim();

            if (!name.isEmpty()) shopName = name;

            prefs.edit()
                    .putString(KEY_SHOP_NAME, shopName)
                    .putString(KEY_SHOP_CONTACT, contact)
                    .apply();

            Toast.makeText(this, "⚙️ সেটিংস সফলভাবে সংরক্ষিত হয়েছে!", Toast.LENGTH_SHORT).show();
        });
    }

    private void setCurrency(String symbol, String code) {
        currentCurrency = symbol;
        prefs.edit().putString(KEY_CURRENCY, symbol).apply();
        updateCurrencyUi();
        Toast.makeText(this, "কারেন্সি " + symbol + " (" + code + ") নির্ধারিত হয়েছে", Toast.LENGTH_SHORT).show();
    }

    private void updateCurrencyUi() {
        tvActiveCurrencyBadge.setText(currentCurrency + " " + getCurrencyCode(currentCurrency));
    }

    private String getCurrencyCode(String symbol) {
        if ("$".equals(symbol)) return "USD";
        if ("₹".equals(symbol)) return "INR";
        if ("﷼".equals(symbol)) return "SAR";
        return "BDT";
    }

    private void loadSettingsValues() {
        etShopName.setText(prefs.getString(KEY_SHOP_NAME, shopName));
        etShopContact.setText(prefs.getString(KEY_SHOP_CONTACT, ""));
    }
}