package com.ysx.greendaolearn.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ysx.greendaolearn.R;
import com.ysx.greendaolearn.entity.Player;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author ysx
 * @date 2017/10/31
 * @description
 */

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.ViewHolder> {
    private static final String TAG = "PlayerAdapter";

    private OnItemClickListener mOnItemClickListener;
    private List<Player> mData;
    private Context mContext;

    public PlayerAdapter(Context context, List<Player> data) {
        this.mContext = context;
        this.mData = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_item_player, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Player player = mData.get(position);
        holder.mTvName.setText(player.getName());
        holder.mTvId.setText(String.valueOf(player.getId()));
        holder.mTvAge.setText(mContext.getString(R.string.player_age, player.getAge()));

        holder.mTvChampion.setText(mContext.getString(R.string.player_champion, player.getChampion()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(view, holder.getLayoutPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public interface OnItemClickListener {
        /**
         * item 点击事件
         *
         * @param v
         * @param position
         */
        void onItemClick(View v, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    static final class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_name)
        TextView mTvName;
        @BindView(R.id.tv_id)
        TextView mTvId;
        @BindView(R.id.tv_age)
        TextView mTvAge;
        @BindView(R.id.tv_champion)
        TextView mTvChampion;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
