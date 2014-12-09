package com.deadpeace.potlatch.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.deadpeace.potlatch.Contract;
import com.deadpeace.potlatch.PotlatchSvc;
import com.deadpeace.potlatch.R;
import com.deadpeace.potlatch.client.PotlatchSvcApi;
import retrofit.RetrofitError;

/**
 * Created with IntelliJ IDEA.
 * User: DeadPeace
 * Date: 21.10.2014
 * Time: 11:02
 * To change this template use File | Settings | File Templates.
 */
public class LoginActivity extends Activity
{
    @InjectView(R.id.btn_sign_in)
    protected Button btnSignIn;

    @InjectView(R.id.btn_exit)
    protected Button btnExit;

    @InjectView(R.id.progressBar)
    protected ProgressBar progress;

    private final Handler handler=new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch(msg.what)
            {
                case Contract.ERROR:
                    Toast.makeText(LoginActivity.this, R.string.msg_fail_connected, Toast.LENGTH_LONG).show();
                    break;
                case Contract.SUCCESS:
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                    break;
            }
            btnSignIn.setEnabled(true);
            btnExit.setEnabled(true);
            progress.setVisibility(View.INVISIBLE);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        ButterKnife.inject(this);
    }

    @OnClick(R.id.btn_sign_in)
    public void doSignIn()
    {
        final PotlatchSvcApi svc=PotlatchSvc.init(((EditText) findViewById(R.id.edit_login)).getText().toString(), ((EditText) findViewById(R.id.edit_pass)).getText().toString());
        btnSignIn.setEnabled(false);
        btnExit.setEnabled(false);
        progress.setVisibility(View.VISIBLE);
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    Contract.setUser(svc.getUser());
                    handler.sendEmptyMessage(Contract.SUCCESS);
                }
                catch(RetrofitError e)
                {
                    Log.e(Contract.LOG_TAG, "Error logging in via OAuth.", e);
                    handler.sendEmptyMessage(Contract.ERROR);
                }
            }
        }).start();
    }

    @OnClick(R.id.btn_exit)
    public void doExit()
    {
        finish();
    }
}
