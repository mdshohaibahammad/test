package com.shofiqul.test_app;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText edbuy, edsell;
    private Button hisabbutton;
    private TextView textdisplay;

    // Enterprise UI Components
    private ImageView btnResetHeader;
    private View btnReset;
    private TextView tvStatusBadge;
    private LinearLayout layoutMetricGrid, layoutInsight, layoutActions;
    private TextView tvSummaryBuy, tvSummarySell, tvSummaryMargin, tvSummaryMarkup;
    private TextView tvInsight;
    private ImageView ivInsight;
    private View btnCopy, btnShare;

    // Preset Chips
    private TextView chip10, chip15, chip20, chip25, chip30, chip50;

    private String lastGeneratedReport = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupPresetChips();
        setupClickListeners();
    }

    private void initViews() {
        edbuy = findViewById(R.id.edbuy);
        edsell = findViewById(R.id.edsell);
        hisabbutton = findViewById(R.id.hisabbutton);
        textdisplay = findViewById(R.id.textdisplay);

        btnResetHeader = findViewById(R.id.btnResetHeader);
        btnReset = findViewById(R.id.btnReset);
        tvStatusBadge = findViewById(R.id.tvStatusBadge);
        layoutMetricGrid = findViewById(R.id.layoutMetricGrid);
        layoutInsight = findViewById(R.id.layoutInsight);
        layoutActions = findViewById(R.id.layoutActions);

        tvSummaryBuy = findViewById(R.id.tvSummaryBuy);
        tvSummarySell = findViewById(R.id.tvSummarySell);
        tvSummaryMargin = findViewById(R.id.tvSummaryMargin);
        tvSummaryMarkup = findViewById(R.id.tvSummaryMarkup);
        tvInsight = findViewById(R.id.tvInsight);
        ivInsight = findViewById(R.id.ivInsight);

        btnCopy = findViewById(R.id.btnCopy);
        btnShare = findViewById(R.id.btnShare);

        chip10 = findViewById(R.id.chip10);
        chip15 = findViewById(R.id.chip15);
        chip20 = findViewById(R.id.chip20);
        chip25 = findViewById(R.id.chip25);
        chip30 = findViewById(R.id.chip30);
        chip50 = findViewById(R.id.chip50);
    }

    private void setupPresetChips() {
        applyChipPreset(chip10, 10f);
        applyChipPreset(chip15, 15f);
        applyChipPreset(chip20, 20f);
        applyChipPreset(chip25, 25f);
        applyChipPreset(chip30, 30f);
        applyChipPreset(chip50, 50f);
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
                performEnterpriseCalculation();
            } catch (NumberFormatException e) {
                edbuy.setError("সঠিক সংখ্যা প্রদান করুন");
            }
        });
    }

    private void setupClickListeners() {
        hisabbutton.setOnClickListener(v -> performEnterpriseCalculation());

        View.OnClickListener resetAction = v -> resetAllFields();
        if (btnReset != null) btnReset.setOnClickListener(resetAction);
        if (btnResetHeader != null) btnResetHeader.setOnClickListener(resetAction);

        if (btnCopy != null) {
            btnCopy.setOnClickListener(v -> {
                if (lastGeneratedReport.isEmpty()) {
                    Toast.makeText(MainActivity.this, "কপি করার মতো কোনো ডাটা নেই", Toast.LENGTH_SHORT).show();
                    return;
                }
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Hisab Report", lastGeneratedReport);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(MainActivity.this, "📋 রিপোর্ট ক্লিপবোর্ডে কপি হয়েছে!", Toast.LENGTH_SHORT).show();
            });
        }

        if (btnShare != null) {
            btnShare.setOnClickListener(v -> {
                if (lastGeneratedReport.isEmpty()) {
                    Toast.makeText(MainActivity.this, "শেয়ার করার মতো কোনো ডাটা নেই", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "ফাইন্যান্সিয়াল মার্জিন রিপোর্ট");
                shareIntent.putExtra(Intent.EXTRA_TEXT, lastGeneratedReport);
                startActivity(Intent.createChooser(shareIntent, "রিপোর্ট শেয়ার করুন"));
            });
        }
    }

    private void performEnterpriseCalculation() {
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
            float buy = Float.parseFloat(sBuy);
            float sell = Float.parseFloat(sSell);

            if (buy <= 0) {
                edbuy.setError("ক্রয়মূল্য ০ এর চেয়ে বেশি হতে হবে");
                edbuy.requestFocus();
                return;
            }

            float diff = sell - buy;
            float markupPercent = (diff / buy) * 100f; // Markup based on cost
            float marginPercent = sell > 0 ? (diff / sell) * 100f : 0f; // Profit margin based on revenue

            // Populate 4-Grid Dashboard
            layoutMetricGrid.setVisibility(View.VISIBLE);
            layoutInsight.setVisibility(View.VISIBLE);
            layoutActions.setVisibility(View.VISIBLE);

            tvSummaryBuy.setText(String.format(Locale.getDefault(), "৳ %.2f", buy));
            tvSummarySell.setText(String.format(Locale.getDefault(), "৳ %.2f", sell));
            tvSummaryMarkup.setText(String.format(Locale.getDefault(), "%+.2f%%", markupPercent));
            tvSummaryMargin.setText(String.format(Locale.getDefault(), "%+.2f%%", marginPercent));

            if (diff > 0) {
                // Profit
                int profitColor = ContextCompat.getColor(this, R.color.status_profit);
                textdisplay.setTextColor(profitColor);
                tvSummaryMargin.setTextColor(profitColor);
                tvStatusBadge.setText("✅ লাভজনক");
                tvStatusBadge.setTextColor(profitColor);
                if (ivInsight != null) ivInsight.setColorFilter(profitColor);

                String heroText = String.format(Locale.getDefault(),
                        "🎉 মোট নিট লাভ: ৳ %.2f\nমার্জিন: +%.2f%%  |  মার্কআপ: +%.2f%%",
                        diff, marginPercent, markupPercent);
                textdisplay.setText(heroText);

                if (marginPercent >= 30f) {
                    tvInsight.setText("🔥 চমৎকার মার্জিন! আপনার ব্যবসা অত্যন্ত শক্তিশালী এবং সম্প্রসারণের অনুকূল।");
                } else if (marginPercent >= 15f) {
                    tvInsight.setText("✅ স্বাস্থ্যকর মার্জিন। নিয়মিত ক্যাশফ্লো ও অপারেটিং ব্যয় সমন্বয়ের জন্য আদর্শ।");
                } else {
                    tvInsight.setText("⚠️ মার্জিন কিছুটা কম। অপারেটিং খরচ কমাতে বা বিক্রয়মূল্য সামান্য বাড়াতে বিবেচনা করুন।");
                }

                lastGeneratedReport = String.format(Locale.getDefault(),
                        "📊 【FINEXPERT মার্জিন রিপোর্ট】\n" +
                        "──────────────────────\n" +
                        "• ক্রয়মূল্য (Cost): ৳ %.2f\n" +
                        "• বিক্রয়মূল্য (Price): ৳ %.2f\n" +
                        "• নিট লাভ (Profit): ৳ %.2f\n" +
                        "• প্রফিট মার্জিন: +%.2f%%\n" +
                        "• মার্কআপ হার: +%.2f%%\n" +
                        "• স্ট্যাটাস: লাভজনক\n" +
                        "──────────────────────\n" +
                        "জেনারেটেড বাই FinExpert Pro",
                        buy, sell, diff, marginPercent, markupPercent);

            } else if (diff < 0) {
                // Loss
                int lossColor = ContextCompat.getColor(this, R.color.status_loss);
                textdisplay.setTextColor(lossColor);
                tvSummaryMargin.setTextColor(lossColor);
                tvStatusBadge.setText("⚠️ লোকসান");
                tvStatusBadge.setTextColor(lossColor);
                if (ivInsight != null) ivInsight.setColorFilter(lossColor);

                String heroText = String.format(Locale.getDefault(),
                        "🚨 লোকসান চিহ্নিত: ৳ %.2f\nক্ষতির হার: %.2f%%",
                        Math.abs(diff), marginPercent);
                textdisplay.setText(heroText);

                tvInsight.setText("🚨 লোকসান হচ্ছে! অবিলম্বে সরবরাহ খরচ কমান অথবা বিক্রয়মূল্য পুনঃনির্ধারণ করুন।");

                lastGeneratedReport = String.format(Locale.getDefault(),
                        "📊 【FINEXPERT মার্জিন রিপোর্ট】\n" +
                        "──────────────────────\n" +
                        "• ক্রয়মূল্য (Cost): ৳ %.2f\n" +
                        "• বিক্রয়মূল্য (Price): ৳ %.2f\n" +
                        "• লোকসান (Loss): -৳ %.2f\n" +
                        "• ক্ষতির হার: %.2f%%\n" +
                        "• স্ট্যাটাস: লোকসান\n" +
                        "──────────────────────\n" +
                        "জেনারেটেড বাই FinExpert Pro",
                        buy, sell, Math.abs(diff), marginPercent);

            } else {
                // Break even
                int neutralColor = ContextCompat.getColor(this, R.color.text_headline);
                textdisplay.setTextColor(neutralColor);
                tvSummaryMargin.setTextColor(neutralColor);
                tvStatusBadge.setText("⚖️ ব্রেক-ইভেন");
                tvStatusBadge.setTextColor(neutralColor);

                textdisplay.setText("⚖️ সমান সমান (Break-even)\nকোনো লাভ বা ক্ষতি হয়নি (০.০০%)");
                tvInsight.setText("⚖️ ব্রেক-ইভেন পয়েন্টে আছেন। অপারেটিং খরচ যোগ করলে এটি লোকসানে রূপ নিতে পারে।");

                lastGeneratedReport = String.format(Locale.getDefault(),
                        "📊 【FINEXPERT মার্জিন রিপোর্ট】\n" +
                        "──────────────────────\n" +
                        "• ক্রয়মূল্য (Cost): ৳ %.2f\n" +
                        "• বিক্রয়মূল্য (Price): ৳ %.2f\n" +
                        "• স্ট্যাটাস: ব্রেক-ইভেন (০ লাভ / ০ ক্ষতি)\n" +
                        "──────────────────────\n" +
                        "জেনারেটেড বাই FinExpert Pro",
                        buy, sell);
            }

        } catch (NumberFormatException e) {
            Toast.makeText(MainActivity.this, "সঠিক সংখ্যা ইনপুট দিন", Toast.LENGTH_SHORT).show();
        }
    }

    private void resetAllFields() {
        edbuy.setText("");
        edsell.setText("");
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
        lastGeneratedReport = "";

        Toast.makeText(this, "সব তথ্য রিসেট করা হয়েছে", Toast.LENGTH_SHORT).show();
    }
}