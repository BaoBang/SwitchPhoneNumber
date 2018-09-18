package com.fpt.baobang.switchphonenumber;

import android.Manifest;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.fpt.baobang.switchphonenumber.adapter.HomeNetWorkAdapter;
import com.fpt.baobang.switchphonenumber.listener.GroupCallBack;
import com.fpt.baobang.switchphonenumber.model.CustomContact;
import com.fpt.baobang.switchphonenumber.model.Phone;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupContactActivity extends AppCompatActivity {
    private boolean isLoad = false;
    private String IS_BACKUP = "is_backup";
    String vfile = "backup-contacts.vcf";
    String storage_path = Environment.getExternalStorageDirectory().toString() + "/" + vfile;
    public static Map<String, HashMap<String, String>> MAP = new HashMap<>();
    public static Map<String, List<CustomContact>> COUNT = new HashMap();
    public static String NETWORK_HOME = "network_home";
    public static String HEAD_NAME = "head_name";
    Toolbar toolbar;

    private void firstInit() {
        // Vietel
        HashMap<String, String> map = new HashMap<>();
        map.put("0162", "032");
        map.put("0163", "033");
        map.put("0164", "034");
        map.put("0165", "035");
        map.put("0166", "036");
        map.put("0167", "037");
        map.put("0168", "038");
        map.put("0169", "039");
        MAP.put("Vietel", map);
        // mobifone
        HashMap<String, String> moibi = new HashMap<>();
        moibi.put("0120", "070");
        moibi.put("0121", "079");
        moibi.put("0122", "077");
        moibi.put("0126", "076");
        moibi.put("0128", "078");
        MAP.put("Mobifone", moibi);
        // vinaphone
        HashMap<String, String> vinaphone = new HashMap<>();
        vinaphone.put("0123", "083");
        vinaphone.put("0124", "084");
        vinaphone.put("0125", "085");
        vinaphone.put("0127", "081");
        vinaphone.put("0129", "082");
        MAP.put("Vinaphone", vinaphone);
        // vietnamobile
        HashMap<String, String> vietnamobile = new HashMap<>();
        vietnamobile.put("0186", "056");
        vietnamobile.put("0188", "058");
        MAP.put("Vietnamobile", vietnamobile);
        // Gmobile
        HashMap<String, String> Gmobile = new HashMap<>();
        Gmobile.put("0199", "059");
        MAP.put("Gmobile", Gmobile);
        // cố định VSAT
//        HashMap<String, String> vsat = new HashMap<>();
//        vsat.put("0992", "0672");
//        MAP.put("Cố định VSAT", vsat);
    }

    private RecyclerView rvGroups;
    private HomeNetWorkAdapter mAdapter;
    private List<String> mItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_contact);

        firstInit();
        addComponents();
        checkPermissionGranted();

    }

    private void addContac() {

        int a = 300;
        try {
            for (int i = 1; i <= 10; i++) {
                ArrayList<ContentProviderOperation> ops =
                        new ArrayList<ContentProviderOperation>();
                int rawContactInsertIndex = ops.size();
                ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null).build());
                ops.add(ContentProviderOperation
                        .newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, "Test" + i) // Name of the person
                        .build());
                ops.add(ContentProviderOperation
                        .newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(
                                ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, "01672099" + (a + i)) // Number of the person
                        .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE).build()); // Type of mobile number
                getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
    }

    public void checkPermissionGranted() {
        if (isPermissionGranted()) {
            loadData();
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
            //READ_EXTERNAL_STORAGE
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                temp.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case 2: {
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
            case 200:
                loadData();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
        if (isLoad) {
            loadData();
        }
    }

    private void addComponents() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        mItems = new ArrayList<>(MAP.keySet());
        mAdapter = new HomeNetWorkAdapter(this, mItems, new GroupCallBack() {
            @Override
            public void onCLick(String name, String headNumber) {
                goToMainActivivy(name, headNumber);
            }
        });
        rvGroups = findViewById(R.id.rvGroups);
        rvGroups.setLayoutManager(new LinearLayoutManager(this));
        rvGroups.setAdapter(mAdapter);
    }

    private void goToMainActivivy(String name, String headNumber) {
        if (GroupContactActivity.COUNT.get(headNumber) == null || GroupContactActivity.COUNT.get(headNumber).size() == 0) {
            DialogUtils
                    .showMessage(this, "Thông báo", "Đầu số " + headNumber + " của nhà mạng " + name + " đã được chuyển.");
            return;
        }
        Intent intent = new Intent(this, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(GroupContactActivity.NETWORK_HOME, name);
        bundle.putSerializable(GroupContactActivity.HEAD_NAME, headNumber);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void loadData() {
//        addContac();
        GroupContactActivity.COUNT = new HashMap<>();
        new GetData(this).execute();
    }

    private String getNewFirst(String first) {
        for (String key : MAP.keySet()) {
            HashMap<String, String> map = MAP.get(key);
            if (map.get(first) != null && map.get(first).length() > 0) {
                return map.get(first);
            }
        }
        return null;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (String key : GroupContactActivity.COUNT.keySet()) {
            if (GroupContactActivity.COUNT.get(key) != null) {
                GroupContactActivity.COUNT.get(key).clear();
            }
        }
        MAP.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mnBackup:
                doBackUp();
                break;
            case R.id.mnRestore:
                doRestrore();
                break;
        }
        return true;
    }

    private void doRestrore() {

        File file = new File(storage_path);
        if (!(file.exists() && getIsBackup())) {
            DialogUtils.showMessage(this, "Thông báo", "Dữ liệu chưa được sao lưu");
            return;
        }
        new RestoreContact(this).execute(file);
    }

    private void removeAllContacts() {
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (cursor == null) {
            return;
        }
        while (cursor.moveToNext()) {
            String lookupKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
            Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
            contentResolver.delete(uri, null, null);
        }
    }

    private void doBackUp() {
        File file = new File(storage_path);
        if (file.exists() && getIsBackup()) {
            DialogUtils.showMessage(this, "Thông báo", "Dữ liệu đã được sao lưu");
            return;
        }
        new BackUpContact(this).execute();
    }

    public void saveIsBackUp(boolean isBackUp) {
        SharedPreferences mPreferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(IS_BACKUP, isBackUp);
        editor.commit();
    }

    public boolean getIsBackup() {
        SharedPreferences mPreferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mPreferences.edit();
        return mPreferences.getBoolean(IS_BACKUP, false);
    }

    class GetData extends AsyncTask<Void, Void, Void> {
        private GroupContactActivity groupContactActivity;

        public GetData(GroupContactActivity groupContactActivity) {
            this.groupContactActivity = groupContactActivity;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            DialogUtils.showLoading(groupContactActivity, "Tải dữ liệu...");
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mAdapter.notifyDataSetChanged();
            DialogUtils.hideLoading();
            isLoad = false;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            String phoneNumber = null;
            Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
            String _ID = ContactsContract.Contacts._ID;
            String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
            String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

            Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
            String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
            String phoneID = ContactsContract.CommonDataKinds.Phone._ID;
            String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;
            String TYPE = ContactsContract.CommonDataKinds.Phone.TYPE;

            ContentResolver contentResolver = groupContactActivity.getContentResolver();

            Cursor cursor = contentResolver.query(CONTENT_URI, null, null, null, null);
            if (cursor == null) {
                return null;
            }
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {

                    String contact_id = cursor.getString(cursor.getColumnIndex(_ID));
                    String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));
                    int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_PHONE_NUMBER)));
                    if (hasPhoneNumber > 0) {
                        Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[]{contact_id}, null);
                        if (phoneCursor != null) {
                            while (phoneCursor.moveToNext()) {
                                phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER)).replace("-", "").replace(" ", "").replace("+84", "0");
                                if (phoneNumber.length() > 10) {
                                    String type = phoneCursor.getString(phoneCursor.getColumnIndex(TYPE));
                                    String phoneId = phoneCursor.getString(phoneCursor.getColumnIndex(phoneID));
                                    CustomContact customContact = new CustomContact();
                                    customContact.setContactId(contact_id);
                                    customContact.setName(name);
                                    String first = phoneNumber.substring(0, 4);
                                    String newFirst = getNewFirst(first);
                                    if (newFirst != null && newFirst.length() > 0) {
                                        customContact.setPhoneNumbers(new Phone(first, phoneNumber.substring(4), type, newFirst));
                                        customContact.getPhoneNumbers().setPhoneId(phoneId);
                                        List<CustomContact> customContacts = COUNT.get(first);
                                        if (customContacts == null) {
                                            customContacts = new ArrayList<>();
                                        }
                                        customContacts.add(customContact);
                                        COUNT.put(first, customContacts);
                                    }
                                }
                            }
                            phoneCursor.close();
                        }
                    }

                }
            }
            return null;
        }
    }

    class BackUpContact extends AsyncTask<Void, Void, Integer> {

        GroupContactActivity groupContactActivity;

        public BackUpContact(GroupContactActivity groupContactActivity) {
            this.groupContactActivity = groupContactActivity;
        }

        @Override
        protected Integer doInBackground(Void... voids) {

            FileOutputStream mFileOutputStream = null;
            try {
                mFileOutputStream = new FileOutputStream(storage_path, false);
                Cursor cursor = getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
                if (cursor != null && cursor.getCount() > 0) {
                    int i = 0;
                    while (cursor.moveToNext()) {
                        String lookupKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_VCARD_URI, lookupKey);
                        AssetFileDescriptor fd;
                        fd = groupContactActivity.getContentResolver().openAssetFileDescriptor(uri, "r");

                        FileInputStream fis = new FileInputStream(fd.getFileDescriptor());
                        int temp;
                        while ((temp = fis.read()) != -1) {
                            mFileOutputStream.write(temp);
                        }

                        fd.close();
                        fis.close();
                    }
                    cursor.close();
                    mFileOutputStream.close();
                } else {
                    mFileOutputStream.close();
                    if (cursor != null) {
                        cursor.close();
                    }
                    return -1;
                }
            } catch (IOException e) {
                return 0;
            }
            return 1;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            DialogUtils.showLoading(groupContactActivity, "Đang sao lưu dữ liệu...");
        }

        @Override
        protected void onPostExecute(Integer aVoid) {
            super.onPostExecute(aVoid);
            DialogUtils.hideLoading();
            if (aVoid == 0) {
                DialogUtils.showMessage(groupContactActivity, "Thông báo", "Sao lưu thất bại");
                saveIsBackUp(false);
            } else if (aVoid == -1) {
                DialogUtils.showMessage(groupContactActivity, "Thông báo", "Danh bạ rỗng");
                saveIsBackUp(false);
            } else {
                DialogUtils.showMessage(groupContactActivity, "Thông báo", "Sao lưu dữ liệu thành công vào bộ nhớ trong.");
                saveIsBackUp(true);
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }

    class RestoreContact extends AsyncTask<File, Void, Integer> {

        GroupContactActivity groupContactActivity;

        public RestoreContact(GroupContactActivity groupContactActivity) {
            this.groupContactActivity = groupContactActivity;
        }

        @Override
        protected Integer doInBackground(File... file) {
            try {
                Intent intent = new Intent();
                intent.setAction(android.content.Intent.ACTION_VIEW);
                Uri uri = FileProvider.getUriForFile(groupContactActivity,
                        BuildConfig.APPLICATION_ID + ".provider",
                        file[0]);
                intent.setDataAndType(uri, "text/x-vcard");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                // remove all contact
                removeAllContacts();
                groupContactActivity.startActivityForResult(intent, 200);

                return 1;
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            DialogUtils.showLoading(groupContactActivity, "Khôi phục dữ liệu...");

        }

        @Override
        protected void onPostExecute(Integer aVoid) {
            super.onPostExecute(aVoid);
            DialogUtils.hideLoading();
            if (aVoid == 0) {
                DialogUtils.showMessage(groupContactActivity, "Thông báo", "Khôi phục dữ liệu thất bại");
                return;
            } else {
                isLoad = true;
            }

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }
}
