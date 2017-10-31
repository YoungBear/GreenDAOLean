package com.ysx.greendaolearn;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ysx.greendaolearn.adapter.PlayerAdapter;
import com.ysx.greendaolearn.entity.DaoMaster;
import com.ysx.greendaolearn.entity.DaoSession;
import com.ysx.greendaolearn.entity.Player;
import com.ysx.greendaolearn.entity.PlayerDao;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author ysx
 * @date 2017/10/31
 * @description
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @BindView(R.id.et_name)
    EditText mEtName;
    @BindView(R.id.et_age)
    EditText mEtAge;
    @BindView(R.id.btn_insert)
    Button mBtnInsert;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;


    private List<Player> mData = new ArrayList<>();
    private PlayerAdapter mAdapter;
    private Context mContext;

    /**
     * 数据库名称
     */
    private static final String DATABASE_NAME = "players.db";
    private DaoSession mDaoSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mContext = this;
        initRecyclerView();
        initDaoSession();
        loadData();
    }

    @OnClick({R.id.btn_insert})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_insert:
                addPlayer();
                loadData();
                break;
            default:
                break;
        }
    }

    /**
     * 初始化RecyclerView
     */
    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL);
        Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.recycler_view_divider);
        if (drawable != null) {
            dividerItemDecoration.setDrawable(drawable);
        }
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mAdapter = new PlayerAdapter(mContext, mData);
        mAdapter.setOnItemClickListener(new PlayerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Log.d(TAG, "onItemClick: position: " + position);
                deletePlayer(mData.get(position).getId());
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * 加载所有数据
     */
    private void loadData() {
        mData.clear();
        mData.addAll(getAllData());
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
        if (mData.size() <= 0) {
            Toast.makeText(mContext, "empty in database", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 初始化DaoSession
     * 即获取一个全局的DaoSession实例
     * // TODO: 2017/10/31 可以使用一个单例类单独管理这个对象
     */
    private void initDaoSession() {
        DaoMaster.OpenHelper openHelper = new DaoMaster.DevOpenHelper(
                mContext.getApplicationContext(), DATABASE_NAME, null);
        DaoMaster daoMaster = new DaoMaster(openHelper.getWritableDatabase());
        mDaoSession = daoMaster.newSession();
    }

    /**
     * 获取全部数据，按照Id升序排列
     *
     * @return 数据列表
     */
    private List<Player> getAllData() {
        PlayerDao playerDao = mDaoSession.getPlayerDao();
        return playerDao.queryBuilder()
                .orderAsc(PlayerDao.Properties.Id)
                .build()
                .list();
    }

    /**
     * 插入一条数据
     *
     * @param player
     */
    private void insertData(Player player) {
        PlayerDao playerDao = mDaoSession.getPlayerDao();
        playerDao.insert(player);
    }

    /**
     * 根据id删除一条数据
     *
     * @param id
     */
    private void deleteData(long id) {
        PlayerDao playerDao = mDaoSession.getPlayerDao();
        playerDao.deleteByKey(id);
    }

    /**
     * 更新一条数据
     * 更新年龄
     *
     * @param id
     * @param age
     */
    private void updateData(long id, int age) {
        Log.d(TAG, "updateData: id: " + id + ", age: " + age);
        PlayerDao playerDao = mDaoSession.getPlayerDao();
        Player player = playerDao.queryBuilder()
                .where(PlayerDao.Properties.Id.eq(id))
                .build()
                .unique();
        player.setAge(age);
        playerDao.update(player);
    }

    /**
     * 添加一个player
     */
    private void addPlayer() {
        String name = mEtName.getText().toString();
        String ageString = mEtAge.getText().toString();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(mContext, "please input name", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(ageString)) {
            Toast.makeText(mContext, "please input age", Toast.LENGTH_SHORT).show();
        }else {
            int age = Integer.parseInt(ageString);
            addPlayer(name, age);
        }
    }

    /**
     * 根据参数添加一个player
     *
     * @param name
     * @param age
     */
    private void addPlayer(String name, int age) {
        Log.d(TAG, "addUser: name: " + name + ", age: " + age);

        PlayerDao playerDao = mDaoSession.getPlayerDao();
        Player playerOld = playerDao.queryBuilder()
                .where(PlayerDao.Properties.Name.eq(name))
                .build()
                .unique();
        /**
         * 如果已经有相同的name，则更新age
         * 否则直接插入
         */
        if (playerOld != null) {
            updateData(playerOld.getId(), age);
        } else {
            Player player = new Player();
            player.setName(name);
            player.setAge(age);
            insertData(player);
        }
        loadData();
    }

    /**
     * 删除一条数据
     *
     * @param id
     */
    private void deletePlayer(final long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(R.string.delete_alert_tips)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteData(id);
                        loadData();

                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

}
