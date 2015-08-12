package com.george.redditreader.Fragments;


import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.george.redditreader.Activities.MainActivity;
import com.george.redditreader.Activities.UserActivity;
import com.george.redditreader.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class EnterUserDialogFragment extends DialogFragment implements View.OnClickListener {

    private MainActivity activity;
    private EditText editText;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        activity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_enter_user, container, false);
        Button cancelButton = (Button) view.findViewById(R.id.button_cancel);
        Button viewButton = (Button) view.findViewById(R.id.button_view);
        editText = (EditText) view.findViewById(R.id.editText_subreddit);
        editText.requestFocus();

        cancelButton.setOnClickListener(this);
        viewButton.setOnClickListener(this);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                onClick(v);
                return true;
            }
        });

        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        getDialog().setCanceledOnTouchOutside(true);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setDialogWidth();
    }

    private void setDialogWidth() {
        Window window = getDialog().getWindow();
        int width = 3 * getResources().getDisplayMetrics().widthPixels / 4;
        window.setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.button_cancel) {
            dismiss();
        }
        else {
            String user = editText.getText().toString();
            if(!user.equals("")) {
                dismiss();
                user = user.replaceAll("\\s","");
                Intent intent = new Intent(activity, UserActivity.class);
                intent.putExtra("username", user.toLowerCase());
                startActivity(intent);
            }
            else {
                editText.setHint(R.string.enter_user);
                editText.setHintTextColor(getResources().getColor(R.color.red));
            }
        }
    }

}
