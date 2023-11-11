package com.soket.srsmobile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class AdapterOpnameReport extends RecyclerView.Adapter<AdapterOpnameReport.OpnameReportViewHolder>
{
    private final ArrayList<clsOpnameReportDetail> dataList;

    public AdapterOpnameReport(ArrayList<clsOpnameReportDetail> dataList) {
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public OpnameReportViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate( R.layout.row_opname_report_detail, parent, false);
        return new OpnameReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OpnameReportViewHolder holder, int position) {
        holder.txtNamaProduk.setText(dataList.get(position).GetNamaBarang());
        holder.txtKodeProduk.setText(dataList.get(position).GetKodeProduk());
        holder.txtOpnameToko.setText(dataList.get(position).GetOpnameToko());
        holder.txtOpnameGudang.setText(dataList.get(position).GetOpnameGudang());
    }

    @Override
    public int getItemCount() {
        return (dataList != null) ? dataList.size() : 0;
    }

    public static class OpnameReportViewHolder extends RecyclerView.ViewHolder{
        private final TextView txtNamaProduk;
        private final TextView txtKodeProduk;
        private final TextView txtOpnameToko;
        private final TextView txtOpnameGudang;

        public OpnameReportViewHolder(View itemView) {
            super(itemView);
            txtNamaProduk =  itemView.findViewById(R.id.textViewNamaProduk_OpnameReport);
            txtKodeProduk =  itemView.findViewById(R.id.textViewKodeProduk_OpnameReport);
            txtOpnameToko =  itemView.findViewById(R.id.textViewOpnameToko_OpnameReport);
            txtOpnameGudang =  itemView.findViewById(R.id.textViewOpnameGudang_OpnameReport);
        }
    }
}
