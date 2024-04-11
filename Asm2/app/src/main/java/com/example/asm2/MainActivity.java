package com.example.asm2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.example.asm2.fragment.GioHangFragment;
import com.example.asm2.fragment.HoSoFragment;
import com.example.asm2.fragment.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom);
        bottomNavigationView.setOnItemSelectedListener(item -> {//sự kiện hiển thị khi mình vô cái nút đó
            displaySelectedFragment(item.getItemId());
            return true;
        });

        // Hiển thị fragment home khi khởi động activity
        displaySelectedFragment(R.id.navigation_home);
    }
    private void displaySelectedFragment(int itemId) {
        Fragment fragment;
        if (itemId == R.id.navigation_home) {
            fragment = new HomeFragment();
        } else if (itemId == R.id.navigation_gioHang) {
            fragment = new GioHangFragment();
        } else {
            fragment = new HoSoFragment();
        }
        //để hiện thị qua từng phần
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view_tag, fragment).commit();
    }
}