package pro.network.jvradmin;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.view.View;
import android.widget.LinearLayout;

import pro.network.jvradmin.ad.MainActivityAd;
import pro.network.jvradmin.banner.MainActivityBanner;
import pro.network.jvradmin.feedback.MainActivityFeedback;
import pro.network.jvradmin.order.MainActivityOrder;
import pro.network.jvradmin.product.MainActivityProduct;

public class NaviActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navi);
        LinearLayout ad = (LinearLayout) findViewById(R.id.ad);
        ad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent io = new Intent(NaviActivity.this, MainActivityAd.class);
                startActivity(io);

            }
        });
        CardView stock = (CardView) findViewById(R.id.stock);
        stock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent io = new Intent(NaviActivity.this, MainActivityProduct.class);
                startActivity(io);

            }
        });
        CardView banner = (CardView) findViewById(R.id.banner);
        banner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent io = new Intent(NaviActivity.this, MainActivityBanner.class);
                startActivity(io);

            }
        });

        CardView order = (CardView) findViewById(R.id.orders);
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent io = new Intent(NaviActivity.this, MainActivityOrder.class);
                startActivity(io);

            }
        });

        CardView feedback = (CardView) findViewById(R.id.feedback);
        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent io = new Intent(NaviActivity.this, MainActivityFeedback.class);
                startActivity(io);

            }
        });

    }
}
