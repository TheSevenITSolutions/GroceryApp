package com.delightbasket.grocery.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.delightbasket.grocery.R;
import com.delightbasket.grocery.adapters.ChatAdapter;
import com.delightbasket.grocery.databinding.FragmentChatBinding;


public class ChatFragment extends Fragment {
    FragmentChatBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat, container, false);

        initView();
        return binding.getRoot();
    }

    private void initView() {
        ChatAdapter chatAdapter = new ChatAdapter();
        binding.rvChat.setAdapter(chatAdapter);
    }
}