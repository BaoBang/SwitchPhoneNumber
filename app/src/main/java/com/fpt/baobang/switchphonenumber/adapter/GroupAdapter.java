package com.fpt.baobang.switchphonenumber.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fpt.baobang.switchphonenumber.GroupContactActivity;
import com.fpt.baobang.switchphonenumber.R;
import com.fpt.baobang.switchphonenumber.listener.ItemClickListener;

import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupHolder> {
    private Context context;
    private List<String> mItems;
    private ItemClickListener callBack;

    public GroupAdapter(Context context, List<String> mItems, ItemClickListener callBack) {
        this.context = context;
        this.mItems = mItems;
        this.callBack = callBack;
    }

    @NonNull
    @Override
    public GroupHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_group, parent, false);
        return new GroupHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupHolder holder, int position) {
        holder.bindView(mItems.get(position));
    }

    @Override
    public int getItemCount() {
        return mItems == null ? 0 : mItems.size();
    }

    public class GroupHolder extends RecyclerView.ViewHolder {
        TextView txtFirst, txtTotal;
        public GroupHolder(View itemView) {
            super(itemView);
            txtFirst = itemView.findViewById(R.id.txtFirst);
            txtTotal = itemView.findViewById(R.id.txtTotal);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callBack.onClick(getAdapterPosition());
                }
            });
        }

        public void bindView(String s) {
            txtFirst.setText(s);
            int size = GroupContactActivity.COUNT.get(s) == null ? 0 : GroupContactActivity.COUNT.get(s).size();
            txtTotal.setText("Tổng: " + size);
        }
    }
}
