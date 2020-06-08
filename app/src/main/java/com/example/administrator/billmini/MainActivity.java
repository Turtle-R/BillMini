package com.example.administrator.billmini;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<CostBean> mCostBeanList;
    private Context mContext;
    private DatabaseHelper mDatabaseHelper;
    private CostListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDatabaseHelper=new DatabaseHelper(this);

        mCostBeanList=new ArrayList<>();
        initCostData();
        ListView costList= (ListView) findViewById(R.id.lv_main);

        mAdapter=new CostListAdapter(this,mCostBeanList);
        costList.setAdapter(mAdapter);


        costList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("删除");
                builder.setMessage("是否删除这笔账单?");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDatabaseHelper.deleteOne(mCostBeanList.get(position));
                        mCostBeanList.remove(position);
                        mAdapter.notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("Cancel", null);
                builder.create().show();
            }
        });



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                 //       .setAction("Action", null).show();
                AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                LayoutInflater inflater=LayoutInflater.from(MainActivity.this);
                View viewDialog=inflater.inflate(R.layout.new_cost_data,null);
                final EditText title= (EditText) viewDialog.findViewById(R.id.et_cost_title);
                final EditText money= (EditText) viewDialog.findViewById(R.id.et_cost_money);
                final DatePicker date= (DatePicker) viewDialog.findViewById(R.id.dp_cost_date);

                builder.setView(viewDialog);
                builder.setTitle("新的花费");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Field field = null;
                        try {
                            //通过反射获取dialog中的私有属性mShowing
                            field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                            field.setAccessible(true);//设置该属性可以访问
                            } catch (Exception ex) {

                            }
                        CostBean costBean=new CostBean();
                        costBean.costTitle=title.getText().toString();
                        costBean.costMoney=money.getText().toString();
                        costBean.costDate=date.getYear()+"-"+(date.getMonth()+1)+"-"+date.getDayOfMonth();
                        if(costBean.costTitle==null||"".equals(costBean.costTitle)||costBean.costMoney==null||"".equals(costBean.costMoney))
                        {
                            try {
                                //设置dialog不可关闭
                                field.set(dialog, false);
                                dialog.dismiss();
                                } catch (Exception ex) {
                                }
                        }
                        else {

                            mDatabaseHelper.insertCost(costBean);
                            mCostBeanList.add(costBean);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                });

                builder.setNegativeButton("Cancel",null);

                builder.create().show();


                //创建并展示
                //builder.create().show();


            }
        });

    }

    private void initCostData() {
        Cursor cursor= mDatabaseHelper.getAllCostData();
        if (cursor!=null){
            while (cursor.moveToNext()){
                CostBean costBean=new CostBean();
                costBean.costTitle=cursor.getString(cursor.getColumnIndex("cost_title"));
                costBean.costDate=cursor.getString(cursor.getColumnIndex("cost_date"));
                costBean.costMoney=cursor.getString(cursor.getColumnIndex("cost_money"));
                mCostBeanList.add(costBean);
            }
            cursor.close();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.chart_all) {
            Log.v("MainActivity","Click chart_all");
            Toast.makeText(MainActivity.this,"Click chart_all",Toast.LENGTH_SHORT).show();
            Intent intent =new Intent(MainActivity.this,ChartsActivity.class);
            intent.putExtra("cost_list",(Serializable) mCostBeanList);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.delete_all) {
            Toast.makeText(MainActivity.this,"Click delete_all",Toast.LENGTH_SHORT).show();

            mDatabaseHelper.deleteAllData();
            mCostBeanList.clear();
            mAdapter.notifyDataSetChanged();
            return true;
        }
        else if(id == R.id.about_all){
            Intent intent = new Intent();
            intent.setClass(MainActivity.this,AboutActivity.class);
            Log.v("MainActivity","Click About_all");
            Toast.makeText(MainActivity.this,"Click about_all",Toast.LENGTH_SHORT).show();
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
