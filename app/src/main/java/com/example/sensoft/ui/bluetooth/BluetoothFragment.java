package com.example.sensoft.ui.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.example.sensoft.MainActivity;
import com.example.sensoft.R;

import java.util.ArrayList;
import java.util.Set;

public class BluetoothFragment extends Fragment {

    int LAUNCH_SECOND_ACTIVITY = 0;
    ListView device_list;
    private Set<BluetoothDevice> pairedDevices;
    BluetoothAdapter bluetoothAdapter;
    private BluetoothViewModel bluetoothViewModel;
    public static String EXTRA_ADDRESS = "EXTRA_ADDRESS";
    ArrayAdapter<String> adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothViewModel =
                ViewModelProviders.of(this).get(BluetoothViewModel.class);
        View root = inflater.inflate(R.layout.fragment_bluetooth, container, false);
        device_list = (ListView) root.findViewById(R.id.device_list);
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        BluetoothControllerAndConnection();
    }

    private void ListDevices() {
        pairedDevices = bluetoothAdapter.getBondedDevices();
        ArrayList list = new ArrayList();

        if (pairedDevices.size() > 0) {
            for (BluetoothDevice bt : pairedDevices) {
                list.add(bt.getName() + "\n" + bt.getAddress());
            }
        } else {
            Toast.makeText(getContext(), "Eşleşen Cihaz Bulunmamaktadır.", Toast.LENGTH_SHORT).show();
        }
        final ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, list);
        device_list.setAdapter(adapter);
        device_list.setOnItemClickListener(selectDevice);
    }

    private void BluetoothControllerAndConnection() {
        if (bluetoothAdapter == null) {
            Toast.makeText(getContext(), "Cihazınızda bluetooth desteği bulunmamaktadır.", Toast.LENGTH_SHORT).show();
        }
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBluetoothIntent = new Intent(bluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetoothIntent, LAUNCH_SECOND_ACTIVITY);
        } else {
            ListDevices();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LAUNCH_SECOND_ACTIVITY) {
            if (resultCode == MainActivity.RESULT_OK) {
                ListDevices();
            }
            if (resultCode == MainActivity.RESULT_CANCELED) {
                Toast.makeText(getContext(), "Bluetooth Bağlantı İsteği Reddedildi.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public AdapterView.OnItemClickListener selectDevice = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            String info = ((TextView) view).getText().toString();
            String address = info.substring(info.length() - 17);
            Bundle bundle=new Bundle();
            bundle.putString(EXTRA_ADDRESS,address);
            Navigation.findNavController(view).navigate(R.id.action_navigation_bluetooth_to_navigation_home,bundle);
        }
    };
}
