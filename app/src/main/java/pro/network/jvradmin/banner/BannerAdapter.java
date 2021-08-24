package pro.network.jvradmin.banner;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import pro.network.jvradmin.R;
import pro.network.jvradmin.app.Appconfig;
import pro.network.jvradmin.app.GlideApp;

/**
 * Created by ravi on 16/11/17.
 */

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.MyViewHolder> {
    private Context context;
    private List<Banner> bannerList;
    private BannerClick bannerClick;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumbnail, cancel;
        public TextView description;

        public MyViewHolder(View view) {
            super(view);


            thumbnail = view.findViewById(R.id.thumbnail);
            cancel = view.findViewById(R.id.cancel);
            description =(TextView) view.findViewById(R.id.description);

        }
    }


    public BannerAdapter(Context context, List<Banner> bannerList, BannerClick bannerClick) {
        this.context = context;
        this.bannerList = bannerList;
        this.bannerClick = bannerClick;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.banner_row_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Banner banner = bannerList.get(position);

        GlideApp.with(context)
                .load(Appconfig.getResizedImage(banner.getBanner(), true))
                .placeholder(R.drawable.vivo)
                .into(holder.thumbnail);
        holder.description.setText(banner.getDescription());
        holder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bannerClick.onDeleteClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bannerList.size();
    }


    public void notifyData(List<Banner> bannerList) {
        this.bannerList = bannerList;
        notifyDataSetChanged();
    }
}
