package com.androidkt.mythought;

import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by brijesh on 13/10/17.
 */

public class ThoughtDialogFragment extends DialogFragment {
    public static final String TAG = "ThoughtDialog";

    @BindView(R.id.thought)
    EditText thoughtView;
    @BindView(R.id.name)
    EditText nameView;

    ThoughtListener thoughtListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.dialog_thought, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach");
        if (context instanceof ThoughtListener) {
            thoughtListener = (ThoughtListener) context;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        Log.d(TAG, "onDismiss");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @OnClick(R.id.publish)
    public void onPublish(View view) {
        String thought = thoughtView.getText().toString().trim();
        String name = nameView.getText().toString().trim();
        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(thought)) {
            Thought th = new Thought(thought, name, FirebaseAuth.getInstance().getUid());
            thoughtListener.onPublishThought(th);
            dismiss();
        } else {
            Toast.makeText(getActivity(), "Enter proper Values", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.cancel)
    public void onCancel(View view) {
        dismiss();
    }

    interface ThoughtListener {
        void onPublishThought(Thought thought);
    }
}
