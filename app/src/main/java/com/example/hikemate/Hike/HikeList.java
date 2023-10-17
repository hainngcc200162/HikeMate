package com.example.hikemate.Hike;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.MemoryCategory;
import com.example.hikemate.Database.HikeDatabase;
import com.example.hikemate.Database.Model.Hike;
import com.example.hikemate.Database.Model.HikeImage;
import com.example.hikemate.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import android.view.LayoutInflater;

public class HikeList extends Fragment {
    private RecyclerView detailsRecView;
    private TextView txtEmpty;
    private ShapeableImageView imageEmpty;
    private HikeAdapter hikeAdapter;
    private ArrayList<Hike> hikeArrayList;
    private HikeDatabase db;
    private Button btncreateHike;
    private Hike deletedHike;
    private HikeImage deletedHikeImage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.hike_list, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initRecyclerview();
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();

            if (direction == ItemTouchHelper.LEFT) {
                deletedHike = hikeArrayList.get(position);
                deletedHikeImage = db.hikeImageDao().getHikeImageById(deletedHike.getId());

                //Dialog builder
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity(), R.style.ThemeOverlay_App_MaterialAlertDialog);
                builder.setTitle("Delete person");
                builder.setMessage("Are you sure you want to delete person " + deletedHike.getHikeName() + "?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Perform guest login action
                        db.hikeDao().deleteHike(deletedHike); // Delete from the database first
                        hikeArrayList.remove(position); // Then remove from the list
                        hikeAdapter.notifyItemRemoved(position);
                        Snackbar.make(detailsRecView, "The person " + deletedHike.getHikeName() + " was removed!", Snackbar.LENGTH_LONG)
                                .setAction("Undo", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        db.hikeDao().insert(deletedHike); // Insert back to the database
                                        db.hikeImageDao().insertImage(deletedHikeImage);
                                        hikeArrayList.add(position, deletedHike); // Add back to the list
                                        hikeAdapter.notifyItemInserted(position);
                                        initRecyclerview();
                                    }
                                }).setActionTextColor(Color.parseColor("#0083BE"))
                                .show();
                        initRecyclerview();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        initRecyclerview();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
    }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE && isCurrentlyActive) {
                View itemView = viewHolder.itemView;
                Paint paint = new Paint();
                Drawable icon = ContextCompat.getDrawable(getActivity(), R.drawable.ic_delete);
                int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;

                // Draw the red background
                paint.setColor(Color.parseColor("#F84949"));
                c.drawRect(itemView.getRight() + dX, itemView.getTop(), itemView.getRight(), itemView.getBottom(), paint);

                // Draw the icon on the background
                int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
                int iconRight = itemView.getRight() - iconMargin;
                int iconTop = itemView.getTop() + iconMargin;
                int iconBottom = itemView.getBottom() - iconMargin;
                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                icon.draw(c);
            }
        }
    };

    private void initRecyclerview() {

        Glide.get(getActivity()).setMemoryCategory(MemoryCategory.HIGH);
        db = HikeDatabase.getInstance(getActivity());

        hikeArrayList = (ArrayList<Hike>) db.hikeDao().getAllHikes();

        if(hikeArrayList.isEmpty()){
            txtEmpty.setVisibility(View.VISIBLE);
            imageEmpty.setVisibility(View.VISIBLE);
            detailsRecView.setVisibility(View.GONE);
        }else{
            txtEmpty.setVisibility(View.GONE);
            imageEmpty.setVisibility(View.GONE);
            detailsRecView.setVisibility(View.VISIBLE);

            detailsRecView.setLayoutManager(new LinearLayoutManager(getActivity()));
            hikeAdapter = new HikeAdapter(hikeArrayList, getActivity());
            detailsRecView.setAdapter(hikeAdapter);

            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
            itemTouchHelper.attachToRecyclerView(detailsRecView);
        }



    }

    private void initView(View view) {
        detailsRecView = view.findViewById(R.id.detailsRecView);
        txtEmpty = view.findViewById(R.id.txtEmpty);
        imageEmpty = view.findViewById(R.id.imageEmpty);
        db = HikeDatabase.getInstance(getActivity());
        detailsRecView = view.findViewById(R.id.detailsRecView);


        btncreateHike = view.findViewById(R.id.btnCreateHike);
        btncreateHike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), HikeActivity.class));
            }
        });
    }
}