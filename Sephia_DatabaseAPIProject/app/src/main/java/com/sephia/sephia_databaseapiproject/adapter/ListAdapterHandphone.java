package com.sephia.sephia_databaseapiproject.adapter;

import java.util.ArrayList;
import java.util.List;
import com.sephia.sephia_databaseapiproject.R;
import com.sephia.sephia_databaseapiproject.model.Handphone;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
public class ListAdapterHandphone extends BaseAdapter implements Filterable {
    private final Context context;
    private final List<Handphone> list;
    private List<Handphone> filtered;
    public ListAdapterHandphone(Context context, List<Handphone> list)
    {
        this.context = context;
        this.list = list;
        this.filtered = this.list;
    }
    @Override
    public int getCount() {
        return filtered.size();
    }
    @Override
    public Object getItem(int position) {
        return filtered.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup
            parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(this.context);
            convertView = inflater.inflate(R.layout.list_row, null);
        }
        Handphone hp = filtered.get(position);
        TextView textNama = convertView.findViewById(R.id.text_nama);
        textNama.setText(hp.getPhone_name());
        TextView textHarga =  convertView.findViewById(R.id.text_harga);
        textHarga.setText(hp.getPrice());
        return convertView;
    }
    @Override
    public Filter getFilter() {
        return new HandphoneFilter();
    }
    private class HandphoneFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence
                                                         constraint) {
            List<Handphone> filteredData = new ArrayList<Handphone>();
            FilterResults result = new FilterResults();
            String filterString = constraint.toString().toLowerCase();
            for (Handphone hp : list) {
                if (hp.getPhone_name().toLowerCase().contains(filterString)) {
                    filteredData.add(hp);
                }
            }
            result.count = filteredData.size();
            result.values = filteredData;
            return result;
        }
        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            filtered = (List<Handphone>) results.values;
            notifyDataSetChanged();
        }
    }
}