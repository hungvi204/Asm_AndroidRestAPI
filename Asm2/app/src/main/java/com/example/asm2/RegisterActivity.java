package com.example.asm2;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.asm2.model.Response;
import com.example.asm2.model.Users;
import com.example.asm2.services.HttpRequest_User;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

public class RegisterActivity extends AppCompatActivity {
    EditText edtUsername, edtPassword, edtEmail, edtName;
    Button btnRegister, btnLogin;

    private HttpRequest_User httpRequest;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        edtEmail = findViewById(R.id.edtEmail);
        edtName = findViewById(R.id.edtName);
        btnRegister = findViewById(R.id.btnRegister);
        btnLogin = findViewById(R.id.btnLogin);
        httpRequest = new HttpRequest_User();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(RegisterActivity.this, "click", Toast.LENGTH_SHORT).show();
                // Sử dụng RequestBody
                // sử dụng RequestBody
                RequestBody _username = RequestBody.create(MediaType.parse("multipart/form-data"), edtUsername.getText().toString().trim());
                RequestBody _password = RequestBody.create(MediaType.parse("multipart/form-data"), edtPassword.getText().toString().trim());
                RequestBody _email = RequestBody.create(MediaType.parse("multipart/form-data"), edtEmail.getText().toString().trim());
                RequestBody _name = RequestBody.create(MediaType.parse("multipart/form-data"), edtName.getText().toString().trim());
                MultipartBody.Part avatarPart = null;
                // Gọi API đăng ký
                httpRequest.callAPI().register(_username, _password, _email, _name, avatarPart).enqueue(responseUser);
            }
        });
    }
    // Callback xử lý phản hồi từ server
    Callback<Response<Users>> responseUser = new Callback<Response<Users>>() {
        @Override
        public void onResponse(Call<Response<Users>> call, retrofit2.Response<Response<Users>> response) {
            if (response.isSuccessful()) {
                // Kiểm tra status code
                if (response.body().getStatus() == 200) {
                    Toast.makeText(RegisterActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                }
            }
        }

        @Override
        public void onFailure(Call<Response<Users>> call, Throwable t) {
            Log.d(">>> error", "onFailure" + t.getMessage());
        }
    };
}