package com.androidkt.mythought;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.androidkt.mythought.adapter.ClickListener;
import com.androidkt.mythought.adapter.FirestoreAdapter;
import com.androidkt.mythought.adapter.RecyclerItemTouchHelper;
import com.androidkt.mythought.adapter.ThoughtListAdapter;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements ClickListener,
        RecyclerItemTouchHelper.RecyclerItemTouchHelperListener, ThoughtDialogFragment.ThoughtListener,
        OnFailureListener {

    public static final String PUBLIC_THOUGHT = "publicThought";
    private static final String TAG = "MainActivity";
    private static final int LIMIT = 50;
    private static final int RC_SIGN_IN = 901;


    @BindView(R.id.thoughtList)
    RecyclerView thoughtListView;

    @BindView(R.id.constraintLayout)
    ConstraintLayout constraintLayout;

    DocumentReference docRef;
    FirestoreAdapter adapter;
    RecyclerView.LayoutManager layoutManager;
    FirebaseFirestore firebaseFirestore;
    private ThoughtDialogFragment thoughtDialogFragment;
    private List<Thought> thoughts;
    private Query query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        FirebaseFirestore.setLoggingEnabled(true);
        firebaseFirestore = FirebaseFirestore.getInstance();

        docRef = FirebaseFirestore.getInstance().document("thought/lifeThought");

        query = docRef.collection(PUBLIC_THOUGHT).orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(LIMIT);
        thoughts = new ArrayList<>();


        adapter = new ThoughtListAdapter(query, this) {
            @Override
            protected void onDataChanged() {
                Log.d(TAG, "Data Change");
                thoughtListView.scrollToPosition(0);
            }

            @Override
            protected void onError(FirebaseFirestoreException e) {
                handleFirebaseException(e);
            }
        };
        layoutManager = new LinearLayoutManager(this);
        thoughtListView.setLayoutManager(layoutManager);

        thoughtListView.setItemAnimator(new DefaultItemAnimator());
        thoughtListView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        thoughtListView.setAdapter(adapter);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(thoughtListView);

        thoughtDialogFragment = new ThoughtDialogFragment();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (shouldStartSignIn()) {
            startSignIn();
            return;
        }
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

    private boolean shouldStartSignIn() {
        return (FirebaseAuth.getInstance().getCurrentUser() == null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RC_SIGN_IN) {
            if (adapter != null) {
                adapter.startListening();
            }
        }
    }

    private void startSignIn() {
        // Sign in with FirebaseUI
        Intent intent = AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(Collections.singletonList(
                        new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                .setIsSmartLockEnabled(false)
                .build();

        startActivityForResult(intent, RC_SIGN_IN);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_thought:
                onAddItemClicks();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onAddItemClicks() {
        thoughtDialogFragment.show(getFragmentManager(), ThoughtDialogFragment.TAG);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof ThoughtListAdapter.ThoughtItemHolder) {
            final DocumentSnapshot documentSnapshot = adapter.getSnapshot(position);
            final Thought thought = documentSnapshot.toObject(Thought.class);


            docRef.collection(PUBLIC_THOUGHT).document(documentSnapshot.getId()).delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "Item Delete");
                        }
                    })
                    .addOnFailureListener(this);

            Snackbar snackbar = Snackbar.make(constraintLayout, thought.getText() + " removed from cart!", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setDocument(documentSnapshot.getId(), thought);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

    public void setDocument(String docId, Thought thought) {
        docRef.collection(PUBLIC_THOUGHT).document(docId).set(thought).
                addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Document Undo Successfully");
                    }
                }).
                addOnFailureListener(this);
    }

    @Override
    public void onPublishThought(Thought thought) {
        docRef.collection(PUBLIC_THOUGHT).add(thought)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "Document Successfully Publish");
                    }
                })
                .addOnFailureListener(this);
    }

    @Override
    public void onItemClick(View view, int position) {
        final Thought thought = adapter.getSnapshot(position).toObject(Thought.class);
    }

    @Override
    public void onFailure(@NonNull Exception e) {

        if (e instanceof FirebaseFirestoreException) {
            handleFirebaseException((FirebaseFirestoreException) e);
        }
        Log.e(TAG, "Document can't be updated " + e.toString());
    }

    private void handleFirebaseException(@NonNull FirebaseFirestoreException e) {
        if (e.getCode() == FirebaseFirestoreException.Code.PERMISSION_DENIED) {
            Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }


}
