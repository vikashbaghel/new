package com.app.rupyz.ui.network.views;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.app.rupyz.R;
import com.app.rupyz.adapter.network.NetworkOrgAdapter;
import com.app.rupyz.databinding.FragmentNetworkBinding;
import com.app.rupyz.generic.helper.PaginationScrollListener;
import com.app.rupyz.generic.utils.AppConstant;
import com.app.rupyz.generic.utils.Utility;
import com.app.rupyz.model_kt.NetWorkConnectModel;
import com.app.rupyz.model_kt.NetworkDataItem;
import com.app.rupyz.ui.connections.ConnectionsActivity;
import com.app.rupyz.ui.organization.profile.OrgProfileActivity;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class NetworkFragment extends Fragment implements View.OnClickListener, NetworkConnectListener {

    FragmentNetworkBinding binding;
    private NetworkOrgAdapter mAdapter;
    private List<NetworkDataItem> suggestionList;

    private NetworkViewModel networkViewModel;

    private Integer my_connection = 0, invitation = 0, currentPage = 1;
    private GridLayoutManager linearLayoutManager;
    private boolean isLoading = false, isLastPage = false, isSearchClick = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        suggestionList = new ArrayList<>();
        mAdapter = new NetworkOrgAdapter(suggestionList, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNetworkBinding.inflate(getLayoutInflater());

        networkViewModel = new ViewModelProvider(this).get(NetworkViewModel.class);
        initLayout();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initObservers();
        networkViewModel.onConnectionInfo();
        loadNextPage();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initObservers() {
        networkViewModel.getConnectionInfoLiveData().observe(requireActivity(), model -> {
            if (model.getData() != null && model.getData().getConnectionsCount() != null) {
                my_connection = model.getData().getConnectionsCount();
                invitation = model.getData().getActiveConnectionRequestReceived();

                binding.tvMyConnection.setText("My Connections (" + my_connection + ")");
                binding.tvPendingConnection.setText("Invitations (" + invitation + ")");
            }
        });

        networkViewModel.getSuggestedLiveData().observe(requireActivity(), mData -> {
            if (mData != null && mData.getData() != null && mData.getData().getResults() != null && mData.getData().getResults().size() > 0) {
                isLoading = false;

                if (isSearchClick) {
                    suggestionList.clear();

                }
                binding.recyclerView.setVisibility(View.VISIBLE);
                suggestionList.addAll(mData.getData().getResults());
                mAdapter.notifyDataSetChanged();
                binding.progressBar.setVisibility(View.GONE);
            } else {
                binding.recyclerView.setVisibility(View.GONE);
                isLastPage = true;
            }
        });

        networkViewModel.getSuggestedSearchLiveData().observe(requireActivity(), mData -> {

            if (mData != null && mData.getData() != null
                    && mData.getData().getResults() != null
                    && mData.getData().getResults().size() > 0) {

                binding.recyclerView.setVisibility(View.VISIBLE);
                if (currentPage == 1) {
                    suggestionList.clear();
                }
                suggestionList.addAll(mData.getData().getResults());
                mAdapter.notifyDataSetChanged();
            } else {
                binding.recyclerView.setVisibility(View.GONE);
            }
        });

        networkViewModel.getFollowLiveData().observe(requireActivity(), networkConnectResponseModel -> {
            Log.e("DEBUG", "response = " + networkConnectResponseModel.getMessage());
        });
    }

    private void initLayout() {
        binding.etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch();
                hideSoftKeys();
                return true;
            }
            return false;
        });

        binding.ivSearch.setOnClickListener(v -> {
            hideSoftKeys();
            performSearch();
        });

        linearLayoutManager = new GridLayoutManager(requireActivity(), 2);
        binding.clMyConnection.setOnClickListener(this);
        binding.clInvitation.setOnClickListener(this);
        binding.recyclerView.setLayoutManager(linearLayoutManager);
        binding.recyclerView.setAdapter(mAdapter);


        binding.recyclerView.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;
                loadNextPage();
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });
    }

    private void loadNextPage() {
        networkViewModel.getSuggestionList(currentPage);
        if (currentPage > 1) {
            binding.progressBar.setVisibility(View.VISIBLE);
        }
    }

    private void hideSoftKeys() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }


    private void performSearch() {
        currentPage = 1;
        isLastPage = false;
        isLoading = true;
        isSearchClick = true;
        binding.listHeader.setText("Search Result");
        if (!binding.etSearch.getText().toString().equalsIgnoreCase("")) {
            networkViewModel.getSuggestionListSearch(binding.etSearch.getText().toString());
        } else {
            networkViewModel.getSuggestionList(currentPage);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cl_my_connection:
                startActivity(new Intent(requireContext(), ConnectionsActivity.class)
                        .putExtra(AppConstant.CONNECTION_TYPE, AppConstant.MY_CONNECTION)
                        .putExtra(AppConstant.MY_CONNECTION_COUNT, my_connection));
                break;

            case R.id.cl_invitation:
                someActivityResultLauncher.launch(new Intent(requireContext(), ConnectionsActivity.class)
                        .putExtra(AppConstant.CONNECTION_TYPE, AppConstant.INVITATION)
                        .putExtra(AppConstant.INVITATION_COUNT, invitation));
                break;
        }

    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    networkViewModel.onConnectionInfo();
                }
            });

    @Override
    public void onConnect(@NotNull NetworkDataItem model, int position) {
        NetWorkConnectModel netWorkConnectModel = new NetWorkConnectModel();
        netWorkConnectModel.setTarget_id(model.getTargetId());
        netWorkConnectModel.setAction("SEND");
        networkViewModel.onConnect(netWorkConnectModel);
        suggestionList.get(position).setStatus("PENDING");
        mAdapter.notifyItemChanged(position);
    }

    @Override
    public void openProfile(@NonNull String slug) {
        startActivity(new Intent(requireContext(), OrgProfileActivity.class).putExtra(AppConstant.PROFILE_SLUG, slug));
    }
}