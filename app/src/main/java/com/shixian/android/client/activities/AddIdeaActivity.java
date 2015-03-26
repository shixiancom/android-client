package com.shixian.android.client.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.shixian.android.client.R;
import com.shixian.android.client.activities.fragment.ProjectFeedFragment;
import com.shixian.android.client.activities.fragment.base.AbsListViewBaseFragment;
import com.shixian.android.client.engine.ProjectEngine;
import com.shixian.android.client.views.DarkAlertDialog;
import com.shixian.android.client.views.DialgoFragment;
import com.shixian.android.client.views.LightProgressDialog;

import org.apache.http.Header;


/**
 * Created by tangtang on 15/3/25.
 */
public class AddIdeaActivity  extends UmengActivity {


    private static final String TAG="AddIdeaActivity";

    private static final int LESS_TEXT_LENGTH=10;

    private String projectid;


    private Handler handler=new Handler() ;




    private Toolbar toolbar;

    private EditText et_comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_idea);

        projectid=getIntent().getStringExtra(ProjectFeedFragment.PROJECT_ID);



        et_comment= (EditText) findViewById(R.id.et_comment);



        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("发表想法");
        toolbar.setSubtitle("(至少输入"+LESS_TEXT_LENGTH+"个字)");
        toolbar.setSubtitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        et_comment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                int distance=LESS_TEXT_LENGTH-s.toString().length();
                if(distance<=0)
                {
                    toolbar.setSubtitle(null);
                }else{
                    toolbar.setSubtitle("(还差"+distance+"个字)");
                }
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // Handle Back Navigation :D
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddIdeaActivity.this.onBackPressed();
            }
        });



    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId())
        {
            case R.id.action_send:


                if(et_comment.getText().toString().length()<LESS_TEXT_LENGTH)
                {
                    Toast.makeText(this,"你输入的字数少于"+LESS_TEXT_LENGTH+" 不能发送",Toast.LENGTH_SHORT).show();
                    break;
                }


                final AlertDialog progressDialog= DarkAlertDialog.create(AddIdeaActivity.this);
                progressDialog.setMessage("正在发送中");


                progressDialog.show();

                //1 开启一个progressbar



                ProjectEngine.addIdea(projectid,et_comment.getText().toString(),new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int position, Header[] headers, byte[] bytes) {


                        /**
                         *
                         * 2  projressbar消失
                         * 3 发送消失给fragment
                         * 4 刷新 fragment
                         */
                        progressDialog.dismiss();

                        String result=new String(bytes);
                        if(result.contains("false"))
                        {
                            Toast.makeText(AddIdeaActivity.this,"评论失败",Toast.LENGTH_SHORT).show();

                        }else{

                            //handler.sendEmptyMessage();


                            setResult(Activity.RESULT_OK);

                            finish();

                        }


                    }

                    @Override
                    public void onFailure(int position, Header[] headers, byte[] bytes, Throwable throwable) {
                       // Toast.makeText(AddIdeaActivity.this,R.string.check_net,Toast.LENGTH_SHORT).show();
                        Toast.makeText(AddIdeaActivity.this,new String(bytes),Toast.LENGTH_SHORT).show();

                    }
                });

                break;

        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {

        if(et_comment.getText().toString().isEmpty())
        {
            finish();
        }else{

           final  DialgoFragment dialgoFragment=new DialgoFragment("您要放弃发表？","发表想法","确定","取消",new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int which) {
                   finish();
               }},null);



            dialgoFragment.show(getFragmentManager(), "loginDialog");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

         MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_add_idea,menu);
        return super.onCreateOptionsMenu(menu);

    }
}
