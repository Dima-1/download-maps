package com.example.downloadmaps;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by DR
 * on 31.10.2019.
 */
class CountryListAdapter extends RecyclerView.Adapter<CountryListAdapter.ItemViewHolder> {
    private Context context;
    private ArrayList<Entry> countryList;

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private ImageView map;
        private TextView countryName;
        private ImageView download;

        public ItemViewHolder(View itemView) {
            super(itemView);
            map = itemView.findViewById(R.id.ivMap);
            countryName = itemView.findViewById(R.id.tvCountryName);
            download = itemView.findViewById(R.id.ivDownload);
        }
    }

    public CountryListAdapter(Context context, ArrayList<Entry> myDataset) {

        countryList = myDataset;
        this.context = context;
    }

    @NonNull
    @Override
    public CountryListAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_main, parent, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final CountryListAdapter.ItemViewHolder viewHolder, int position) {
        Entry entry = countryList.get(position);
        viewHolder.countryName.setText(entry.getName());
        viewHolder.download.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, viewHolder.countryName.getText(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    @Override
    public int getItemCount() {
        return countryList.size();
    }
}
