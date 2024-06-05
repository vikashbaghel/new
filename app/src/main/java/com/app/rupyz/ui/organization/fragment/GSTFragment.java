package com.app.rupyz.ui.organization.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.rupyz.adapter.organization.GstListAdapter;
import com.app.rupyz.databinding.GstFragmentBinding;
import com.app.rupyz.generic.logger.Logger;
import com.app.rupyz.generic.model.organization.GstViewModel;
import com.app.rupyz.ui.organization.EntitySignUpActivity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class GSTFragment extends Fragment {
    private Button btn_next;
    private Context mContext;
    private GstViewModel mData;
    private GstFragmentBinding binding;

    public GSTFragment(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = GstFragmentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        initLayout();
        initData();
        return view;
    }

    private void initData() {
        try {
            EntitySignUpActivity activity = (EntitySignUpActivity) getActivity();
            String getData = activity.sendData();
            Logger.errorLogger(this.getClass().getName(), getData);
            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObj = (JsonObject) jsonParser.parse(getData);
            Gson gson = new Gson();
            mData = gson.fromJson(jsonObj.get("data"), GstViewModel.class);
//            binding.recyclerView.setAdapter(new GstListAdapter(getContext(), mData.getGstin_list()));
        } catch (Exception ex) {

        }
    }

    private void initLayout() {
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 1);
        binding.recyclerView.setLayoutManager(mLayoutManager);
    }
}
