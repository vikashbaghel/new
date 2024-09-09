package com.app.rupyz.adapter.individual;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.rupyz.databinding.MyBankListInsideItemBinding;
import com.app.rupyz.generic.model.individual.experian.Tradeline;
import com.app.rupyz.ui.user.ProfileActivity;
import com.google.android.gms.common.util.Strings;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MyBankListAdapter extends RecyclerView.Adapter<MyBankListAdapter.ViewHolder> {
    private List<Tradeline> listdata;
    private MyBankListInsideItemBinding binding;
    private Context mContext;

    // RecyclerView recyclerView;
    public MyBankListAdapter(List<Tradeline> listdata, Context mContext) {
        this.listdata = listdata;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        binding = MyBankListInsideItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        binding.txtBankName.setText(listdata.get(position).getSubscriber_Name());
        /*try {
            JSONObject jsonObject = new JSONObject(listdata.get(position).getcAIS_Holder_Phone_Details().toString());
            if(!isNullEmpty(jsonObject.getString("Telephone_Number"))){
                binding.rlTelephone.setVisibility(View.VISIBLE);
                binding.txtTelephone.setText(jsonObject.getString("Telephone_Number"));
            }
            else {
                binding.txtTelephone.setText("");
                binding.rlTelephone.setVisibility(View.GONE);
            }

            if(!isNullEmpty(jsonObject.getString("EMailId"))){
                binding.rlEmail.setVisibility(View.VISIBLE);
                binding.txtContactEmail.setText(jsonObject.getString("EMailId"));
            }

            else {
                binding.txtContactEmail.setText("");
                binding.rlEmail.setVisibility(View.GONE);
            }

            if(!isNullEmpty(jsonObject.getString("Mobile_Telephone_Number"))){
                binding.rlPhone.setVisibility(View.VISIBLE);
                binding.txtContactNumber.setText(jsonObject.getString("Mobile_Telephone_Number"));
            }
            else {
                binding.txtContactNumber.setText("");
                binding.rlPhone.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }*/

        MyContactListAdapter adapter = new MyContactListAdapter(listdata.get(position).getcAIS_Holder_Phone_Details(), mContext);
        //MyAddressListAdapter addressListAdapter = new MyAddressListAdapter(mData.getTradelines(), ProfileActivity.this);

        binding.recyclerviewContactInfo.setHasFixedSize(true);
        binding.recyclerviewContactInfo.setLayoutManager(new LinearLayoutManager(mContext));
        binding.recyclerviewContactInfo.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        MyBankListInsideItemBinding binding;

        public ViewHolder(MyBankListInsideItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public static boolean isNullEmpty(String str) {

        // check if string is null
        if (str == null) {
            return true;
        }

        else if (str.equals("null")) {
            return true;
        }

        // check if string is empty
        else if(str.isEmpty()){
            return true;
        }

        else {
            return false;
        }
    }
}