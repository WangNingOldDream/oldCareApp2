package com.demo.btmNav.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.demo.oldcare.R;
import com.demo.userProfile.EditProfileActivity;
import com.demo.userProfile.LoginActivity;
import com.demo.utils.ImageUtil;

public class UserProfileFragment extends VPFragment {
    private TextView tvNickName,tvAccount,tvAge,tvGender,tvCity,tvHome,tvSign,tvBirthdayTime;
    private Button btToEdit,btToLogin;

    private ImageView ivAvatar;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public UserProfileFragment() {

    }

    public static UserProfileFragment newInstance(String param1, String param2) {
        UserProfileFragment fragment = new UserProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        Log.d(TAG, "onCreateContextMenu: ");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView: ");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initView(view);
        initData();
        Log.d(TAG, "onViewCreated: ");
    }
    private void initView(View view) {
        tvAccount = view.findViewById(R.id.tv_account_text);
        tvNickName = view.findViewById(R.id.tv_nick_name);
        tvAge = view.findViewById(R.id.tv_age);
        tvHome = view.findViewById(R.id.tv_home_text);
        tvSign = view.findViewById(R.id.tv_sign_text);
        tvBirthdayTime =view. findViewById(R.id.tv_birth_time_text);
        tvGender = view.findViewById(R.id.tv_gender);
        tvCity = view.findViewById(R.id.tv_city);
        ivAvatar = view.findViewById(R.id.iv_avatar);
        btToEdit =view.findViewById(R.id.btn_to_edit);
        btToLogin=view.findViewById(R.id.btn_to_login);
        btToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
        btToEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toEdit();
            }
        });
    }

    private static final String TAG = "UserProfileFragment";

    @Override
    public void onStart() {
        super.onStart();
        initData();
        Log.d(TAG, "onStart: ");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }

    private void initData() {
        getDataFromSpf();
    }
    private void getDataFromSpf() {
        SharedPreferences spfRecord = getActivity().getSharedPreferences("spfRecord", MODE_PRIVATE);
        String account = spfRecord.getString("account", "");
        String nickName = spfRecord.getString("nick_name", "");
        String city = spfRecord.getString("city", "");
        String gender = spfRecord.getString("gender", "");
        String birthDayTime = spfRecord.getString("birth_day_time", "");
        String sign = spfRecord.getString("sign", "");
        String image64 = spfRecord.getString("image_64", "");
        String age = getAgeByBirthDay(birthDayTime);

        tvAccount.setText(account);
        tvNickName.setText(nickName);
        tvAge.setText(age);
        tvHome.setText(city);
        tvSign.setText(sign);
        tvBirthdayTime.setText(birthDayTime);
        tvGender.setText(gender);
        tvCity.setText(city);
        ivAvatar.setImageBitmap(ImageUtil.base64ToImage(image64));
    }
    private String getAgeByBirthDay(String birthDayTime) {

        if (TextUtils.isEmpty(birthDayTime)) {
            return "";
        }

        try {
            int index = birthDayTime.indexOf("年");
            String result = birthDayTime.substring(0, index);

            int parseInt = Integer.parseInt(result);
            return String.valueOf(2024 - parseInt);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";

    }
    public void toEdit() {
        Intent intent = new Intent(requireActivity(), EditProfileActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }
    public void logout() {
        SharedPreferences spf = requireActivity().getSharedPreferences("spfRecord", MODE_PRIVATE);
        SharedPreferences.Editor edit = spf.edit();
        edit.putBoolean("isLogin", false);
        edit.apply();
        Intent intent = new Intent(requireActivity(), LoginActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }
}