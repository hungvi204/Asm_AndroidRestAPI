package com.example.asm2;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.asm2.adapter.Adapter_Item_District_Seclect_GHN;
import com.example.asm2.adapter.Adapter_Item_Province_Seclect_GHN;
import com.example.asm2.adapter.Adapter_Item_Ward_Seclect_GHN;
import com.example.asm2.model.District;
import com.example.asm2.model.DistrictRequest;
import com.example.asm2.model.Fruit;
import com.example.asm2.model.GHNItem;
import com.example.asm2.model.GHNOrderRespone;
import com.example.asm2.model.Order;
import com.example.asm2.model.Province;

import com.example.asm2.model.ResponseGHN;
import com.example.asm2.model.Ward;
import com.example.asm2.services.GHNOrderResquest;
import com.example.asm2.services.GHNRequest;
import com.example.asm2.services.HttpRequest_Fruit;
import com.example.asm2.services.HttpRequest_Order;

import java.util.ArrayList;

import com.example.asm2.model.Response;

import retrofit2.Call;
import retrofit2.Callback;


public class LocationActivity extends AppCompatActivity {
    GHNRequest ghnRequest;
    HttpRequest_Order httpRequest_order;
    Spinner sp_province, sp_district, sp_ward;
    EditText edt_location, edt_name, edt_phone;
    Button btn_next;
    //lưu dữ liệu chọn
    private String WardCode;
    private int DistrictID;
    private int ProvinceID;
    private Adapter_Item_Ward_Seclect_GHN adapter_item_ward_seclect_ghn;
    private Adapter_Item_Province_Seclect_GHN adapter_item_province_seclect_ghn;
    private Adapter_Item_District_Seclect_GHN adapter_item_district_seclect_ghn;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        ghnRequest = new GHNRequest();
        sp_province = findViewById(R.id.sp_province);
        sp_district = findViewById(R.id.sp_district);
        sp_ward = findViewById(R.id.sp_ward);
        edt_location = findViewById(R.id.edtLocation);
        edt_name = findViewById(R.id.edt_name);
        edt_phone = findViewById(R.id.edt_phone);
        btn_next = findViewById(R.id.btnNext);
        //gọi api lấy danh sách TP/Tỉnh đầu tiên
        ghnRequest.callAPI().getListProvince().enqueue(responseProvince);
        //lắng nghe sự kiện chọn
        sp_province.setOnItemSelectedListener(onItemSelectedListener);
        sp_district.setOnItemSelectedListener(onItemSelectedListener);
        sp_ward.setOnItemSelectedListener(onItemSelectedListener);
        sp_province.setSelection(0);

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (WardCode.equals("")) return;
                Fruit fruit = (Fruit) getIntent().getExtras().getSerializable("item");
                GHNItem ghnItem = new GHNItem();
                ghnItem.setName(fruit.getName());
                ghnItem.setPrice(Integer.parseInt(fruit.getPrice()));
                ghnItem.setCode(fruit.get_id());
                ghnItem.setQuantity(1);
                ghnItem.setWeight(50);
                ArrayList<GHNItem> items = new ArrayList<>();
                items.add(ghnItem);
                GHNOrderResquest ghnOrderResquest = new GHNOrderResquest(
                        edt_name.getText().toString(),
                        edt_phone.getText().toString(),
                        edt_location.getText().toString(),
                        WardCode,
                        DistrictID,
                        items
                );
                ghnRequest.callAPI().GHNOrder(ghnOrderResquest).enqueue(responseOrder);
                Toast.makeText(LocationActivity.this, "Đặt hàng thành công", Toast.LENGTH_SHORT).show();

            }
        });
    }

    AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            if (adapterView.getId() == R.id.sp_province) {
                ProvinceID = ((Province) adapterView.getAdapter().getItem(i)).getProvinceID();
                DistrictRequest districtRequest = new DistrictRequest(ProvinceID);
                ghnRequest.callAPI().getListDistrict(districtRequest).enqueue(responseDistrict);
            } else if (adapterView.getId() == R.id.sp_district) {
                DistrictID = ((District) adapterView.getAdapter().getItem(i)).getDistrictID();
                ghnRequest.callAPI().getListWard(DistrictID).enqueue(responseWard);
            } else if (adapterView.getId() == R.id.sp_ward) {
                WardCode = ((Ward) adapterView.getAdapter().getItem(i)).getWardCode();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };
    private void SetDataSpinProvince(ArrayList<Province> ds){
        adapter_item_province_seclect_ghn = new Adapter_Item_Province_Seclect_GHN(this, ds);
        sp_province.setAdapter(adapter_item_province_seclect_ghn);
        adapter_item_province_seclect_ghn.notifyDataSetChanged();
    }
    private void SetDataSpinDistrict(ArrayList<District> ds){
        adapter_item_district_seclect_ghn = new Adapter_Item_District_Seclect_GHN(this, ds);
        sp_district.setAdapter(adapter_item_district_seclect_ghn);
    }
    private void SetDataSpinWard(ArrayList<Ward> ds){
        adapter_item_ward_seclect_ghn = new Adapter_Item_Ward_Seclect_GHN(this, ds);
        sp_ward.setAdapter(adapter_item_ward_seclect_ghn);
    }
    Callback<ResponseGHN<ArrayList<Province>>> responseProvince = new Callback<ResponseGHN<ArrayList<Province>>>() {
        @Override
        public void onResponse(Call<ResponseGHN<ArrayList<Province>>> call, retrofit2.Response<ResponseGHN<ArrayList<Province>>> response) {

        }

        @Override
        public void onFailure(Call<ResponseGHN<ArrayList<Province>>> call, Throwable t) {

        }
    };
    Callback<ResponseGHN<ArrayList<District>>> responseDistrict = new Callback<ResponseGHN<ArrayList<District>>>() {
        @Override
        public void onResponse(Call<ResponseGHN<ArrayList<District>>> call, retrofit2.Response<ResponseGHN<ArrayList<District>>> response) {
            if (response.isSuccessful()) {
                if (response.body().getCode() == 200) {
                    ArrayList<District> ds = new ArrayList<>(response.body().getData());
                    SetDataSpinDistrict(ds);
                }
            }
        }

        @Override
        public void onFailure(Call<ResponseGHN<ArrayList<District>>> call, Throwable t) {

        }
    };
    Callback<ResponseGHN<ArrayList<Ward>>> responseWard = new Callback<ResponseGHN<ArrayList<Ward>>>() {
        @Override
        public void onResponse(Call<ResponseGHN<ArrayList<Ward>>> call, retrofit2.Response<ResponseGHN<ArrayList<Ward>>> response) {
            if (response.isSuccessful()) {
                if (response.body().getCode() == 200) {
                    ArrayList<Ward> ds = new ArrayList<>(response.body().getData());
                    SetDataSpinWard(ds);
                }
            }
        }

        @Override
        public void onFailure(Call<ResponseGHN<ArrayList<Ward>>> call, Throwable t) {

        }
    };

    Callback<ResponseGHN<GHNOrderRespone>> responseOrder = new Callback<ResponseGHN<GHNOrderRespone>>() {
        @Override
        public void onResponse(Call<ResponseGHN<GHNOrderRespone>> call, retrofit2.Response<ResponseGHN<GHNOrderRespone>> response) {
            if (response.isSuccessful()) {
                if (response.body().getCode() == 200) {
                    Toast.makeText(LocationActivity.this, "Đặt hàng thành công", Toast.LENGTH_SHORT).show();
                    Order order = new Order();
                    order.setOrder_code(response.body().getData().getOrder_code());
                    order.setId_user(getSharedPreferences("INFO", MODE_PRIVATE).getString("id", ""));
                    httpRequest_order.callAPI().order(order).enqueue(responseOrderDatabase);
                }
            }
        }

        @Override
        public void onFailure(Call<ResponseGHN<GHNOrderRespone>> call, Throwable t) {

        }
    };
    Callback<Response<Order>> responseOrderDatabase = new Callback<Response<Order>>() {
        @Override
        public void onResponse(Call<Response<Order>> call, retrofit2.Response<Response<Order>> response) {
            if (response.isSuccessful()) {
                if (response.body().getStatus() == 200) {
                    Toast.makeText(LocationActivity.this, "Cảm ơn đã mua hàng", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }

        @Override
        public void onFailure(Call<Response<Order>>  call, Throwable t) {

        }
    };
}