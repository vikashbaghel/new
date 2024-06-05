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

import com.app.rupyz.R;
import com.app.rupyz.ui.organization.EntitySignUpActivity;

public class AuthAccountFragment extends Fragment {
    private Button btn_next;
    private Context mContext;

    public AuthAccountFragment(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.auth_account_fragment, container, false);
        initLayout(view);
        return view;
    }

    private void initLayout(View view) {
        btn_next = view.findViewById(R.id.btn_next);
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((EntitySignUpActivity) mContext).updateViewPager(3);
            }
        });
    }
}
