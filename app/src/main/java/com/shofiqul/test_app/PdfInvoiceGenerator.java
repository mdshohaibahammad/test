package com.shofiqul.test_app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PdfInvoiceGenerator {

    public static File generateInvoicePdf(Context context, TransactionItem item, String shopName, String currencySymbol) {
        PdfDocument document = new PdfDocument();

        // Standard receipt page dimensions: width 595 (A4 width in points), height 842
        int pageWidth = 595;
        int pageHeight = 842;
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        Paint bgPaint = new Paint();

        // Background
        bgPaint.setColor(Color.WHITE);
        canvas.drawRect(0, 0, pageWidth, pageHeight, bgPaint);

        // Header Background Banner
        bgPaint.setColor(Color.parseColor("#0F172A"));
        canvas.drawRect(0, 0, pageWidth, 120, bgPaint);

        // Header Title
        paint.setColor(Color.WHITE);
        paint.setTextSize(22);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText(shopName != null && !shopName.isEmpty() ? shopName : "FINEXPERT BUSINESS INVOICE", 40, 55, paint);

        paint.setTextSize(12);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        paint.setColor(Color.parseColor("#94A3B8"));
        canvas.drawText("Commercial Margin & Financial Summary", 40, 78, paint);

        String dateStr = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(new Date());
        canvas.drawText("Generated: " + dateStr, 40, 98, paint);

        // Invoice Meta Box
        int y = 160;
        paint.setColor(Color.parseColor("#1E293B"));
        paint.setTextSize(15);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("ITEM DETAILS", 40, y, paint);

        // Divider
        paint.setColor(Color.parseColor("#E2E8F0"));
        paint.setStrokeWidth(2);
        canvas.drawLine(40, y + 10, pageWidth - 40, y + 10, paint);

        y += 40;
        paint.setColor(Color.parseColor("#334155"));
        paint.setTextSize(13);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));

        drawTableRow(canvas, paint, "Product / Title:", item.getTitle(), 40, y, pageWidth);
        y += 28;
        drawTableRow(canvas, paint, "Quantity (Units):", String.valueOf(item.getQuantity()), 40, y, pageWidth);
        y += 28;
        drawTableRow(canvas, paint, "Unit Buying Cost:", currencySymbol + " " + String.format(Locale.US, "%.2f", item.getBuyPrice()), 40, y, pageWidth);
        y += 28;
        drawTableRow(canvas, paint, "Unit Selling Price:", currencySymbol + " " + String.format(Locale.US, "%.2f", item.getSellPrice()), 40, y, pageWidth);
        y += 28;
        drawTableRow(canvas, paint, "Extra Expenses (Delivery/Pack):", currencySymbol + " " + String.format(Locale.US, "%.2f", item.getExtraCost()), 40, y, pageWidth);
        y += 28;
        drawTableRow(canvas, paint, "Discount Rate:", String.format(Locale.US, "%.2f%%", item.getDiscountPercent()), 40, y, pageWidth);
        y += 28;
        drawTableRow(canvas, paint, "VAT / Tax Rate:", String.format(Locale.US, "%.2f%%", item.getVatPercent()), 40, y, pageWidth);

        // Executive Financial Summary Table
        y += 45;
        paint.setColor(Color.parseColor("#1E293B"));
        paint.setTextSize(15);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("EXECUTIVE FINANCIAL SUMMARY", 40, y, paint);

        paint.setColor(Color.parseColor("#E2E8F0"));
        paint.setStrokeWidth(2);
        canvas.drawLine(40, y + 10, pageWidth - 40, y + 10, paint);

        y += 40;
        paint.setTextSize(13);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        drawTableRow(canvas, paint, "Total Cost (COGS + Extra):", currencySymbol + " " + String.format(Locale.US, "%.2f", item.getTotalCost()), 40, y, pageWidth);
        y += 28;
        drawTableRow(canvas, paint, "Gross Revenue (After Tax/Disc):", currencySymbol + " " + String.format(Locale.US, "%.2f", item.getTotalRevenue()), 40, y, pageWidth);
        y += 28;
        drawTableRow(canvas, paint, "Profit Margin %:", String.format(Locale.US, "%+.2f%%", item.getMarginPercent()), 40, y, pageWidth);

        // Highlight Box for Net Profit/Loss
        y += 35;
        bgPaint.setColor(item.getNetProfit() >= 0 ? Color.parseColor("#ECFDF5") : Color.parseColor("#FEF2F2"));
        canvas.drawRoundRect(40, y, pageWidth - 40, y + 60, 12, 12, bgPaint);

        paint.setColor(item.getNetProfit() >= 0 ? Color.parseColor("#047857") : Color.parseColor("#B91C1C"));
        paint.setTextSize(16);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        String resultLabel = item.getNetProfit() >= 0 ? "NET PROFIT (নিট লাভ):" : "NET LOSS (লোকসান):";
        canvas.drawText(resultLabel, 55, y + 36, paint);

        String profitAmount = currencySymbol + " " + String.format(Locale.US, "%.2f", Math.abs(item.getNetProfit()));
        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(profitAmount, pageWidth - 55, y + 36, paint);
        paint.setTextAlign(Paint.Align.LEFT);

        // Footer Note
        paint.setColor(Color.parseColor("#94A3B8"));
        paint.setTextSize(10);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC));
        canvas.drawText("This document is generated digitally by FinExpert Pro. No signature required.", 40, pageHeight - 40, paint);

        document.finishPage(page);

        File pdfDir = new File(context.getCacheDir(), "invoices");
        if (!pdfDir.exists()) {
            pdfDir.mkdirs();
        }

        File pdfFile = new File(pdfDir, "invoice_" + System.currentTimeMillis() + ".pdf");
        try {
            FileOutputStream fos = new FileOutputStream(pdfFile);
            document.writeTo(fos);
            fos.close();
            document.close();
            return pdfFile;
        } catch (IOException e) {
            e.printStackTrace();
            document.close();
            return null;
        }
    }

    private static void drawTableRow(Canvas canvas, Paint paint, String label, String value, int left, int y, int pageWidth) {
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(label, left, y, paint);

        paint.setTextAlign(Paint.Align.RIGHT);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText(value, pageWidth - left, y, paint);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        paint.setTextAlign(Paint.Align.LEFT);
    }

    public static void sharePdf(Context context, File pdfFile, String subject) {
        if (pdfFile == null || !pdfFile.exists()) {
            Toast.makeText(context, "পিডিএফ ফাইল তৈরি ব্যর্থ হয়েছে", Toast.LENGTH_SHORT).show();
            return;
        }

        Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", pdfFile);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(Intent.createChooser(intent, "পিডিএফ ইনভয়েস শেয়ার করুন"));
    }
}
