package pro.network.jvradmin.order;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import pro.network.jvradmin.R;
import pro.network.jvradmin.app.Appconfig;
import pro.network.jvradmin.app.GlideApp;
import pro.network.jvradmin.product.Product;
import pro.network.jvradmin.product.ProductUpdate;


public class OrderListSubAdapter extends RecyclerView.Adapter<OrderListSubAdapter.MyViewHolder> {

    private Context mainActivityUser;
    private ArrayList<Product> myorderBeans;
    SharedPreferences preferences;
    int selectedPosition = 0;


    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView product_image;
        private TextView qty;
        LinearLayout items;


        public MyViewHolder(View view) {
            super((view));
            product_image = (ImageView) view.findViewById(R.id.product_image);
            qty = (TextView) view.findViewById(R.id.qty);
            items = view.findViewById(R.id.items);

        }
    }

    public OrderListSubAdapter(Context mainActivityUser, ArrayList<Product> myorderBeans) {
        this.mainActivityUser = mainActivityUser;
        this.myorderBeans = myorderBeans;
    }

    public void notifyData(ArrayList<Product> myorderBeans) {
        this.myorderBeans = myorderBeans;
        notifyDataSetChanged();
    }

    public void notifyData(int selectedPosition) {
        this.selectedPosition = selectedPosition;
        notifyDataSetChanged();
    }

    public OrderListSubAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.myorders_list_sub, parent, false);

        return new OrderListSubAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Product myorderBean = myorderBeans.get(position);
        holder.qty.setText(myorderBean.getQty() +" "+ myorderBean.getModel());
//        ArrayList<String> images = new Gson().fromJson(myorderBean.getImage(), (Type) List.class);

        GlideApp.with(mainActivityUser)
                .load(Appconfig.getResizedImage(myorderBean.getImage(), true))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(false)
                .placeholder(R.drawable.vivo)
                .into(holder.product_image);

        holder.items.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(mainActivityUser, ProductUpdate.class);
                intent.putExtra("isServer",true);
                intent.putExtra("id",myorderBean.getId());
                mainActivityUser.startActivity(intent);
            }
        });

    }

    public int getItemCount() {
        return myorderBeans.size();
    }

}


