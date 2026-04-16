package net.kaaass.zerotierfix.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.kaaass.zerotierfix.R;

import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

public class NetworkInterfaceFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;
    private List<InterfaceInfo> interfaceList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_network_interface, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadNetworkInterfaces();

        adapter = new RecyclerViewAdapter(interfaceList);
        recyclerView.setAdapter(adapter);

        return view;
    }

    private void loadNetworkInterfaces() {
        interfaceList.clear();
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                if (iface.isLoopback()) {
                    continue;
                }

                InterfaceInfo info = new InterfaceInfo();
                info.name = iface.getName();
                info.displayName = iface.getDisplayName();

                StringBuilder addresses = new StringBuilder();
                Enumeration<java.net.InetAddress> inetAddresses = iface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    java.net.InetAddress addr = inetAddresses.nextElement();
                    if (addresses.length() > 0) {
                        addresses.append("\n");
                    }
                    addresses.append(addr.getHostAddress());
                }
                info.addresses = addresses.toString();

                byte[] hardwareAddress = iface.getHardwareAddress();
                if (hardwareAddress != null) {
                    StringBuilder mac = new StringBuilder();
                    for (int i = 0; i < hardwareAddress.length; i++) {
                        if (i > 0) {
                            mac.append(":");
                        }
                        mac.append(String.format("%02X", hardwareAddress[i]));
                    }
                    info.macAddress = mac.toString();
                } else {
                    info.macAddress = "N/A";
                }

                interfaceList.add(info);
            }
            Collections.sort(interfaceList, (a, b) -> a.name.compareToIgnoreCase(b.name));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class InterfaceInfo {
        String name;
        String displayName;
        String addresses;
        String macAddress;
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

        private final List<InterfaceInfo> mValues;

        public RecyclerViewAdapter(List<InterfaceInfo> items) {
            mValues = items;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_network_interface, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            InterfaceInfo info = mValues.get(position);
            holder.mName.setText(info.name);
            holder.mAddress.setText(info.addresses);
            holder.mMac.setText(info.macAddress);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final TextView mName;
            public final TextView mAddress;
            public final TextView mMac;

            public ViewHolder(View view) {
                super(view);
                mName = view.findViewById(R.id.iface_name);
                mAddress = view.findViewById(R.id.iface_address);
                mMac = view.findViewById(R.id.iface_mac);
            }
        }
    }
}