package com.example.downloadmaps;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
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

	class ItemViewHolder extends RecyclerView.ViewHolder {
		private ImageView map;
		private TextView countryName;
		private ProgressBar progressBar;
		private ImageView download;
		DownloadClickListener downloadClickListener;

		ItemViewHolder(View itemView) {
			super(itemView);
			map = itemView.findViewById(R.id.ivMap);
			countryName = itemView.findViewById(R.id.tvCountryName);
			download = itemView.findViewById(R.id.ivDownload);
			progressBar = itemView.findViewById(R.id.progressBarMap);
			progressBar.getIndeterminateDrawable().setColorFilter(itemView.getResources().getColor(R.color.colorProgress), PorterDuff.Mode.SRC_IN);
			progressBar.getProgressDrawable().setColorFilter(itemView.getResources().getColor(R.color.colorProgress), PorterDuff.Mode.SRC_IN);
			downloadClickListener = new DownloadClickListener();
			download.setOnClickListener(downloadClickListener);
		}
	}

	class DownloadClickListener implements View.OnClickListener {
		Entry entry;
		boolean cancel;

		@Override
		public void onClick(View v) {
			if (cancel) {
				entry.setLoadWait(false);
				view.cancelDownloadMap(entry);
			} else {
				entry.setLoadWait(true);
				view.downloadMap(entry);
			}
		}

		void setCancel(boolean cancel) {
			this.cancel = cancel;
		}

		void setEntry(Entry entry) {
			this.entry = entry;
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

		viewHolder.downloadClickListener.setEntry(entry);
		viewHolder.countryName.setText(entry.getName());

		if (0 < entry.getDownloadProgress() && entry.getDownloadProgress() < 100) {
			entry.setLoadWait(false);
			viewHolder.progressBar.setProgress(entry.getDownloadProgress());
			setForCancel(viewHolder, false);
		} else {
			if (entry.isLoadWait()) {
				setForCancel(viewHolder, true);
			} else {
				viewHolder.progressBar.setVisibility(View.GONE);
				viewHolder.downloadClickListener.setCancel(false);
				viewHolder.download.setImageDrawable(viewHolder.download.getResources()
						.getDrawable(R.drawable.ic_action_import));
			}
		}
		if (entry.getDownloadProgress() == 100) {
			Drawable normalDrawable = ContextCompat.getDrawable(viewHolder.map.getContext(),
					R.drawable.ic_map);
			Drawable wrapDrawable = DrawableCompat.wrap(normalDrawable);
			DrawableCompat.setTint(wrapDrawable,
					viewHolder.map.getResources().getColor(R.color.colorDownloadedMapIcon));
			viewHolder.map.setImageDrawable(wrapDrawable);
			viewHolder.download.setImageDrawable(viewHolder.download.getResources()
					.getDrawable(R.drawable.ic_action_remove_dark));
		} else {
			Drawable normalDrawable = viewHolder.map.getDrawable();
			Drawable wrapDrawable = DrawableCompat.wrap(normalDrawable);
			DrawableCompat.setTint(wrapDrawable,
					viewHolder.map.getResources().getColor(R.color.colorIcon));
			viewHolder.map.setImageDrawable(wrapDrawable);
		}
	}

	private void setForCancel(@NonNull ItemViewHolder viewHolder, boolean indeterminate) {
		viewHolder.progressBar.setIndeterminate(indeterminate);
		viewHolder.progressBar.setVisibility(View.VISIBLE);
		viewHolder.download.setImageDrawable(viewHolder.download.getResources()
				.getDrawable(R.drawable.ic_action_remove_dark));
		viewHolder.downloadClickListener.setCancel(true);
	}

	@Override
	public int getItemCount() {
		return countryList.size();
	}

	void setItems(ArrayList<Entry> countryList) {
		this.countryList = countryList;
	}
}


