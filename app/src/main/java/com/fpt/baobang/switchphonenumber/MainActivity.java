package com.fpt.baobang.switchphonenumber;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentProviderOperation;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fpt.baobang.switchphonenumber.adapter.ContactApdater;
import com.fpt.baobang.switchphonenumber.listener.ItemClickListener;
import com.fpt.baobang.switchphonenumber.listener.UpdateCallBack;
import com.fpt.baobang.switchphonenumber.model.CustomContact;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private boolean mIsFromRequestPermission;
    private LinearLayout layoutContent;
    RecyclerView rvContacts;
    ContactApdater mApdater;
    List<CustomContact> mItems;
    LinearLayout layoutStatus;
    Button btnSwitch;
    CheckBox cbSelecteAll;
    TextView txtTitle, txtStatus;
    Toolbar toolbar;
    private int contactNeedEdit;

    private int processCount = 0;
    private int sucessed = 0;
    private String networkName;
    private String headNumber;
    private boolean isEditSucessed[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle bundle = getIntent().getExtras();

        networkName = (String) bundle.getSerializable(GroupContactActivity.NETWORK_HOME);
        headNumber = (String) bundle.getSerializable(GroupContactActivity.HEAD_NAME);


        // Gets the ListView from the View list of the parent activity
        initComponents();
        setEnvents();
        checkPermissionGranted();
    }

    private void setEnvents() {

        // click button cập nhật danh bạ
        btnSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSwitchPhoneNumber();
            }
        });

        cbSelecteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doClickCheckBoxSelectAll(cbSelecteAll.isChecked());
            }
        });

    }

    private void doClickCheckBoxSelectAll(boolean isChecked) {
        for (CustomContact customContact : mItems) {
            customContact.setEdit(isChecked);
        }
        mApdater.notifyDataSetChanged();
    }

    private void initComponents() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rvContacts = findViewById(R.id.rvContacts);
        btnSwitch = findViewById(R.id.btnSwitch);
        layoutStatus = findViewById(R.id.layoutStatuts);
        cbSelecteAll = findViewById(R.id.cbSelectAll);
        txtTitle = findViewById(R.id.toolbar_title);
        layoutContent = findViewById(R.id.layoutContent);
        txtStatus = findViewById(R.id.txtStatus);

        txtTitle.setText(networkName);
        rvContacts.setLayoutManager(new LinearLayoutManager(this));

        txtStatus.setText("Bạn đã chuyển thành công tất cả thuê bao 11 số có đầu số " + headNumber + " của nhà mạng " + networkName);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void checkPermissionGranted() {
        if (isPermissionGranted()) {
            loadData();
        }
    }

    private void loadData() {
        mItems = GroupContactActivity.COUNT.get(headNumber);
        mApdater = new ContactApdater(this, mItems, new ItemClickListener() {
            @Override
            public void onClick(int position) {
                CustomContact customContact = mItems.get(position);
                customContact.setEdit(!customContact.isEdit());
                cbSelecteAll.post(new Runnable() {
                    @Override
                    public void run() {
                        cbSelecteAll.setChecked(checkSelectAll());
                    }
                });
            }
        });
        rvContacts.setAdapter(mApdater);
        if (mItems != null && mItems.size() > 0) {
            layoutStatus.setVisibility(View.GONE);
            layoutContent.setVisibility(View.VISIBLE);
        } else {
            layoutStatus.setVisibility(View.VISIBLE);
            layoutContent.setVisibility(View.GONE);
        }
    }

    private boolean checkSelectAll() {

        for (CustomContact customContact : mItems) {
            if (!customContact.isEdit()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return (super.onOptionsItemSelected(item));

    }

    @Override
    public boolean onNavigateUp() {
        onBackPressed();
        return true;
    }

    private void doSwitchPhoneNumber() {

        processCount = 0;
        sucessed = 0;
        final int total = getContactNeedEdit();
        isEditSucessed = new boolean[mItems.size()];
        Log.e("TOTAL", total + "");
        if (total == 0) {
            DialogUtils.showMessage(this, "Thông báo", "Vui lòng chọn danh bạ cần chuyển.");
            return;
        }
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        final ProgressDialog progressDialog = DialogUtils.showLoading(this, "Chuyển đầu số", "Đang chuyển...", total);
        progressDialog.show();
        for (int i = mItems.size() - 1; i >= 0; i--) {
            final CustomContact cus = mItems.get(i);
            final int index = i;
            if (cus.isEdit()) {
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(200);
                            update(cus, new UpdateCallBack() {
                                @Override
                                public void onSucess() {
                                    sucessed++;
                                    processCount++;
                                    isEditSucessed[index] = true;
                                    progressDialog.incrementProgressBy(1);
                                    checkUpdateContactFinished(progressDialog, total);
                                }

                                @Override
                                public void onFailed() {
                                    progressDialog.incrementProgressBy(1);
                                    processCount++;
                                    isEditSucessed[index] = false;
                                    checkUpdateContactFinished(progressDialog, total);
                                }
                            });

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
        executorService.shutdown();

    }

//    private void updatePhoneNumber(ContentResolver contentResolver, CustomContact customContact) {
//        // Create content values object.
//        ContentValues contentValues = new ContentValues();
//
//        // Put new phone number value.
//        contentValues.put(ContactsContract.CommonDataKinds.Phone.NUMBER, customContact.getPhoneNumbers().getNewFirst() + customContact.getPhoneNumbers().getPhone());
//
//        // Create query condition, query with the raw contact id.
//        StringBuffer whereClauseBuf = new StringBuffer();
//
//        // Specify the update contact id.
//        whereClauseBuf.append(ContactsContract.Data.RAW_CONTACT_ID);
//        whereClauseBuf.append("=");
//        whereClauseBuf.append(customContact.getContactId());
//
//        // Specify the row data mimetype to phone mimetype( vnd.android.cursor.item/phone_v2 )
//        whereClauseBuf.append(" and ");
//        whereClauseBuf.append(ContactsContract.Data.MIMETYPE);
//        whereClauseBuf.append(" = '");
//        String mimetype = ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE;
//        whereClauseBuf.append(mimetype);
//        whereClauseBuf.append("'");
//
//        // Specify the row data mimetype to phone mimetype( vnd.android.cursor.item/phone_v2 )
//        whereClauseBuf.append(" and ");
//        whereClauseBuf.append(ContactsContract.CommonDataKinds.Phone._ID);
//        whereClauseBuf.append(" = ");
//        whereClauseBuf.append(customContact.getPhoneNumbers().getPhoneId());
//
//        // Specify phone type.
//        whereClauseBuf.append(" and ");
//        whereClauseBuf.append(ContactsContract.CommonDataKinds.Phone.TYPE);
//        whereClauseBuf.append(" = ");
//        whereClauseBuf.append(customContact.getPhoneNumbers().getType());
//
//        // Update phone info through Data uri.Otherwise it may throw java.lang.UnsupportedOperationException.
//        Uri dataUri = ContactsContract.Data.CONTENT_URI;
//
//        // Get update data count.
//        int updateCount = contentResolver.update(dataUri, contentValues, whereClauseBuf.toString(), null);
//        Log.e("Result", updateCount + "");
//    }

    private void update(CustomContact customContact, UpdateCallBack callBack) {
        try {
            ArrayList<ContentProviderOperation> ops = new ArrayList<>();
            ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                    .withSelection(
                            ContactsContract.CommonDataKinds.Phone._ID + " = ? AND " +
                                    ContactsContract.CommonDataKinds.Phone.TYPE + " = ? "
                            , new String[]{customContact.getPhoneNumbers().getPhoneId(),
                                    customContact.getPhoneNumbers().getType()})
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, customContact.getPhoneNumbers().getNewFirst() + customContact.getPhoneNumbers().getPhone())
                    .build());

            getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            callBack.onSucess();
        } catch (Exception e) {
            callBack.onFailed();
        }
    }

    private void checkUpdateContactFinished(ProgressDialog progressDialog, int total) {
        Log.e("compare", processCount + "-" + total);
        if (processCount == total) {
            progressDialog.dismiss();
            for (int i = mItems.size() - 1; i >= 0; i--) {
                if (isEditSucessed[i]) {
                    mItems.remove(i);
                }
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loadData();
                    DialogUtils.showMessage(MainActivity.this, "Thông báo", sucessed + "/" + processCount + " đầu số được chuyển thành công");
                }
            });

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case 2: {
                mIsFromRequestPermission = true;
                boolean isAllAllowed = true;
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        isAllAllowed = false;
                        break;
                    }
                }
                if (!isAllAllowed) {
                    Toast.makeText(this, "Không đủ quyền để chạy chương trình", Toast.LENGTH_SHORT).show();
                } else {
                    loadData();
                }
                return;
            }
        }
    }


    private boolean isPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            ArrayList<String> temp = new ArrayList<>();
            //READ_PHONE_STATE
            if (checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                temp.add(Manifest.permission.READ_CONTACTS);
            }
            //READ_EXTERNAL_STORAGE
            if (checkSelfPermission(Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                temp.add(Manifest.permission.WRITE_CONTACTS);
            }
            if (temp.size() > 0) {
                String permissions[] = new String[temp.size()];
                for (int i = 0; i < temp.size(); i++) {
                    permissions[i] = temp.get(i);
                }
                ActivityCompat.requestPermissions(this, permissions, 2);
                return false;
            }
        }
        return true;
    }

    public int getContactNeedEdit() {
        int count = 0;
        for (CustomContact customContact : mItems) {
            if (customContact.isEdit()) {
                count++;
            }
        }
        return count;
    }
}
