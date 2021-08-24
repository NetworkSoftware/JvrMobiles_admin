package pro.network.jvradmin.ad;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import pro.network.jvradmin.R;
import pro.network.jvradmin.app.Appconfig;
import pro.network.jvradmin.app.GlideApp;


public class AdimgAdapter extends RecyclerView.Adapter<AdimgAdapter.MyViewHolder> {

    private List<String> moviesList;
    private Context context;
    private AdClick adClick;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView3, close;

        public MyViewHolder(View view) {
            super(view);
            imageView3 = (ImageView) view.findViewById(R.id.imageView3);
            close = (ImageView) view.findViewById(R.id.close);


        }
    }


    public AdimgAdapter(List<String> moviesList, Context context, AdClick adClick) {
        this.moviesList = moviesList;
        this.context = context;
        this.adClick = adClick;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ad_img, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        String shop = moviesList.get(position);
        String file = Appconfig.ip + "/feed/" + shop;
        GlideApp.with(context)
                .load(file)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(false)
                .placeholder(R.drawable.mobile_phone)
                .into(holder.imageView3);


        holder.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adClick.onDeleteClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }

    public void notifyData(List<String> moviesList) {
        this.moviesList = moviesList;
        notifyDataSetChanged();
    }


}
