package com.shixian.android.client.activities.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.shixian.android.client.MyApplication;
import com.shixian.android.client.R;
import com.shixian.android.client.activities.MainActivity;
import com.shixian.android.client.activities.NewProjectActivity;
import com.shixian.android.client.activities.fragment.base.UmengFragment;
import com.shixian.android.client.activities.fragment.interfaces.FragmentHelper;
import com.shixian.android.client.utils.SharedPerenceUtil;
import com.shixian.android.client.views.DialgoFragment;

/**
 * Created by tangtang on 15/3/31.
 */
public class AddProjectStepOneFragment extends UmengFragment implements FragmentHelper{

    private EditText et_title;
    private EditText et_content;

    private NewProjectActivity activity;

    private static final int MIN_LEN=70;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =initView(inflater);

        initDate(savedInstanceState);

        return view;
    }

    public View initView(LayoutInflater inflater) {

        View view=inflater.inflate(R.layout.fragment_addprojectone,null,false);
        et_title= (EditText) view.findViewById(R.id.et_title);
        et_content= (EditText) view.findViewById(R.id.et_content);

        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity= (NewProjectActivity) getActivity();
    }

    public void initDate(Bundle savedInstanceState) {

        et_title.setText(SharedPerenceUtil.getNewProjectTitle(getActivity().getApplicationContext()));
        et_content.setText(SharedPerenceUtil.getNewProjectContent(getActivity().getApplicationContext()));

    }


    @Override
    public void onResume() {
        super.onResume();
        activity.setCurrentFragment(this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {
            case R.id.action_send:

                if(TextUtils.isEmpty(et_title.getText().toString().trim()))
                {
                    Toast.makeText(activity,"标题不能为空",Toast.LENGTH_SHORT).show();
                    return true;
                }

                if(et_content.getText().toString().trim().length()<MIN_LEN)
                {
                    Toast.makeText(activity,"内容不能小于"+MIN_LEN+"字",Toast.LENGTH_SHORT).show();
                    return true;
                }

                //进入下一步

                AddProjectStepTwoFragment fragment=new AddProjectStepTwoFragment();

                Bundle bundle=new Bundle();


                bundle.putString("title",et_title.getText().toString());
                bundle.putString("description",et_content.getText().toString());

                fragment.setArguments(bundle);
                activity.addFragment(fragment);






//
//                ProjectEngine.addProject(et_title.getText().toString().trim(), et_content.getText().toString().trim(), fuzeren, new AsyncHttpResponseHandler() {
//                    @Override
//                    public void onSuccess(int position, Header[] headers, byte[] bytes) {
//                        Toast.makeText(getActivity(), new String(bytes), Toast.LENGTH_SHORT).show();
//
//
//                    }
//
//                    @Override
//                    public void onFailure(int position, Header[] headers, byte[] bytes, Throwable throwable) {
//                        Toast.makeText(getActivity(), new String(bytes), Toast.LENGTH_SHORT).show();
//                    }
//                });

                break;
        }


        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_new_projectone,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }



    public void onBackPressed() {




        if(et_title.getText().toString().trim().isEmpty()&&et_content.getText().toString().trim().isEmpty())
        {
            getActivity().finish();
            SharedPerenceUtil.clearNewProject(getActivity().getApplicationContext());
            ((MyApplication)getActivity().getApplication()).setHasCaogao(false);
        }else{

            final DialgoFragment dialgoFragment=new DialgoFragment("您要放弃发表？","发布项目","确定","取消",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //这里做一些保存草稿的操作

                    SharedPerenceUtil.putNewProject(getActivity().getApplicationContext(),et_title.getText().toString(),et_content.getText().toString());
                    ((MyApplication)getActivity().getApplication()).setHasCaogao(true);
                    getActivity().finish();
                }},null);

            dialgoFragment.show(getActivity().getFragmentManager(), "loginDialog2");


        }

    }





}
