package com.example.sensoft.ui.home;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import butterknife.ButterKnife;

import com.example.sensoft.R;
import com.example.sensoft.SpeechAPI.SpeechAPI;
import com.example.sensoft.SpeechAPI.Titresim;
import com.example.sensoft.SpeechAPI.VoiceRecorder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HomeFragment extends Fragment{

    private HomeViewModel homeViewModel;
    private static final String DEVICE_ADDRESS_CODE = "EXTRA_ADDRESS";
    private static String EXTRA_ADDRESS;
    String address = null;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket =null;
    BluetoothDevice remoteDevice;
    BluetoothServerSocket mmServer;
    private boolean isBtConnected = false;
    static  final UUID myUUID= UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ArrayList uyarikelimeleri  = new ArrayList();
    private static final int RECORD_Request_Code=101;
    private SpeechAPI speechAPI;
    private VoiceRecorder voiceRecorder;

    private final VoiceRecorder.Callback callback = new VoiceRecorder.Callback() {
        @Override
        public void onVoiceStart() {
            if (speechAPI != null )
            {
                speechAPI.startRecognizing( voiceRecorder.getSampleRate() );
            }
        }

        @Override
        public void onVoice(byte[] data, int size) {
            if (speechAPI != null )
            {
                speechAPI.recognize( data,size);
            }
        }

        @Override
        public void onVoiceEnd() {
            if (speechAPI != null )
            {
                speechAPI.finishRecognizing();
            }
        }
    };



    public HomeFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            EXTRA_ADDRESS = getArguments().getString(DEVICE_ADDRESS_CODE);
            if(EXTRA_ADDRESS != null){
                address = EXTRA_ADDRESS;
                new BTbaglan().execute();
            }
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        speechAPI = new SpeechAPI(getActivity());
        ButterKnife.bind( getActivity() );

        //uyarikelimeleri.add( 0,"boş" );
        uyarikelimeleri.add( 0,"imdat" );
        //uyarikelimeleri.add( 2,"yardım edin" );
        //uyarikelimeleri.add( 3,"merhaba" );
        //uyarikelimeleri.add( 4,"isim" );
        //uyarikelimeleri.add( 5,"bakar mısın" );
        //uyarikelimeleri.add( 6,"acil" );
        //uyarikelimeleri.add( 7,"hey" );
        //uyarikelimeleri.add( 8,"selam" );
        //uyarikelimeleri.add( 9,"gelir misin" );
        //uyarikelimeleri.add( 10,"gider misin" );
        uyarikelimeleri.add( 1,"İmdat" );
        //uyarikelimeleri.add( 12,"Selam" );
        //uyarikelimeleri.add( 13,"Merhaba " );
        //uyarikelimeleri.add( 14,"dikkat" );
        return root;

    }

    private void Disconnect(){
        if(btSocket!=null){
            try {
                btSocket.close();
            } catch (IOException e){
                // msg("Error");
            }
        }
        //getActivity().finish();
    }

    private class BTbaglan extends AsyncTask<Void, Void, Void> {
        private boolean ConnectSuccess = true;

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(getContext(), "Baglanıyor...", "Lütfen Bekleyin");
        }

        @Override
        protected Void doInBackground(Void... devices) {
            try {
                if (btSocket == null || !isBtConnected) {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice cihaz = myBluetooth.getRemoteDevice(address);
                    btSocket = cihaz.createInsecureRfcommSocketToServiceRecord(myUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();
                }
            } catch (IOException e) {
                ConnectSuccess = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (!ConnectSuccess) {
                // msg("Baglantı Hatası, Lütfen Tekrar Deneyin");
                Toast.makeText(getContext(),"Bağlantı Hatası Tekrar Deneyin",Toast.LENGTH_SHORT).show();
                //getActivity().finish();
            } else {
                //   msg("Baglantı Basarılı");
                Toast.makeText(getContext(),"Bağlantı Başarılı",Toast.LENGTH_SHORT).show();
                isBtConnected = true;
            }
            progress.dismiss();
        }

    }

    private int GrantedPermission(String permission){
        return ContextCompat.checkSelfPermission( getContext(),permission );
    }

    private void makeRequest(String permission)
    {
        ActivityCompat.requestPermissions( getActivity(),new String[]{permission},RECORD_Request_Code );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == RECORD_Request_Code)
        {
            if (grantResults.length == 0 && grantResults[0] == PackageManager.PERMISSION_DENIED)
            {
                getActivity().finish();
            }
            else {
                startVoiceRecorder();
            }
        }
    }

    private final SpeechAPI.Listener listener = new SpeechAPI.Listener() {
        @Override
        public void onSpeechRecognized(final String text_dinlenen_ses, final boolean isFinal) {
            if(isFinal)
            {
                voiceRecorder.dismiss();
            }
            if(!TextUtils.isEmpty( text_dinlenen_ses ))
            {
                getActivity().runOnUiThread( new Runnable() {
                    @Override
                    public void run() {
                        if(isFinal)
                        {
                            /*textView.setText( null );
                            list.add( 0,text_dinlenen_ses );
                            adapter.notifyDataSetChanged();
                            listView.smoothScrollToPosition( 0 );
                            textView.setVisibility( View.GONE );*/
                        }
                        else {
                            //textView.setText( text_dinlenen_ses );
                            //textView.setVisibility( View.VISIBLE );
                            String uyari_ses = text_dinlenen_ses;
                            for (int i =0;i<uyarikelimeleri.size() ;i++){
                                if (uyari_ses.contains( (CharSequence) uyarikelimeleri.get( i ) )){
                                    veri_gonder(33);
                                    Titresim titret = new Titresim();
                                    titret.titrestir( getContext() );
                                    NotificationCompat.Builder notificationBuilder= new NotificationCompat.Builder(getContext());
                                    notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
                                    notificationBuilder.setContentTitle( (CharSequence) uyarikelimeleri.get( i ) );
                                    notificationBuilder.setContentText("SenSoft Uyarıyor");
                                    notificationBuilder.setTicker("Etrafınızda İmdat Diye Bağıran Biri Var. Lütfen Etrafına Bak");
                                    NotificationManager mNotificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE );
                                    mNotificationManager.notify( 001,notificationBuilder.build() );
                                }
                            }

                        }

                    }
                } );
            }
        }
    };

    private void startVoiceRecorder()
    {
        if (voiceRecorder != null)
        {
            voiceRecorder.stop();
        }
        voiceRecorder = new VoiceRecorder( callback );
        voiceRecorder.start();

    }

    private void stopVoiceRecorder()
    {
        if (voiceRecorder != null)
        {
            voiceRecorder.stop();
            voiceRecorder = null;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
            if (GrantedPermission( Manifest.permission.RECORD_AUDIO ) == PackageManager.PERMISSION_GRANTED)
            {
                startVoiceRecorder();
            }
            else {
                makeRequest( Manifest.permission.RECORD_AUDIO );
            }
            speechAPI.addListener( listener );
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        stopVoiceRecorder();
        speechAPI.removeListener( listener );
        speechAPI.destroy();
        speechAPI=null;
        super.onStop();
    }

    private void veri_gonder(int notificaion_id)
    {
        if(btSocket != null)
        {
            try
            {
                btSocket.getOutputStream().write(notificaion_id);
            }
            catch (IOException e)
            {


            }
        }
    }
}
