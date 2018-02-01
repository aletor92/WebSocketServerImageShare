package bigtower.it.teacher.bigtower.it.teacher.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.koushikdutta.async.http.WebSocket;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import bigtower.it.teacher.MainActivity;
import bigtower.it.teacher.R;
import bigtower.it.teacher.utils.WebSocketUtil;

import static android.R.layout.simple_list_item_1;


public class ConnectionFragment extends Fragment {



    private OnFragmentInteractionListener mListener;

    public ConnectionFragment() {
    }

    public static ConnectionFragment newInstance() {
        ConnectionFragment fragment = new ConnectionFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_connection, container, false);
        TextView tv = (TextView) v.findViewById(R.id.ip);
        tv.setText(tv.getText() + getLocalIpAddress());
        ListView lv = (ListView) v.findViewById(R.id.listview_connection);
        List ipClients = new ArrayList<>();
        for (HashMap<String, WebSocket> map: WebSocketUtil.getInstance().getClients()) {
            for ( String key : map.keySet()) {
                ipClients.add(key);
            }
        }
        ArrayAdapter<String> itemsAdapter;
        itemsAdapter = new ArrayAdapter<String>(getActivity(), simple_list_item_1, ipClients);

        lv.setAdapter(itemsAdapter);
        return v;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public String getLocalIpAddress() {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':')<0;
                            if (isIPv4)
                                return sAddr;
                    }
                }
            }
        } catch (Exception ex) { } // for now eat exceptions
        return null;
    }

    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }
}
