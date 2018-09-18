package com.fpt.baobang.switchphonenumber.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.fpt.baobang.switchphonenumber.R;
import com.fpt.baobang.switchphonenumber.listener.ItemClickListener;
import com.fpt.baobang.switchphonenumber.model.CustomContact;

import java.util.List;

public class ContactApdater extends RecyclerView.Adapter<ContactApdater.ContactHolder> {
    private List<CustomContact> items;
    private Context mContext;
    private ItemClickListener listener;

    public ContactApdater(Context mContext, List<CustomContact> items, ItemClickListener listener) {
        this.items = items;
        this.mContext = mContext;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item, parent, false);
        return new ContactHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactHolder holder, int position) {
        holder.bindView(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    public class ContactHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtFirstPhone, txtPhone, txtNewFirstPhone, txtNewPhone;
        CheckBox cbSelected;

        public ContactHolder(View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtPhone = itemView.findViewById(R.id.txtPhone);
            txtFirstPhone = itemView.findViewById(R.id.txtFirstPhone);
            txtNewFirstPhone = itemView.findViewById(R.id.txtFirstNewPhone);
            txtNewPhone = itemView.findViewById(R.id.txtNewPhone);
            cbSelected = itemView.findViewById(R.id.cbSelected);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(getAdapterPosition());
                    cbSelected.post(new Runnable() {
                        @Override
                        public void run() {
                            cbSelected.setChecked(items.get(getAdapterPosition()).isEdit());
                        }
                    });
                }
            });
        }

        public void bindView(final CustomContact customContact) {
            txtName.setText(customContact.getName());
            txtFirstPhone.setText(customContact.getPhoneNumbers().getFirst());
            txtPhone.setText(customContact.getPhoneNumbers().getPhone());
            txtNewFirstPhone.setText(customContact.getPhoneNumbers().getNewFirst());
            txtNewPhone.setText(customContact.getPhoneNumbers().getPhone());
            cbSelected.post(new Runnable() {
                @Override
                public void run() {
                    cbSelected.setChecked(customContact.isEdit());
                }
            });
        }
    }
}
