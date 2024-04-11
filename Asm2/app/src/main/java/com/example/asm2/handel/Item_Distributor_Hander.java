package com.example.asm2.handel;


import com.example.asm2.model.Distributor;

public interface Item_Distributor_Hander { //Tạo xử lý khi click vào item trong adapter
    public void Delete(String id);
    public void Update(String id, Distributor distributor);
}
