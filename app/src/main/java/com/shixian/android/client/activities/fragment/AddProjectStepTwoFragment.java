package com.shixian.android.client.activities.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.shixian.android.client.MyApplication;
import com.shixian.android.client.R;
import com.shixian.android.client.activities.NewProjectActivity;
import com.shixian.android.client.activities.fragment.base.BaseFragment;
import com.shixian.android.client.activities.fragment.base.UmengFragment;
import com.shixian.android.client.activities.fragment.interfaces.FragmentHelper;
import com.shixian.android.client.engine.ProjectEngine;
import com.shixian.android.client.utils.SharedPerenceUtil;
import com.shixian.android.client.views.DialgoFragment;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by tangtang on 15/3/31.
 */
public class AddProjectStepTwoFragment extends UmengFragment implements FragmentHelper{


    private static final String TAG="AddProjectStepTwoFragment";

    private static final int TYPE_ADMIN=0;
    private static final int TYPE_NOT_ADMIN=1;


    private ListView lv_admin;
    private TextView tv_description;


    private int currentSelect;

    private String[] types={"负责人","非负责人"};

    private AdminAdapter adapter;


    private String title;
    private String description;

    private NewProjectActivity activity;



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=initView(inflater);
        initDate(savedInstanceState);

        setHasOptionsMenu(true);

        activity= (NewProjectActivity) getActivity();
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        activity.setCurrentFragment(this);

    }

    public View initView(LayoutInflater inflater) {

        View view=inflater.inflate(R.layout.fragment_addprojectwo,null,false);

        lv_admin= (ListView) view.findViewById(R.id.lv_admin);
        tv_description= (TextView) view.findViewById(R.id.tv_description);
        return view;
    }


    public void initDate(Bundle savedInstanceState) {

        Bundle bundle= getArguments();
        title=bundle.getString("title");
        description=bundle.getString("description");

        currentSelect= SharedPerenceUtil.getNewFuzeren(getActivity().getApplicationContext());
        adapter=new AdminAdapter();
        lv_admin.setAdapter(adapter);

        lv_admin.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentSelect=position;
                adapter.notifyDataSetChanged();
            }
        });

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {
            case R.id.action_send:





                String fuzeren;

                if(currentSelect==TYPE_ADMIN)
                {
                    fuzeren="true";
                }else{
                    fuzeren="false";
                }


                ProjectEngine.addProject(getActivity(),title, description, fuzeren, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int position, Header[] headers, byte[] bytes) {

                        SharedPerenceUtil.clearNewProject(getActivity().getApplicationContext());
                        ((MyApplication)getActivity().getApplication()).setHasCaogao(false);
                        activity.setResult(Activity.RESULT_OK);
                        getActivity().finish();



                    }

                    @Override
                    public void onFailure(int position, Header[] headers, byte[] bytes, Throwable throwable) {

                        String str=new String(bytes);
                        if(!TextUtils.isEmpty(str))
                        {
                            try {
                                JSONObject object=new JSONObject(str);

                                Toast.makeText(getActivity(),"标题:"+object.getString("title"),Toast.LENGTH_SHORT).show();

                            } catch (JSONException e) {
                                Toast.makeText(getActivity(),"服务器异常",Toast.LENGTH_SHORT).show();

                            }


                        }else {
                            Toast.makeText(getActivity(), R.string.check_net, Toast.LENGTH_SHORT).show();
                        }


                    }
                });

                break;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_new_projectwo,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onBackPressed() {

        //返回处理
        SharedPerenceUtil.putNewFuzeren(activity.getApplicationContext(),currentSelect);
        getActivity().getSupportFragmentManager().popBackStack();


//            final DialgoFragment dialgoFragment=new DialgoFragment("您要放弃发表？","发布项目","确定","取消",new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    //这里做一些保存草稿的操作
//
//                    SharedPerenceUtil.putNewProject(getActivity(),title,description,currentSelect);
//
//                    ((MyApplication)getActivity().getApplication()).setHasCaogao(true);
//                    getActivity().finish();
//                }},null);
//
//            dialgoFragment.show(getActivity().getFragmentManager(), "loginDialog");




    }



    private class AdminAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return types.length;
        }

        @Override
        public Object getItem(int position) {
            return types[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view;
            Holder holder;

            if(convertView!=null)
            {
                view=convertView;
                holder= (Holder) view.getTag();
            }else{
                view=View.inflate(getActivity(),R.layout.item_admin,null);

                holder=new Holder();

                holder.iv_check= (ImageView) view.findViewById(R.id.iv_check);
                holder.tv_admin= (TextView) view.findViewById(R.id.tv_admin);

                view.setTag(holder);
            }


            if(position==currentSelect)
                holder.iv_check.setVisibility(View.VISIBLE);
            else
                    holder.iv_check.setVisibility(View.GONE);



            return view;
        }
    }


   private class Holder{
       TextView tv_admin;
       ImageView iv_check;
   }
}
