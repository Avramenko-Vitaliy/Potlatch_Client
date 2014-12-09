package com.deadpeace.potlatch.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.deadpeace.potlatch.Contract;
import com.deadpeace.potlatch.R;
import com.deadpeace.potlatch.activity.dialog.OpenFileDialog;
import com.deadpeace.potlatch.adapter.gift.Gift;
import com.deadpeace.potlatch.support.AsyncAddGift;

import java.io.File;
import java.util.Date;

/**
 * Created by Виталий on 10.11.2014.
 */
public class NewGift extends Activity
{
    private static File file;
    private LinearLayout container;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_gift);
        container=(LinearLayout)findViewById(R.id.content_image_or_place);
        container.addView(LayoutInflater.from(getApplicationContext()).inflate(file==null?R.layout.from_place:R.layout.for_image,null));
        ButterKnife.inject(this);
    }

    @OnClick(R.id.btn_from_camera)
    public void clickCamera()
    {
        //TODO start camera for take photo
        Log.i(Contract.LOG_TAG,"click on btn_camera");
        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        file=new File(Environment.getExternalStorageDirectory(),"gift_"+DateFormat.format("yyyymmdd_kkmmss",new Date())+".jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(file));
        startActivityForResult(intent,Contract.CAMERA_RESULT);
    }

    @OnClick(R.id.btn_from_directory)
    public void clickDirectory()
    {
        //TODO open file dialog for choose image file
        OpenFileDialog fileDialog=new OpenFileDialog(this).setFilter(".*\\.jpg")
                .setOpenDialogListener(new OpenFileDialog.OpenDialogListener()
                {
                    @Override
                    public void OnSelectedFile(String fileName)
                    {
                        file=new File(fileName);
                        setImageToGift(file);
                    }
                });
        fileDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data)
    {
        if(Contract.CAMERA_RESULT==requestCode)
            if(resultCode==-1&&file.exists())
                setImageToGift(file);
            else
                file=null;
    }

    private void setImageToGift(File file)
    {
        container.removeAllViews();
        LinearLayout layout=(LinearLayout)LayoutInflater.from(getApplicationContext()).inflate(R.layout.for_image,null);
        ((ImageView)layout.findViewById(R.id.image_gift)).setImageURI(Uri.fromFile(file));
        container.addView(layout);
    }

    @OnClick(R.id.btn_close)
    public void clickClose()
    {
        file=null;
        finish();
    }

    @OnClick(R.id.btn_done)
    public void clickDone()
    {
        EditText title=(EditText)findViewById(R.id.edit_title);
        if(title.getText().toString()==null)
            Toast.makeText(getApplicationContext(),R.string.toast_not_title,Toast.LENGTH_LONG).show();
        else
            if(file==null||!file.exists())
                Toast.makeText(getApplicationContext(),R.string.toast_file_not_exist,Toast.LENGTH_LONG).show();
            else
            {
                Gift gift=new Gift();
                gift.setTitle(title.getText().toString());
                gift.setDescription(((EditText)findViewById(R.id.edit_desc)).getText().toString());
                new AsyncAddGift(gift,file,this).execute();
            }
    }

    public void startProgress()
    {
        dialog=ProgressDialog.show(this,getApplicationContext().getResources().getString(R.string.pb_title),getApplicationContext().getResources().getString(R.string.pb_message),true,false);
    }

    public void endProgress(Gift result)
    {
        if(dialog!=null)
        {
            dialog.dismiss();
            if(result!=null && result.getId()>0)
            {
                file=null;
                setResult(Contract.ADD_GIFT,new Intent().putExtra(Contract.GIFT,result));
                finish();
            }
            else
                Toast.makeText(getApplicationContext(),R.string.msg_fail_upload,Toast.LENGTH_LONG).show();
        }
    }
}
