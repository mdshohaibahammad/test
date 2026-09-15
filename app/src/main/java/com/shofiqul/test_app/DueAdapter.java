package com.shofiqul.test_app;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class DueAdapter extends RecyclerView.Adapter<DueAdapter.DueViewHolder> {

    public interface OnDueActionListener {
        void onTogglePaid(DueItem item);
        void onDelete(DueItem item);
    }

    private final Context context;
    private final List<DueItem> list;
    private String currencySymbol;
    private final OnDueActionListener listener;

    public DueAdapter(Context context, List<DueItem> list, String currencySymbol, OnDueActionListener listener) {
        this.context = context;
        this.list = list;
        this.currencySymbol = currencySymbol;
        this.listener = listener;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_due, parent, false);
        return new DueViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DueViewHolder holder, int position) {
        DueItem item = list.get(position);

        holder.tvCustomerName.setText(item.getCustomerName());
        holder.tvDueDate.setText("তারিখ: " + item.getDate());
        holder.tvDueAmount.setText(String.format(Locale.getDefault(), "%s %.2f", currencySymbol, item.getDueAmount()));

        String noteText = "";
        if (!item.getCustomerPhone().isEmpty()) {
            noteText += "📱 " + item.getCustomerPhone();
        }
        if (!item.getNotes().isEmpty()) {
            noteText += (noteText.isEmpty() ? "" : "  |  ") + "📝 " + item.getNotes();
        }
        holder.tvDueNotes.setText(noteText.isEmpty() ? "কোনো বিবরণ নেই" : noteText);

        boolean isPaid = "PAID".equalsIgnoreCase(item.getStatus());
        if (isPaid) {
            holder.tvDueStatusBadge.setText("পরিশোধিত ✅");
            holder.tvDueStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.status_profit));
            holder.tvDueAmount.setTextColor(ContextCompat.getColor(context, R.color.status_profit));
            holder.btnDueTogglePaid.setColorFilter(ContextCompat.getColor(context, R.color.text_muted));
        } else {
            holder.tvDueStatusBadge.setText("বাকি আছে ⚠️");
            holder.tvDueStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.status_loss));
            holder.tvDueAmount.setTextColor(ContextCompat.getColor(context, R.color.status_loss));
            holder.btnDueTogglePaid.setColorFilter(ContextCompat.getColor(context, R.color.status_profit));
        }

        // WhatsApp Reminder
        holder.btnDueWhatsApp.setOnClickListener(v -> {
            String phone = item.getCustomerPhone().trim().replaceAll("[^0-9+]", "");
            if (phone.isEmpty()) {
                Toast.makeText(context, "কাস্টমারের ফোন নম্বর নেই", Toast.LENGTH_SHORT).show();
                return;
            }
            if (phone.startsWith("01")) {
                phone = "+88" + phone;
            }

            String message = String.format(Locale.getDefault(),
                    "আসসালামু আলাইকুম %s,\nআপনার কাছে আমাদের দোকানের বাকি বাবদ %s %.2f টাকা পাওনা রয়েছে। অনুগ্রহ করে দ্রুত পরিশোধের অনুরোধ রইল। ধন্যবাদ।",
                    item.getCustomerName(), currencySymbol, item.getDueAmount());

            try {
                String url = "https://api.whatsapp.com/send?phone=" + phone + "&text=" + Uri.encode(message);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                context.startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(context, "WhatsApp ওপেন করা যায়নি", Toast.LENGTH_SHORT).show();
            }
        });

        // Direct Phone Call
        holder.btnDueCall.setOnClickListener(v -> {
            String phone = item.getCustomerPhone().trim();
            if (phone.isEmpty()) {
                Toast.makeText(context, "কাস্টমারের ফোন নম্বর নেই", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
            context.startActivity(intent);
        });

        // Toggle Paid
        holder.btnDueTogglePaid.setOnClickListener(v -> {
            if (listener != null) listener.onTogglePaid(item);
        });

        // Delete
        holder.btnDueDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDelete(item);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class DueViewHolder extends RecyclerView.ViewHolder {
        TextView tvCustomerName, tvDueDate, tvDueAmount, tvDueNotes, tvDueStatusBadge;
        ImageView btnDueWhatsApp, btnDueCall, btnDueTogglePaid, btnDueDelete;

        public DueViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvDueDate = itemView.findViewById(R.id.tvDueDate);
            tvDueAmount = itemView.findViewById(R.id.tvDueAmount);
            tvDueNotes = itemView.findViewById(R.id.tvDueNotes);
            tvDueStatusBadge = itemView.findViewById(R.id.tvDueStatusBadge);
            btnDueWhatsApp = itemView.findViewById(R.id.btnDueWhatsApp);
            btnDueCall = itemView.findViewById(R.id.btnDueCall);
            btnDueTogglePaid = itemView.findViewById(R.id.btnDueTogglePaid);
            btnDueDelete = itemView.findViewById(R.id.btnDueDelete);
        }
    }
}
