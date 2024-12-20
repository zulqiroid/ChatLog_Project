package com.example.chatlog_project.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.chatlog_project.Common;
import com.example.chatlog_project.R;
import com.jsibbold.zoomage.ZoomageView;

public class View_image extends AppCompatActivity {
    ImageView back_btn;
    ZoomageView myZoomageView;
    TextView user_name, time_txtview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_image);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String name=getIntent().getStringExtra("user name");
        String time=getIntent().getStringExtra("time");



        myZoomageView=findViewById(R.id.myZoomageView);
        user_name=findViewById(R.id.user_name);
        time_txtview=findViewById(R.id.time_txtview);

        myZoomageView.setImageBitmap(Common.IMAGE_BITMAP);
        back_btn=findViewById(R.id.back_btn);
        if ( name!= null && time!=null){
            time_txtview.setVisibility(View.VISIBLE);
            time_txtview.setText(time);
            user_name.setText(name);
        } else if (name!=null )  {
            time_txtview.setVisibility(View.GONE);
            user_name.setText(name);
        } else {
            user_name.setText("Profile picture");
        }
        back_btn.setOnClickListener(v -> onBackPressed());



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}