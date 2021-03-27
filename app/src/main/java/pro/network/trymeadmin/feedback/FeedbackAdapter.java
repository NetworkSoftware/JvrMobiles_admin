package pro.network.trymeadmin.feedback;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import pro.network.trymeadmin.R;
import pro.network.trymeadmin.app.Appconfig;
import pro.network.trymeadmin.order.Order;
import pro.network.trymeadmin.order.OrderListSubAdapter;
import pro.network.trymeadmin.order.StatusListener;

/**
 * Created by ravi on 16/11/17.
 */

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.MyViewHolder> {
    private Context context;
    private List<Feedback> feedbacks;

    public FeedbackAdapter(Context context, List<Feedback> feedbacks) {
        this.context = context;
        this.feedbacks = feedbacks;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.feedback_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Feedback feedback = feedbacks.get(position);
        holder.user_name.setText(feedback.getUser_name());
        holder.feedback.setText(feedback.getFeedback());



    }

    @Override
    public int getItemCount() {
        return feedbacks.size();

    }


    public void notifyData(List<Feedback> feedbacks) {
        this.feedbacks=feedbacks;
        notifyDataSetChanged();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView user_name, feedback;


        public MyViewHolder(View view) {
            super(view);
            user_name = (TextView) view.findViewById(R.id.user_name);
            feedback = (TextView) view.findViewById(R.id.feedback);



        }
    }
}
