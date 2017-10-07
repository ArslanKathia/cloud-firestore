package com.androidkt.mythought;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.androidkt.mythought.adapter.FirestoreAdapter;
import com.androidkt.mythought.adapter.ThoughtListAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    public static final String PUBLIC_THOUGHT = "publicThought";
    private static final String TAG = "MainActivity";
    private static final int LIMIT = 50;
    @BindView(R.id.publish)
    Button publish;
    @BindView(R.id.thought)
    EditText thoughtView;
    @BindView(R.id.name)
    EditText nameView;

    @BindView(R.id.thoughtList)
    RecyclerView thoughtListView;

    DocumentReference docRef;
    FirestoreAdapter adapter;

    RecyclerView.LayoutManager layoutManager;
    FirebaseFirestore firebaseFirestore;
    private List<Thought> thoughts;
    private Query query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        FirebaseFirestore.setLoggingEnabled(true);
        firebaseFirestore = FirebaseFirestore.getInstance();

        docRef = firebaseFirestore.document("thought/lifeThought");

        query = docRef.collection(PUBLIC_THOUGHT).orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(LIMIT);
        thoughts = new ArrayList<>();
        adapter = new ThoughtListAdapter(query) {
            @Override
            protected void onDataChanged() {
                Log.d(TAG, "Data Change");
                thoughtListView.scrollToPosition(0);
            }

            @Override
            protected void onError(FirebaseFirestoreException e) {
                Log.e(TAG, e.toString());
            }
        };
        layoutManager = new LinearLayoutManager(this);

        thoughtListView.setLayoutManager(layoutManager);

        thoughtListView.setAdapter(adapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }

    public void publishThought(View view) {
        final String name = nameView.getText().toString().trim();
        String thought = thoughtView.getText().toString().trim();

        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(thought)) {
            Thought th = new Thought(thought, name);
            docRef.collection(PUBLIC_THOUGHT).add(th)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                            nameView.setText("");
                            thoughtView.setText("");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error adding document", e);
                        }
                    });
        }
    }
}
