package com.byteshaft.mybudget.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.byteshaft.mybudget.R;


public class ContactFragment extends Fragment implements View.OnClickListener {

    private View baseView;
    private EditText mEditTextName;
    private EditText mEditTextNumber;
    private EditText mEditTextSubject;
    private EditText mEditTextMessage;
    private Button mButton;
    private String emailAddress = "contact@amifinancialsolutions.ie";
    private TextView mTextViewCall;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        baseView = inflater.inflate(R.layout.contact_fragment, container, false);
        mEditTextName = (EditText) baseView.findViewById(R.id.editText1);
        mEditTextNumber = (EditText) baseView.findViewById(R.id.editText2);
        mEditTextSubject = (EditText) baseView.findViewById(R.id.editText3);
        mEditTextMessage = (EditText) baseView.findViewById(R.id.editText4);
        mButton = (Button) baseView.findViewById(R.id.button);
        mButton.setOnClickListener(this);
        mTextViewCall = (TextView) baseView.findViewById(R.id.textViewCall);
        mTextViewCall.setOnClickListener(this);
        return baseView;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                String name = mEditTextName.getText().toString();
                String number = mEditTextNumber.getText().toString();
                String subject = mEditTextSubject.getText().toString();
                String message = mEditTextMessage.getText().toString();
                if (name.isEmpty()) {
                    Toast.makeText(getActivity(), "Please Enter Name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (message.isEmpty()) {
                    Toast.makeText(getActivity(), "please enter Message", Toast.LENGTH_SHORT).show();
                }

                if (!name.isEmpty() &&  !message.isEmpty()) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("message/rfc822");
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailAddress});
                    if (!subject.isEmpty()) {
                        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                    }
                    if (!number.isEmpty()) {
                        intent.putExtra(Intent.EXTRA_TEXT, message + "\n\n" + name + "\n" + number);
                    } else {
                        intent.putExtra(Intent.EXTRA_TEXT, message + "\n\n" + name);
                    }
                    try {
                        startActivity(Intent.createChooser(intent, "Send mail..."));
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(getActivity(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.textViewCall:
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + "01-6604936"));
                startActivity(callIntent);
        }
    }
}
