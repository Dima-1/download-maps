package com.example.downloadmaps;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
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
		ItemViewClickListener itemViewClickListener;

		ItemViewHolder(View itemView) {
			super(itemView);
			map = itemView.findViewById(R.id.ivMap);
			countryName = itemView.findViewById(R.id.tvCountryName);

			download = itemView.findViewById(R.id.ivDownload);
			downloadClickListener = new DownloadClickListener();
			download.setOnClickListener(downloadClickListener);

			ConstraintLayout clItemMain = itemView.findViewById(R.id.clItemMain);
			itemViewClickListener = new ItemViewClickListener();
			clItemMain.setOnClickListener(itemViewClickListener);

			progressBar = itemView.findViewById(R.id.progressBarMap);
			progressBar.getIndeterminateDrawable().setColorFilter(itemView.getResources().getColor(R.color.colorProgress), PorterDuff.Mode.SRC_IN);
			progressBar.getProgressDrawable().setColorFilter(itemView.getResources().getColor(R.color.colorProgress), PorterDuff.Mode.SRC_IN);
		}
	}

	class ItemViewClickListener implements View.OnClickListener {
		Entry entry;

		void setEntry(Entry entry) {
			this.entry = entry;
		}

		@Override
		public void onClick(View v) {
			view.subRegionClick(entry);
		}
	}

	class DownloadClickListener implements View.OnClickListener {
		Entry entry;
		boolean cancel;

		@Override
		public void onClick(View v) {
			if (cancel) {
				view.cancelDownloadMap(entry);
			} else {
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
		viewHolder.itemViewClickListener.setEntry(entry);
		viewHolder.downloadClickListener.setEntry(entry);
		viewHolder.countryName.setText(entry.getName());

		if (0 < entry.getDownloadProgress() && entry.getDownloadProgress() < 100) {
			if (viewHolder.progressBar.isIndeterminate())
				viewHolder.progressBar.setIndeterminate(false);
			entry.setLoadWaiting(false);
			viewHolder.progressBar.setProgress(entry.getDownloadProgress());
			setForCancel(viewHolder, false);
		} else {
			if (entry.isLoadWaiting()) {
				if (!viewHolder.progressBar.isIndeterminate())
					viewHolder.progressBar.setIndeterminate(true);
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
			if (normalDrawable != null) {
				Drawable wrapDrawable = DrawableCompat.wrap(normalDrawable);
				DrawableCompat.setTint(wrapDrawable,
						viewHolder.map.getResources().getColor(R.color.colorDownloadedMapIcon));
				viewHolder.map.setImageDrawable(wrapDrawable);
			}
			viewHolder.download.setImageDrawable(viewHolder.download.getResources()
					.getDrawable(R.drawable.ic_action_remove_dark));
		} else if (entry.getDownloadProgress() == 0 && !entry.isLoadWaiting()) {
			Drawable normalDrawable = ContextCompat.getDrawable(viewHolder.map.getContext(),
					R.drawable.ic_map);
			if (normalDrawable != null) {
				Drawable wrapDrawable = DrawableCompat.wrap(normalDrawable);
				DrawableCompat.setTint(wrapDrawable,
						viewHolder.map.getResources().getColor(R.color.colorIcon));
				viewHolder.map.setImageDrawable(wrapDrawable);
			}
			viewHolder.download.setImageDrawable(viewHolder.download.getResources()
					.getDrawable(R.drawable.ic_action_import));
		}
		if (entry.getFileName().isEmpty()) {
			viewHolder.download.setVisibility(View.INVISIBLE);
		} else {
			viewHolder.download.setVisibility(View.VISIBLE);
		}
	}

	private void setForCancel(@NonNull ItemViewHolder viewHolder, boolean indeterminate) {
		if (viewHolder.progressBar.getVisibility() != View.VISIBLE)
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

	ArrayList<Entry> getCountryList() {
		return countryList;
	}
}


