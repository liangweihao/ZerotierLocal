package net.kaaass.zerotierfix.ui;

import androidx.fragment.app.Fragment;

import net.kaaass.zerotierfix.R;

public class PingActivity extends SingleFragmentActivity {

    @Override
    public Fragment createFragment() {
        return new PingFragment();
    }
}