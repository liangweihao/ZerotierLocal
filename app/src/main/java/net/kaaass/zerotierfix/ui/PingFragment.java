package net.kaaass.zerotierfix.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import net.kaaass.zerotierfix.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class PingFragment extends Fragment {

    private EditText etIpAddress;
    private Button btnPing;
    private TextView tvResult;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ping, container, false);

        etIpAddress = view.findViewById(R.id.et_ip_address);
        btnPing = view.findViewById(R.id.btn_ping);
        tvResult = view.findViewById(R.id.tv_result);

        btnPing.setOnClickListener(v -> {
            String ip = etIpAddress.getText().toString().trim();
            if (!ip.isEmpty()) {
                new PingTask().execute(ip);
            }
        });

        return view;
    }

    private class PingTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            btnPing.setEnabled(false);
            tvResult.setText(R.string.ping);
        }

        @Override
        protected String doInBackground(String... params) {
            String ip = params[0];
            StringBuilder result = new StringBuilder();
            try {
                Process process = Runtime.getRuntime().exec("ping -c 4 " + ip);
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line).append("\n");
                }
                reader.close();
                process.waitFor();
            } catch (Exception e) {
                result.append("Error: ").append(e.getMessage());
            }
            return result.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            btnPing.setEnabled(true);
            tvResult.setText(result);
        }
    }
}