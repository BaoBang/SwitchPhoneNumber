package com.fpt.baobang.switchphonenumber.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fpt.baobang.switchphonenumber.GroupContactActivity;
import com.fpt.baobang.switchphonenumber.R;
import com.fpt.baobang.switchphonenumber.listener.GroupCallBack;
import com.fpt.baobang.switchphonenumber.listener.ItemClickListener;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.ArrayList;
import java.util.List;

public class HomeNetWorkAdapter extends RecyclerView.Adapter<HomeNetWorkAdapter.HomeNetWorkHolder> {

    private Context context;
    private List<String> mItems;
    private GroupCallBack callBack;
    private boolean[] isExpaned;

    public HomeNetWorkAdapter(Context context, List<String> mItems, GroupCallBack callBack) {
        this.context = context;
        this.mItems = mItems;
        this.callBack = callBack;
        isExpaned = new boolean[mItems.size()];
    }

    @NonNull
    @Override
    public HomeNetWorkHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_home_network, parent, false);
        return new HomeNetWorkHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeNetWorkHolder holder, int position) {
        holder.bindView(mItems.get(position));
    }

    @Override
    public int getItemCount() {
        return mItems == null ? 0 : mItems.size();
    }

    public class HomeNetWorkHolder extends RecyclerView.ViewHolder {
        TextView txtName;
        RecyclerView rvGroups;
        GroupAdapter adapter;
        List<String> mmItems;
        LinearLayout layoutHeader;
        ExpandableLayout expandableLayout;
        ImageView imgArrow;

        public HomeNetWorkHolder(View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            rvGroups = itemView.findViewById(R.id.rvGroups);
            rvGroups.setLayoutManager(new LinearLayoutManager(context));
            expandableLayout = itemView.findViewById(R.id.expanLayout);
            layoutHeader = itemView.findViewById(R.id.layoutHeader);
            imgArrow = itemView.findViewById(R.id.imgArrow);
        }

        public void bindView(String s) {
            txtName.setText(s);
            mmItems = new ArrayList<>(GroupContactActivity.MAP.get(s).keySet());
            adapter = new GroupAdapter(context, mmItems, new ItemClickListener() {
                @Override
                public void onClick(int position) {
                    callBack.onCLick(mItems.get(getAdapterPosition()), mmItems.get(position));
                }
            });
            rvGroups.setAdapter(adapter);
            if(isExpaned[getAdapterPosition()]){
                expandableLayout.expand();
                imgArrow.setImageResource(R.drawable.ic_arrow_up);
            }else {
                expandableLayout.collapse();
                imgArrow.setImageResource(R.drawable.ic_arrow_down);
            }
            layoutHeader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isExpaned[getAdapterPosition()]){
                        expandableLayout.collapse();
                        imgArrow.setImageResource(R.drawable.ic_arrow_down);
                        isExpaned[getAdapterPosition()] = false;
                    }else{
                        expandableLayout.expand();

                        imgArrow.setImageResource(R.drawable.ic_arrow_up);
                        isExpaned[getAdapterPosition()] = true;
                    }

                }
            });

        }
    }
}
