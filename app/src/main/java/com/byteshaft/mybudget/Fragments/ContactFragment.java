package com.byteshaft.mybudget.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.byteshaft.mybudget.R;


public class ContactFragment extends Fragment implements View.OnClickListener{

    private View baseView;
    private EditText mEditTextName;
    private EditText mEditTextEmail;
    private EditText mEditTextMessage;
    private Button mButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        baseView = inflater.inflate(R.layout.contact_fragment, container, false);
        mEditTextName = (EditText) baseView.findViewById(R.id.editText);
        mEditTextEmail = (EditText) baseView.findViewById(R.id.editTextEmail);
        mEditTextMessage = (EditText) baseView.findViewById(R.id.editTextMessage);
        mButton = (Button) baseView.findViewById(R.id.button);
        mButton.setOnClickListener(this);
        return baseView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                String name = mEditTextName.getText().toString();
                String email = mEditTextEmail.getText().toString();
                String message = mEditTextMessage.getText().toString();
                if (name.isEmpty()) {
                    Toast.makeText(getActivity(), "Please Enter Name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (email.isEmpty()) {
                    Toast.makeText(getActivity(), "please enter email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (message.isEmpty()) {
                    Toast.makeText(getActivity(), "please enter Message", Toast.LENGTH_SHORT).show();
                }

                if (!name.isEmpty() && !email.isEmpty() && !message.isEmpty()) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("message/rfc822");
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
                    intent.putExtra(Intent.EXTRA_SUBJECT, name);
                    intent.putExtra(Intent.EXTRA_TEXT, message);
                    try {
                        startActivity(Intent.createChooser(intent, "Send mail..."));
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(getActivity(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                    }
                }

                break;
        }
    }
}
