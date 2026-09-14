package com.shofiqul.test_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TxViewHolder> {

    public interface OnItemActionListener {
        void onPdfClick(TransactionItem item);
        void onDeleteClick(TransactionItem item);
    }

    private final Context context;
    private final List<TransactionItem> list;
    private final String currencySymbol;
    private final OnItemActionListener listener;

    public TransactionAdapter(Context context, List<TransactionItem> list, String currencySymbol, OnItemActionListener listener) {
        this.context = context;
        this.list = list;
        this.currencySymbol = currencySymbol;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TxViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_transaction, parent, false);
        return new TxViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TxViewHolder holder, int position) {
        TransactionItem item = list.get(position);

        holder.tvTxTitle.setText(item.getTitle());
        holder.tvTxDate.setText(item.getDate());

        String details = String.format(Locale.getDefault(),
                "পরিমাণ: %d | ক্রয়: %s%.2f | বিক্রয়: %s%.2f\nখরচ: %s%.2f | ভ্যাট: %.1f%% | ছাড়: %.1f%%",
                item.getQuantity(), currencySymbol, item.getBuyPrice(),
                currencySymbol, item.getSellPrice(),
                currencySymbol, item.getExtraCost(),
                item.getVatPercent(), item.getDiscountPercent());
        holder.tvTxDetails.setText(details);

        if (item.getNetProfit() > 0) {
            holder.tvTxProfitBadge.setText(String.format(Locale.getDefault(), "+ %s%.2f (%+.1f%%)", currencySymbol, item.getNetProfit(), item.getMarginPercent()));
            holder.tvTxProfitBadge.setTextColor(ContextCompat.getColor(context, R.color.status_profit));
        } else if (item.getNetProfit() < 0) {
            holder.tvTxProfitBadge.setText(String.format(Locale.getDefault(), "- %s%.2f (%.1f%%)", currencySymbol, Math.abs(item.getNetProfit()), item.getMarginPercent()));
            holder.tvTxProfitBadge.setTextColor(ContextCompat.getColor(context, R.color.status_loss));
        } else {
            holder.tvTxProfitBadge.setText(String.format(Locale.getDefault(), "%s0.00 (0.0%%)", currencySymbol));
            holder.tvTxProfitBadge.setTextColor(ContextCompat.getColor(context, R.color.text_headline));
        }

        holder.btnItemPdf.setOnClickListener(v -> {
            if (listener != null) listener.onPdfClick(item);
        });

        holder.btnItemDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class TxViewHolder extends RecyclerView.ViewHolder {
        TextView tvTxTitle, tvTxDate, tvTxProfitBadge, tvTxDetails;
        ImageView btnItemPdf, btnItemDelete;

        public TxViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTxTitle = itemView.findViewById(R.id.tvTxTitle);
            tvTxDate = itemView.findViewById(R.id.tvTxDate);
            tvTxProfitBadge = itemView.findViewById(R.id.tvTxProfitBadge);
            tvTxDetails = itemView.findViewById(R.id.tvTxDetails);
            btnItemPdf = itemView.findViewById(R.id.btnItemPdf);
            btnItemDelete = itemView.findViewById(R.id.btnItemDelete);
        }
    }
}
