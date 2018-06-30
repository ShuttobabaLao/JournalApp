package com.shutto.journal;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.shutto.journal.Adapter.ListItemAdapter;
import com.shutto.journal.Model.Story;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity {

    List<Story> storyList = new ArrayList<>();
    FirebaseFirestore db;

    RecyclerView listItem;
    RecyclerView.LayoutManager layoutManager;

    FloatingActionButton fab;

    public MaterialEditText title, description;
    public boolean isUpdate = false;
    public String idUpdate = "";
    ListItemAdapter adapter;

    SpotsDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();

        dialog = new SpotsDialog(this);

        title = (MaterialEditText) findViewById(R.id.title);
        description = (MaterialEditText) findViewById(R.id.description);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!isUpdate){
                    setData(title.getText().toString(), description.getText().toString());
                }
                else{
                    upDateData(title.getText().toString(), description.getText().toString());
                    isUpdate = !isUpdate;
                }
            }
        });
        
        listItem = (RecyclerView) findViewById(R.id.story);
        listItem.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        listItem.setLayoutManager(layoutManager);

        loadData();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle().equals("DELETE"));
        deleteItem(item.getOrder());
        return super.onContextItemSelected(item);
    }

    private void deleteItem(int index) {
        db.collection("StoryList").document(storyList.get(index).getId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        loadData();
                    }
                });
    }

    private void upDateData(String title, String description) {
        db.collection("StoryList").document(idUpdate).update(title, "title", description, "description").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
              Toast.makeText(MainActivity.this, "Updated", Toast.LENGTH_SHORT).show();
            }
        });

        //realtime update
        db.collection("StoryList").document(idUpdate).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                loadData();
            }
        });
    }

    private void setData(String title, String description) {
        String id = UUID.randomUUID().toString();
        Map<String, Object> story = new HashMap<>();
        story.put("id", id);
        story.put("title", title);
        story.put("description", description);

        db.collection("StoryList").document(id).set(story).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                loadData();
            }
        });
    }

    private void loadData() {
        dialog.show();
        if (storyList.size() > 0) storyList.clear();
        Task<QuerySnapshot> querySnapshotTask = db.collection("StoryList")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        for (DocumentSnapshot doc:task.getResult()) {
                            Story story = new Story(doc.getString("id"), doc.getString("title"), doc.getString("description"));
                            storyList.add(story);
                        }
                        adapter = new ListItemAdapter(MainActivity.this, storyList);
                        listItem.setAdapter(adapter);
                        dialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(MainActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
