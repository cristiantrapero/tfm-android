package com.cristiantrapero.tfm.operation;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.cristiantrapero.tfm.R;
import com.clj.fastble.BleManager;
import com.clj.fastble.data.BleDevice;

import java.util.ArrayList;
import java.util.List;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class ChatListFragment extends Fragment {

    private ResultAdapter mResultAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chat_list, null);
        initView(v);
        showData();
        return v;
    }

    private void initView(View v) {
        mResultAdapter = new ResultAdapter(getActivity());
        ListView list_chats = (ListView) v.findViewById(R.id.list_chats);
        list_chats.setAdapter(mResultAdapter);

        list_chats.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BluetoothGattService service = mResultAdapter.getItem(position);
                ((OperationActivity) getActivity()).setBluetoothGattService(service);
                ((OperationActivity) getActivity()).changePage(1);
            }
        });
    }

    private void showData() {
        BleDevice bleDevice = ((OperationActivity) getActivity()).getBleDevice();
        BluetoothGatt gatt = BleManager.getInstance().getBluetoothGatt(bleDevice);

        mResultAdapter.clear();
        for (BluetoothGattService service : gatt.getServices()) {
            mResultAdapter.addResult(service);
        }
        mResultAdapter.notifyDataSetChanged();
    }

    private class ResultAdapter extends BaseAdapter {

        private Context context;
        private final List<BluetoothGattService> bluetoothGattServices;

        ResultAdapter(Context context) {
            this.context = context;
            bluetoothGattServices = new ArrayList<>();
        }

        void addResult(BluetoothGattService service) {
            bluetoothGattServices.add(service);
        }

        void clear() {
            bluetoothGattServices.clear();
        }

        @Override
        public int getCount() {
            return bluetoothGattServices.size();
        }

        @Override
        public BluetoothGattService getItem(int position) {
            if (position > bluetoothGattServices.size())
                return null;
            return bluetoothGattServices.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView != null) {
                holder = (ViewHolder) convertView.getTag();
            } else {
                convertView = View.inflate(context, R.layout.adapter_chat, null);
                holder = new ViewHolder();
                convertView.setTag(holder);
                holder.txt_username = (TextView) convertView.findViewById(R.id.txt_username);
                holder.txt_uuid = (TextView) convertView.findViewById(R.id.txt_uuid);
            }

            BluetoothGattService service = bluetoothGattServices.get(position);
            String uuid = service.getUuid().toString();

            holder.txt_username.setText(String.valueOf(getActivity().getString(R.string.service) + "(" + position + ")"));
            holder.txt_uuid.setText(uuid);
            return convertView;
        }

        class ViewHolder {
            TextView txt_username;
            TextView txt_uuid;
        }
    }

}
