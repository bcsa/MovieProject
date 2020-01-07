package com.bcsa.movieproject;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bcsa.movieproject.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdaptor extends RecyclerView.Adapter<RecyclerViewAdaptor.ViewHolder> implements Filterable {

    private static final String TAG ="RecyclerViewAdapter";

    List<Result> movieResults;
    List<Result> movieResultsFull;
    Context mContext;

    public RecyclerViewAdaptor(Context mContext, List<Result>movieresults)
    {
        this.movieResults = movieresults;
        this.movieResultsFull = new ArrayList<>(movieresults);
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_listmovies, parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        Glide.with(holder.image)
                .load(movieResults.get(position).getImage())
                .into(holder.image);
        holder.title.setText(movieResults.get(position).getTitle());
        holder.description.setText(movieResults.get(position).getOverview());
    }

    @Override
    public int getItemCount() {

        return movieResults.size();

    }

    @Override
    public Filter getFilter() {
        return movieFilter;
    }

    private Filter movieFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<Result> filteredList = new ArrayList<>();

            if (charSequence == null || charSequence.length() == 0) {
                filteredList.addAll(movieResultsFull);
            }
            else {
                String filterpattern = charSequence.toString().toLowerCase().trim();

                for (Result result : movieResultsFull) {
                    if (result.getTitle().toLowerCase().contains(filterpattern)) {
                        filteredList.add(result);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }


        @Override
        protected void publishResults (CharSequence charSequence, FilterResults results){
            movieResults.clear();
            movieResults.addAll((List)results.values);
            notifyDataSetChanged();
        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView image;
        private TextView title;
        private TextView description;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.movieimage_IV);
            title = itemView.findViewById(R.id.movietitle_TV);
            description = itemView.findViewById(R.id.moviedescription_TV);

        }


    }
}
