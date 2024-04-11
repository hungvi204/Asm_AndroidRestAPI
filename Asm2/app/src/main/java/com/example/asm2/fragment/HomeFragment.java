package com.example.asm2.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asm2.R;
import com.example.asm2.SpacesItemDecoration;
import com.example.asm2.adapter.Recycle_Item_Distributors;
import com.example.asm2.adapter.Recycle_Item_Fruits;
import com.example.asm2.handel.Item_Distributor_Hander;
import com.example.asm2.model.Distributor;
import com.example.asm2.model.Fruit;
import com.example.asm2.model.Page;
import com.example.asm2.model.Response;
import com.example.asm2.services.HttpRequest;
import com.example.asm2.services.HttpRequest_Fruit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

public class HomeFragment extends Fragment {
    private HttpRequest httpRequest;
    private HttpRequest_Fruit httpRequestFruit;
    private RecyclerView recycle_distributor, recycle_fruit;
    private Recycle_Item_Distributors adapter;
    private Recycle_Item_Fruits adapterFruit;
    private SharedPreferences sharedPreferences;
    private String token;
    private ProgressBar loadmore;
    private ArrayList<Fruit> ds = new ArrayList<>();
    private int page = 1;
    private int totalPage = 0;
    private NestedScrollView nestedScrollView;
    private String sort;
    EditText edtSearch;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recycle_distributor = view.findViewById(R.id.ryc_distributor);
        recycle_fruit = view.findViewById(R.id.rcy_fruit);
        loadmore = view.findViewById(R.id.loadmore);
        nestedScrollView = view.findViewById(R.id.nestScrollView);
        edtSearch = view.findViewById(R.id.edtSearch);
        sharedPreferences = getActivity().getSharedPreferences("INFO", MODE_PRIVATE);
        //Lấy token từ sharedPreferences
        token = sharedPreferences.getString("token", "");
        httpRequestFruit = new HttpRequest_Fruit(token);
        onResume();
        // Khởi tạo services request
        requestDistributor();

        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    //Lấy từ khóa từ ô tìm kiếm
                    String key = edtSearch.getText().toString();

                    httpRequest.callAPI()
                            .searchDistributor(key)
                            .enqueue(getDistributorAPI);

                    return true;
                }
                return false;
            }
        });
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(@NonNull NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    if (totalPage == page) return;
                    if (loadmore.getVisibility() == View.GONE) {
                        loadmore.setVisibility(View.VISIBLE);
                        page ++;//tăng page
                        //Call API
                        onResume();
                    }
                }
            }
        });
        return view;
    }

    private void requestDistributor(){
        httpRequest = new HttpRequest();
        httpRequest.callAPI()
                .getListDistributor() // Phương thức api cần thực thi
                .enqueue(getDistributorAPI); // Xử lý bất đồng bộ
    }

    private void getData(ArrayList<Distributor> ds) {
        adapter = new Recycle_Item_Distributors(getContext(), ds);
        recycle_distributor.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recycle_distributor.setAdapter(adapter);
    }

    @Override
    public void onResume(){
        super.onResume();
        //filter mặc định
        Map<String, String> map = getMapFilter(page, "", "0", "-1");
        httpRequestFruit.callAPI().getPageFruit(map).enqueue(getListFruitRespone);
    }

    Callback<Response<ArrayList<Distributor>>> getDistributorAPI = new Callback<Response<ArrayList<Distributor>>>() {
        @Override
        public void onResponse(Call<Response<ArrayList<Distributor>>> call, retrofit2.Response<Response<ArrayList<Distributor>>> response) {
            if (response.isSuccessful()) {
                if (response.body().getStatus() == 200) {
                    ArrayList<Distributor> ds = response.body().getData();
                    getData(ds);
//                    Toast.makeText(MainActivity.this, response.body().getMessenge(), Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        public void onFailure(Call<Response<ArrayList<Distributor>>> call, Throwable t) {
//            Log.d("GetLisDistributor", "onFaile", + t.getMessage());
        }
    };

    Callback<Response<Distributor>> responseDistributorAPI = new Callback<Response<Distributor>>() {
        @Override
        public void onResponse(Call<Response<Distributor>> call, retrofit2.Response<Response<Distributor>> response) {
            if (response.isSuccessful()) {
                if (response.body().getStatus() == 200) {
                    httpRequest.callAPI()
                            .getListDistributor()
                            .enqueue(getDistributorAPI);
//                    Toast.makeText(HomeFragment.this, ""+response.body().getMessenge(), Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        public void onFailure(Call<Response<Distributor>> call, Throwable t) {
//            Log.d("GetLisDistributor", "onFaile", + t.getMessage());
        }
    };
    Callback<Response<Page<ArrayList<Fruit>>>> getListFruitRespone = new Callback<Response<Page<ArrayList<Fruit>>>>() {
        @Override
        public void onResponse(Call<Response<Page<ArrayList<Fruit>>>> call, retrofit2.Response<Response<Page<ArrayList<Fruit>>>> response) {
            if (response.isSuccessful()) {
                if (response.body().getStatus() == 200) {
                    //set totalPage
                    totalPage = response.body().getData().getTotalPage();
                    //Lay data
                    ArrayList<Fruit> _ds = response.body().getData().getData();
                    getDataFruit(_ds);
                }
            }
        }

        @Override
        public void onFailure(Call<Response<Page<ArrayList<Fruit>>>> call, Throwable t) {
            Log.e("FruitActivity", "Error fetching fruit data: " + t.getMessage());
            Toast.makeText(getContext(), "Failed to fetch data", Toast.LENGTH_SHORT).show();
        }
    };

    private void getDataFruit(ArrayList<Fruit> _ds){
        //Kiểm tra nếu process load more chạy thì chỉ cần add thêm fruit vào list
        if (loadmore.getVisibility() == View.VISIBLE) {
            //Do chạy ở local nên tốc độ mạng tốt nên sẽ thêm đoạn code delay
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    adapterFruit.notifyItemInserted(ds.size() -1);
                    loadmore.setVisibility(View.GONE);
                    ds.addAll(_ds);
                    //thông báo adapter dữ liệu thay đổi
                    adapter.notifyDataSetChanged();
                }
            }, 4000);
            return;
        }
        ds.addAll(_ds);
        adapterFruit = new Recycle_Item_Fruits(getContext(), ds);
        recycle_fruit.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recycle_fruit.setAdapter(adapterFruit);
        // Thiết lập khoảng cách giữa các item trong RecyclerView
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing); // Định nghĩa khoảng cách trong file dimens.xml
        recycle_fruit.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
    }
    //Hàm call api theo filter
    private void FilterFruit(){
//        String _name = edtSearch.getText().toString().equals("") ? "" : edtSearch.getText().toString();
//        String _price = edtSearchPrice.getText().toString().equals("") ? "0" : edtSeachPrice.getText().toString();
//        String _sort = (sort != null && sort.equals("")) ? "-1" : sort;
//
//        Map<String, String> map = getMapFilter(page, _name, _price, _sort   );
//        httpRequestFruit.callAPI().getPageFruit(map).enqueue(getListFruitRespone);
    }

    //hàm setup MapQuery
    private Map<String, String> getMapFilter(int _page, String _name, String _price, String _sort){
        Map<String, String> map = new HashMap<>();
        map.put("page", String.valueOf(_page));
        map.put("name", _name);
        map.put("price", _price);
        map.put("sort", _sort);
        return map;
    }
}