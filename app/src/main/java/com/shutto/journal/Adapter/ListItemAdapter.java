package com.shutto.journal.Adapter;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shutto.journal.MainActivity;
import com.shutto.journal.Model.Story;
import com.shutto.journal.R;

import java.util.List;

class ListItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {

    ItemClickListener itemClickListener;
    TextView item_title, item_description;

    public ListItemViewHolder(View itemView) {
        super(itemView);

        itemView.setOnClickListener(this);
        itemView.setOnCreateContextMenuListener(this);

        item_title = (TextView) itemView.findViewById(R.id.item_title);
        item_description = (TextView) itemView.findViewById(R.id.item_description);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {

        itemClickListener.onClick(v, getAdapterPosition(), false);
    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        contextMenu.setHeaderTitle("Select your desired action");
        contextMenu.add(0,0,getAdapterPosition(),"DELETE");
    }
}

public class ListItemAdapter extends RecyclerView.Adapter<ListItemViewHolder>{

    MainActivity mainActivity;
    List<Story> storyList;

    public ListItemAdapter(MainActivity mainActivity, List<Story> storyList) {
        this.mainActivity = mainActivity;
        this.storyList = storyList;
    }

    @Override
    public ListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mainActivity.getBaseContext());
        View view = inflater.inflate(R.layout.list_item,parent,false);
        return new ListItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ListItemViewHolder holder, int position) {

        holder.item_title.setText(storyList.get(position).getTitle());
        holder.item_description.setText(storyList.get(position).getDescription());

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                mainActivity.title.setText(storyList.get(position).getTitle());
                mainActivity.description.setText(storyList.get(position).getDescription());
                mainActivity.isUpdate = true;
                mainActivity.idUpdate = storyList.get(position).getId();
            }
        });

    }

    @Override
    public int getItemCount() {
        return storyList.size();
    }
}
