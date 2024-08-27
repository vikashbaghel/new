package com.app.rupyz.ui.home.home_slide;

import static com.app.rupyz.generic.utils.SharePrefConstant.TOKEN;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.app.rupyz.MainActivity;
import com.app.rupyz.R;
import com.app.rupyz.databinding.RiskLayoutScoreBinding;
import com.app.rupyz.generic.helper.DateFormatHelper;
import com.app.rupyz.generic.html.ScoreCardShareHelper;
import com.app.rupyz.generic.logger.Logger;
import com.app.rupyz.generic.model.individual.ExpInfoModel;
import com.app.rupyz.generic.network.ApiClient;
import com.app.rupyz.generic.network.ApiInterface;
import com.app.rupyz.generic.utils.SessionHelper;
import com.app.rupyz.generic.utils.SharedPref;
import com.app.rupyz.generic.utils.Utility;
import com.app.rupyz.ui.home.HomeFragment;
import com.app.rupyz.ui.home.activity.HomeDetailActivity;
import com.app.rupyz.ui.home.dailog.score_insights.CreditScoreInfoModal;
import com.app.rupyz.ui.home.dailog.score_insights.RefreshRemarkModal;
import com.app.rupyz.ui.individual.experian.OtpVerifyActivity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.izettle.html2bitmap.Html2Bitmap;
import com.izettle.html2bitmap.content.WebViewContent;

import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScoreFragment extends Fragment {

    RiskLayoutScoreBinding binding;
    private ApiInterface mApiInterface;
    private ExpInfoModel mExpData;
    private boolean visible;

    public ScoreFragment(boolean visible) {
        this.visible = visible;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = RiskLayoutScoreBinding.inflate(getLayoutInflater());
        mApiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
        initLayout();
        return binding.getRoot();
    }

    private void shareResultAsImage(Bitmap bitmap) {
        try {
            String pathOfBmp = MediaStore.Images.Media.insertImage(getContext().getContentResolver(),
                    bitmap, UUID.randomUUID().toString() + ".png", null);
            Logger.errorLogger("Bitmap Crash", pathOfBmp);
            if (pathOfBmp != "") {
                Uri bmpUri = Uri.parse(pathOfBmp);
                final Intent emailIntent1 = new Intent(android.content.Intent.ACTION_SEND);
                emailIntent1.setType("image/png");
                emailIntent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                emailIntent1.putExtra(Intent.EXTRA_STREAM, bmpUri);
                startActivity(emailIntent1);
            } else {
                Logger.errorLogger("Bitmap Crash", "null path ");
            }
        } catch (Exception ex) {
            Logger.errorLogger("Bitmap Crash", ex.getMessage());
            Logger.errorLogger("Bitmap Crash", ex.getLocalizedMessage());
        }
    }


    private void initLayout() {
        if (visible) {
            binding.btnMore.setVisibility(View.VISIBLE);
            binding.imgBtnShareCard.setVisibility(View.VISIBLE);
        } else {
            binding.btnMore.setVisibility(View.GONE);
            binding.imgBtnShareCard.setVisibility(View.GONE);
        }

        binding.scoreSeekBar.setEnabled(false);
        binding.txtCreditAge.setText("Credit Age: " + HomeFragment.mData.getCredit_age());
        binding.txtProfileDate.setText(DateFormatHelper.getProfileDate(
                HomeFragment.mData.getUpdated_at()
        ));
        binding.scoreValue.setText(HomeFragment.mData.getScore_value() + "");
        binding.scoreSeekBar.setProgress(HomeFragment.mData.getScore_value());
        binding.txtScorePauseDays.setText("Report update in " + HomeFragment.mData.getReport_pause_days() + " days");
        try {
            binding.scoreSeekBar.setEnabled(false);
            binding.txtCreditAge.setText("Credit Age: " + HomeFragment.mData.getCredit_age());
            binding.txtProfileDate.setText(DateFormatHelper.getProfileDate(
                    HomeFragment.mData.getUpdated_at()
            ));

            binding.imgBtnShareCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    binding.progressBarCardShareIndividual.setVisibility(View.VISIBLE);
                    binding.imgBtnShareCard.setVisibility(View.GONE);
                    new AsyncTask<Void, Void, Bitmap>() {
                        @Override
                        protected Bitmap doInBackground(Void... voids) {
                            String html = ScoreCardShareHelper.cardShareData(HomeFragment.mData, getContext());
                            return new Html2Bitmap.Builder().setContext(getActivity()).setContent(WebViewContent.html(html)).build().getBitmap();
                        }

                        @Override
                        protected void onPostExecute(Bitmap bitmap) {
                            if (bitmap != null) {
                                shareResultAsImage(bitmap);
                                binding.progressBarCardShareIndividual.setVisibility(View.GONE);
                                binding.imgBtnShareCard.setVisibility(View.VISIBLE);
//                              imageView.setImageBitmap(bitmap);
                            }
                        }
                    }.execute();
//                    initCreditScoreInfoSheet();
                }
            });
            binding.scoreValue.setText(HomeFragment.mData.getScore_value() + "");
            binding.scoreSeekBar.setProgress(HomeFragment.mData.getScore_value());
            binding.txtScorePauseDays.setText("Report update in " + HomeFragment.mData.getReport_pause_days() + " days");

            binding.btnCheckScore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (HomeFragment.mData.getReport_pause_days() <= 0) {
                        initExperian();
                        binding.btnCheckScore.setVisibility(View.GONE);
                        binding.progressBar.setVisibility(View.VISIBLE);
                    } else {
                        initBottomSheet(HomeFragment.mData.getReport_pause_days() + "");
                    }

                }
            });

            binding.btnScoreInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    initCreditScoreInfoSheet();
                }
            });

            binding.btnMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // ((MainActivity)getActivity()).replaceFragement();
                    //startActivity(new Intent(getContext(), HomeDetailActivity.class));
                    Intent intent = new Intent(getActivity(), HomeDetailActivity.class);
                    startActivity(intent);
                }
            });


            if (HomeFragment.mData.getScore_comment().equalsIgnoreCase("H")) {
                binding.riskType.setText(getResources().getString(R.string.low_risk));
                binding.riskType.setTextColor(getResources().getColor(R.color.light_green));
            } else if (HomeFragment.mData.getScore_comment().equalsIgnoreCase("M")) {
                binding.riskType.setText(getResources().getString(R.string.medium_risk));
                binding.riskType.setTextColor(getResources().getColor(R.color.light_yellow));
            } else if (HomeFragment.mData.getScore_comment().equalsIgnoreCase("L")) {
                binding.riskType.setText(getResources().getString(R.string.high_risk));
                binding.riskType.setTextColor(getResources().getColor(R.color.light_red));
            }
            initScoreValue();
            try {
                float score_percentage = ((HomeFragment.mData.getScore_value() - 300) * 100) / 600;

                int score = HomeFragment.mData.getScore_value();
                if (score >= 300 && score <= 650) {
                    binding.scoreScale.setText("Poor");
                    score_percentage = (((HomeFragment.mData.getScore_value() - 300) * 100) / 600);
                } else if (score >= 651 && score <= 770) {
                    binding.scoreScale.setText("Average");
                    score_percentage = (((HomeFragment.mData.getScore_value() - 300) * 100) / 600);
                } else if (score >= 771 && score <= 850) {
                    binding.scoreScale.setText("Good");
                    score_percentage = (((HomeFragment.mData.getScore_value() - 300) * 100) / 600);
                } else if (score >= 851 && score <= 900) {
                    binding.scoreScale.setText("Excellent ");
                    score_percentage = ((HomeFragment.mData.getScore_value() - 300) * 100) / 600;
                }
                LinearLayout.LayoutParams scorePointerLayout = (LinearLayout.LayoutParams)
                        binding.scorePointerLayout.getLayoutParams();
                scorePointerLayout.weight = score_percentage;
                binding.scorePointerLayout.setLayoutParams(scorePointerLayout);

                if (score > 850) {
                    LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) binding.thumb.getLayoutParams();
                    lp.gravity = Gravity.RIGHT;
                    binding.thumb.setLayoutParams(lp);
                }


                LinearLayout.LayoutParams scorePointerSpaceLayout = (LinearLayout.LayoutParams)
                        binding.scorePointerSpaceLayout.getLayoutParams();
                scorePointerSpaceLayout.weight = 100 - score_percentage;
                binding.scorePointerSpaceLayout.setLayoutParams(scorePointerSpaceLayout);

            } catch (Exception ex) {

            }
        } catch (Exception ex) {

        }
    }

    private void initScoreValue() {
        try {
            if (HomeFragment.mData.getGraph_data().getScore().size() > 1) {
                binding.scoreDownValue.setVisibility(View.VISIBLE);
                binding.scoreDownImage.setVisibility(View.VISIBLE);
                int current_score = Integer.parseInt(HomeFragment.mData.getGraph_data().getScore().get(HomeFragment.mData.getGraph_data().getScore().size() - 1));
                int previous_score = Integer.parseInt(HomeFragment.mData.getGraph_data().getScore().get(HomeFragment.mData.getGraph_data().getScore().size() - 2));
                if (current_score > previous_score) {
                    binding.scoreDownValue.setText((current_score - previous_score) + "");
                    binding.scoreDownValue.setTextColor(getActivity().getResources().getColor(R.color.light_green));
                    binding.scoreDownImage.setImageDrawable(getActivity().getResources().getDrawable(R.mipmap.ic_trending_up));
                } else {
                    binding.scoreDownValue.setText((previous_score - current_score) + "");
                    binding.scoreDownValue.setTextColor(getActivity().getResources().getColor(R.color.light_red));
                    binding.scoreDownImage.setImageDrawable(getActivity().getResources().getDrawable(R.mipmap.ic_trending_down));
                }
            } else {
                binding.scoreDownValue.setVisibility(View.GONE);
                binding.scoreDownImage.setVisibility(View.GONE);
            }
        } catch (Exception ex) {

        }
    }

    public void initBottomSheet(String days) {
        Bundle bundle = new Bundle();
        bundle.putString("days", days);
        RefreshRemarkModal fragment = new RefreshRemarkModal();
        fragment.setArguments(bundle);
        fragment.show(getChildFragmentManager(), "");
    }

    public void initCreditScoreInfoSheet() {
        Bundle bundle = new Bundle();
        CreditScoreInfoModal fragment = new CreditScoreInfoModal();
        fragment.setArguments(bundle);
        fragment.show(getChildFragmentManager(), "");
    }

    private void initExperian() {

        Call<String> call1 = mApiInterface.initExperian("Bearer " + SharedPref.getInstance().getString(TOKEN));
        call1.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                binding.btnCheckScore.setVisibility(View.VISIBLE);
                binding.progressBar.setVisibility(View.GONE);
                Logger.errorLogger(this.getClass().getName(), response.code() + "");
                if (response.code() == 200) {
                    Logger.errorLogger(this.getClass().getName(), response.body());
                    JsonParser jsonParser = new JsonParser();
                    JsonObject jsonObj = (JsonObject) jsonParser.parse(response.body());
                    Gson gson = new Gson();
                    mExpData = gson.fromJson(jsonObj.get("data"), ExpInfoModel.class);
                    Intent intent;
                    if (mExpData.getStep() == 0) {
                        intent = new Intent(getActivity(), OtpVerifyActivity.class);
                    } else {
                        intent = new Intent(getActivity(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    }
                    intent.putExtra("session_id", mExpData.getExp_data().getSession_id());
                    intent.putExtra("id", mExpData.getId());
                    startActivity(intent);
                } else {
                    try {
                        String responseBody = response.errorBody().string();
                        Logger.errorLogger(this.getClass().getName(), responseBody + "");
                        new SessionHelper(getActivity()).requestMessage(
                                responseBody);
                    } catch (Exception ex) {
                        Logger.errorLogger(this.getClass().getName(), ex.getMessage() + "");
                    }
                }
                try {
                    Logger.errorLogger(this.getClass().getName(), response.errorBody().string() + "");
                } catch (Exception ex) {
                    Logger.errorLogger(this.getClass().getName(), ex.getMessage() + "");
                }
                Logger.errorLogger(this.getClass().getName(), response.body() + "");
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                binding.btnCheckScore.setVisibility(View.VISIBLE);
                binding.progressBar.setVisibility(View.GONE);
                Logger.errorLogger(this.getClass().getName(), t.getMessage());

                call.cancel();
            }
        });
    }
}
