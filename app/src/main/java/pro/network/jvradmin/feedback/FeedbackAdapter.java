package pro.network.jvradmin.feedback;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pro.network.jvradmin.R;

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
        holder.user_phone.setText(feedback.getUser_phone());



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
        public TextView user_name, feedback,user_phone;


        public MyViewHolder(View view) {
            super(view);
            user_name = (TextView) view.findViewById(R.id.user_name);
            feedback = (TextView) view.findViewById(R.id.feedback);
            user_phone =view.findViewById(R.id.user_phone);



        }
    }
}
