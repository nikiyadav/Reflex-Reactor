package rr.reflexreactor;


import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class DeviceListFragment extends ListFragment implements WifiP2pManager.PeerListListener{
    private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    ProgressDialog progressDialog = null;
    View mContentView = null;
    private WifiP2pDevice device;

    public DeviceListFragment(){

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.setListAdapter(new WiFiPeerListAdapter(getActivity(), R.layout.row_devices, peers));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_device_list,container,false);//nll
        return mContentView;
        /*View v=super.onCreateView(inflater,container,savedInstanceState);
        ViewGroup parent=(ViewGroup) inflater.inflate(R.layout.fragment_device_list,container,false);
        parent.addView(v,0);
        return parent;
        */
    }

    public WifiP2pDevice getDevice() {
        return device;
    }

    private static String getDeviceStatus(int deviceStatus) {
        switch (deviceStatus) {
            case WifiP2pDevice.AVAILABLE:
                return "Available";
            case WifiP2pDevice.INVITED:
                return "Invited";
            case WifiP2pDevice.CONNECTED:
                return "Connected";
            case WifiP2pDevice.FAILED:
                return "Failed";
            case WifiP2pDevice.UNAVAILABLE:
                return "Unavailable";
            default:
                return "Unknown";
        }
    }

    public void changeGOtext(){
        TextView view = (TextView) mContentView.findViewById(R.id.GO);
        view.setText("This Device is now Group Owner");
    }

    public void revertGOtext() {
        TextView view = (TextView) mContentView.findViewById(R.id.GO);
        view.setText("This Device");
    }

    /**
     * Array adapter for ListFragment that maintains WifiP2pDevice list.
     */
    private class WiFiPeerListAdapter extends ArrayAdapter<WifiP2pDevice> {

        private List<WifiP2pDevice> items;
        public WiFiPeerListAdapter(Context context, int textViewResourceId, List<WifiP2pDevice> objects) {
            super(context, textViewResourceId, objects);
            items = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.row_devices, null);
            }
            WifiP2pDevice device = items.get(position);
            if (device != null) {
                TextView top = (TextView) v.findViewById(R.id.device_name);
                TextView bottom = (TextView) v.findViewById(R.id.device_details);
                if (top != null) {
                    top.setText(device.deviceName);
                }
                if (bottom != null) {
                    bottom.setText(getDeviceStatus(device.status));
                }
            }
            return v;

        }
    }

    /**
     * Initiate a connection with the peer.
     */
//    @Override
//    public void onListItemClick(ListView l, View v, int position, long id) {
//        WifiP2pDevice device = (WifiP2pDevice) getListAdapter().getItem(position);
//        ((DeviceActionListener) getActivity()).showDetails(device);
//    }
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        int sz=l.getCount();
        for(int i=0;i<sz;i++){
            View jmd=l.getChildAt(i);
            if(jmd!=null){
                jmd.setBackgroundColor(Color.WHITE);
            }
        }
        WifiP2pDevice device = (WifiP2pDevice) getListAdapter().getItem(position);
        if(getListAdapter().isEnabled(position)){
            v.setBackgroundColor(Color.parseColor("#d32f2f"));
        }
        ((DeviceActionListener) getActivity()).showDetails(device);
    }

    public void updateThisDevice(WifiP2pDevice device) {
        this.device = device;
        TextView view = (TextView) mContentView.findViewById(R.id.my_name);
        view.setText(device.deviceName);
        view = (TextView) mContentView.findViewById(R.id.my_status);
        view.setText(getDeviceStatus(device.status));
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peerList) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        peers.clear();
        peers.addAll(peerList.getDeviceList());
        ((WiFiPeerListAdapter) getListAdapter()).notifyDataSetChanged();
        if (peers.size() == 0) {
            return;
        }
    }

    public void clearPeers() {
        peers.clear();
        ((WiFiPeerListAdapter) getListAdapter()).notifyDataSetChanged();
    }

    public void onInitiateDiscovery() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        ((DeviceListFragment.DeviceActionListener)getActivity()).disconnect();
        SharedPreferencesHandler.setStringValues(getActivity(), "count", "0");
        SharedPreferencesHandler.setStringValues(getActivity(), "A","");
        SharedPreferencesHandler.setStringValues(getActivity(),"B","");
        SharedPreferencesHandler.setStringValues(getActivity(), "C", "");
        SharedPreferencesHandler.setStringValues(getActivity(),"D","");
        SharedPreferencesHandler.setStringValues(getActivity(),"E","");
        SharedPreferencesHandler.setStringValues(getActivity(),"F","");
        progressDialog = ProgressDialog.show(getActivity(), "Press back to cancel", "finding peers", true, true, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

            }
        });
    }

    /**
     * An interface-callback for the activity to listen to fragment interaction
     * events.
     */
    public interface DeviceActionListener {
        void showDetails(WifiP2pDevice device);
        void cancelDisconnect();
        void connect(WifiP2pConfig config);
        void disconnect();
    }
}
