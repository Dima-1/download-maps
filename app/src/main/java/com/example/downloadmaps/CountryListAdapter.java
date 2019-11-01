package com.example.downloadmaps;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by DR
 * on 31.10.2019.
 */
class CountryListAdapter extends RecyclerView.Adapter<CountryListAdapter.ItemViewHolder> {
	private IView view;
	private ArrayList<Entry> countryList;

	static class ItemViewHolder extends RecyclerView.ViewHolder {
		private ImageView map;
		private TextView countryName;
		private ProgressBar progressBar;
		private ImageView download;

		ItemViewHolder(View itemView) {
			super(itemView);
			map = itemView.findViewById(R.id.ivMap);
			countryName = itemView.findViewById(R.id.tvCountryName);
			download = itemView.findViewById(R.id.ivDownload);
			progressBar = itemView.findViewById(R.id.progressBarMap);
		}
	}

	CountryListAdapter(IView view, ArrayList<Entry> countryList) {

		this.countryList = countryList;
		this.view = view;
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
		final Entry entry = countryList.get(position);
		viewHolder.countryName.setText(entry.getName());
		Log.d("-------", "onBindViewHolder: ");
		if (0 < entry.getDownloadProgress() && entry.getDownloadProgress() < 100) {
			viewHolder.progressBar.setVisibility(View.VISIBLE);
			viewHolder.progressBar.setProgress(entry.getDownloadProgress());
		} else {
			viewHolder.progressBar.setVisibility(View.GONE);
		}
		if (entry.getDownloadProgress() == 100) {
			viewHolder.map.getContext();
			Drawable normalDrawable = viewHolder.map.getDrawable();
			Drawable wrapDrawable = DrawableCompat.wrap(normalDrawable);
			DrawableCompat.setTint(wrapDrawable,
					viewHolder.map.getResources().getColor(R.color.colorDownloadedMapIcon));
			viewHolder.map.setImageDrawable(wrapDrawable);
		} else {
			Drawable normalDrawable = viewHolder.map.getDrawable();
			Drawable wrapDrawable = DrawableCompat.wrap(normalDrawable);
			DrawableCompat.setTint(wrapDrawable,
					viewHolder.map.getResources().getColor(R.color.colorIcon));
			viewHolder.map.setImageDrawable(wrapDrawable);
		}

		viewHolder.download.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						view.downloadMap(entry);
					}
				}
		);
	}

	@Override
	public int getItemCount() {
		return countryList.size();
	}

	void setItems(ArrayList<Entry> countryList) {
		this.countryList = countryList;
	}
}


