package com.jorgejag.cuidaportman;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private Context context;
    private List<Upload> uploads;

    public ImageAdapter(Context context, List<Upload> uploads) {
        this.context = context;
        this.uploads = uploads;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.image_item, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ImageViewHolder holder, final int position) {
        final Upload uploadCurrent = uploads.get(position);
        Comment getComment = uploads.get(position).getComment();
        holder.textViewComentario.setText(uploadCurrent.getComment().toString());
        Picasso.get().load(uploadCurrent.getImageUrl()).placeholder(R.mipmap.ic_launcher).fit().centerCrop().into(holder.imageView);


        //Pasamos la incidencia a la activity DetailsActivity
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Comment getComment = uploads.get(position).getComment();
                BitmapDrawable bitmapDrawable = (BitmapDrawable) holder.imageView.getDrawable();

                Bitmap bitmap = bitmapDrawable.getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] bytes = stream.toByteArray();

                //Intent
                Intent intent = new Intent(context, DetailsActivity.class);
                intent.putExtra("comment", getComment.toString());
                intent.putExtra("image", bytes);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return uploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewComentario;
        public ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewComentario = itemView.findViewById(R.id.text_view_comentario);
            imageView = itemView.findViewById(R.id.image_view_upload);
        }


    }
}
