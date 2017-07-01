package com.xing.loadingview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.xing.loadingviewlib.LoadingView;

public class MainActivity extends AppCompatActivity {

    private LoadingView loadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadingView = (LoadingView) findViewById(R.id.load_view);

    }

    public void click(View view) {
        switch (view.getId()) {
            case R.id.btn_success:
                loadingView.updateState(LoadingView.StateEnum.LOAD_SUCCESS);
                break;
            case R.id.btn_fail:
                loadingView.updateState(LoadingView.StateEnum.LOAD_FAILED);
                break;
        }

    }


}
