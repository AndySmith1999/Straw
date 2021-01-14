package com.example.straw;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.straw.MyAdapter;
import com.example.straw.UtilDao;
import com.example.straw.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class contact_activity extends AppCompatActivity implements View.OnClickListener {

    private TextView textNum;
    private FloatingActionButton button;
    private ListView listView;
    private List<User> list,newList;
    private UtilDao dao;
    private MyAdapter adapter;
    private int listNum = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_layout);
        //Initialization control unit
        initWidget();
        DbUtil();
        //Show the ListView
        showListView();
        //Show the number of listView
        linkmanNum();
    }
    /**
     * Initialization control unit
     * */
    private void initWidget(){
        button = findViewById(R.id.floatbutton1);
        listView = findViewById(R.id.main_list_view);
        textNum = findViewById(R.id.main_num);
        newList = new ArrayList<>();
        list = new ArrayList<>();
    }

    /**
     *  Show ListView
     * */
    public void showListView(){
        /**
         * Add data to the list
         * **/
        list = dao.inquireData();

        /**
         * Create and bind the adapter
         * */
        adapter = new MyAdapter(this,R.layout.item,list);
        listView.setAdapter(adapter);

        /**
         * ListView Listener
         * */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                dialogList();
                listNum = i;
            }
        });

        button.setOnClickListener(this);
    }

    /**
     * dialog interface
     * */
    public void dialogNormal(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        DialogInterface.OnClickListener dialogOnClick = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                User userDel = list.get(listNum);
                switch (i){
                    case DialogInterface.BUTTON_POSITIVE:
                        dao.delData(getResources().getString(R.string.username),new String[]{userDel.getName()});
                        refresh();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                    default:break;
                }
            }
        };
        builder.setTitle(getResources().getString(R.string.delete));
//        builder.setMessage(getResources().getString(R.string.delete));
        builder.setPositiveButton(getResources().getString(R.string.sure), dialogOnClick);
        builder.setNegativeButton(getResources().getString(R.string.cancel),dialogOnClick);
        builder.create().show();
    }

    /**
     * selection list
     * */
    
    public void dialogList(){
        final String[] items = {getResources().getString(R.string.ring),getResources().getString(R.string.sms),getResources().getString(R.string.edit),getResources().getString(R.string.delete1)};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                User userNum = list.get(listNum);
                Intent intent;
                switch (i){
                    //make a phone call
                    case 0: intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + userNum.getPhone()));
                        startActivity(intent);
                        break;
                    //send SMS
                    case 1: intent = new Intent(Intent.ACTION_SENDTO);
                        intent.setData(Uri.parse("smsto:" + userNum.getPhone()));
                        startActivity(intent);
                        break;
                    case 2: intent = new Intent(contact_activity.this,AddData.class);
                        //传入当前选中项的姓名和电话以在编辑页面中显示在输入框中
                        intent.putExtra("edit_name",userNum.getName().toString());
                        intent.putExtra("edit_phone",userNum.getPhone().toString());
                        startActivityForResult(intent,2);
                        break;
                    //it will give out a dialog asking for whether delete the contact
                    case 3: dialogNormal();
                        break;
                    default:
                        break;
                }
            }
        });
        builder.create().show();
    }

    //刷新
    public void refresh(){
        //最后查询数据刷新列表
        getNotifyData();
    }

    //页面顶部显示ListView条目数
    public void linkmanNum(){
        textNum.setText("("+list.size()+")");
    }

    //点击添加按钮
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.floatbutton1:
                //跳转到 AddData Activity 传入请求码 1
                Intent intent = new Intent(contact_activity.this,AddData.class);
                startActivityForResult(intent,1);
                break;
            default:break;
        }
    }

    public void DbUtil(){
        dao = ((MyApplication)this.getApplication()).getDao();
    }

    /**
     * 当页面回到此活动时，调用此方法，刷新ListView
     * */
    @Override
    protected void onResume() {
        super.onResume();
        getNotifyData();
    }

    /**
     * 这个是用来动态刷新 * */
    public void getNotifyData(){
        //使用新的容器获得最新查询出来的数据
        newList = dao.inquireData();
        //清除原容器里的所有数据
        list.clear();
        //将新容器里的数据添加到原来容器里
        list.addAll(newList);
        //更新页面顶部括号里显示数据
        linkmanNum();
        //刷新适配器
        adapter.notifyDataSetChanged();
    }

    /**
     *  Here do the modifies to the database
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            // When user click the ADD button
            case 1:
                // Add user to the database
                if (resultCode == RESULT_OK) {
                    String[] key = data.getStringArrayExtra("key");
                    String[] values = data.getStringArrayExtra("values");
                    dao.addData("UserInfo", key, values);
                }
                break;
            // When user click the EDIT button
            case 2:
                // Edit information in the database
                if (resultCode == RESULT_OK) {
                    User user = list.get(listNum);
                    String name = data.getStringExtra("name");
                    String phone = data.getStringExtra("phone");
                    String[] values = {name, phone, user.getName()};
                    dao.update(values);
                }
                break;
        }
    }
}