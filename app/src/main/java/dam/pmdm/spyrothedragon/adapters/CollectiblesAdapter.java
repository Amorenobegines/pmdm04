package dam.pmdm.spyrothedragon.adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dam.pmdm.spyrothedragon.MainActivity;
import dam.pmdm.spyrothedragon.R;
import dam.pmdm.spyrothedragon.databinding.ActivityMainBinding;
import dam.pmdm.spyrothedragon.models.Collectible;

public class CollectiblesAdapter extends RecyclerView.Adapter<CollectiblesAdapter.CollectiblesViewHolder> {

    private List<Collectible> list;
    private int gemPress;

    public CollectiblesAdapter(List<Collectible> collectibleList) {
        this.list = collectibleList;
    }

    @NonNull
    @Override
    public CollectiblesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview, parent, false);
        return new CollectiblesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CollectiblesViewHolder holder, int position) {
        Collectible collectible = list.get(position);
        holder.nameTextView.setText(collectible.getName());
        gemPress = 0;

        // Cargar la imagen (simulado con un recurso drawable)
        int imageResId = holder.itemView.getContext().getResources().getIdentifier(collectible.getImage(), "drawable", holder.itemView.getContext().getPackageName());
        holder.imageImageView.setImageResource(imageResId);

        if (collectible.getName().equals("Gemas")) {
            holder.itemView.setOnClickListener(view -> {
                gemPress++;

                if (gemPress == 4) {
                    ActivityMainBinding binding = ((MainActivity) holder.itemView.getContext()).getBinding();

                    // Preparar el video
                    String path = "android.resource://" + holder.itemView.getContext().getPackageName() + "/" + R.raw.video;
                    binding.videoView.setVideoURI(Uri.parse(path));
                    binding.viewVideo.setVisibility(View.VISIBLE);

                    // Reproducir el video
                    binding.videoView.setOnPreparedListener(mp -> {
                        // Vídeo con el máximo tamaño en función de la pantalla
                        int videoWidth = mp.getVideoWidth();
                        int videoHeight = mp.getVideoHeight();
                        float videoProportion = (float) videoWidth / (float) videoHeight;

                        int screenWidth = binding.getRoot().getWidth();
                        int screenHeight = binding.getRoot().getHeight();
                        float screenProportion = (float) screenWidth / (float) screenHeight;

                        ViewGroup.LayoutParams layoutParams = binding.videoView.getLayoutParams();

                        if (videoProportion > screenProportion) {
                            layoutParams.width = screenWidth;
                            layoutParams.height = (int) (screenWidth / videoProportion);
                        } else {
                            layoutParams.width = (int) (screenHeight * videoProportion);
                            layoutParams.height = screenHeight;
                        }

                        binding.videoView.setLayoutParams(layoutParams);

                        mp.start();

                        // Avisar de activación del Easter Egg
                        Toast.makeText(holder.itemView.getContext(), holder.itemView.getResources().getString(R.string.easter_egg), Toast.LENGTH_SHORT).show();
                    });

                    // Manejar el clic en el video para cerrarlo
                    binding.videoView.setOnClickListener(v -> {
                        binding.videoView.stopPlayback();
                        binding.viewVideo.setVisibility(View.GONE);
                    });

                    // Manejar el final del video para cerrarlo
                    binding.videoView.setOnCompletionListener(mp -> {
                        binding.videoView.stopPlayback();
                        binding.viewVideo.setVisibility(View.GONE);
                    });

                    gemPress = 0;
                }
            });
        }
    }
    
    @Override
    public int getItemCount() {
        return list.size();
    }
    // Clase para el ViewHolder
    public static class CollectiblesViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView;
        ImageView imageImageView;

        public CollectiblesViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.name);
            imageImageView = itemView.findViewById(R.id.image);
        }
    }
}