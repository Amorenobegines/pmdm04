package dam.pmdm.spyrothedragon.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dam.pmdm.spyrothedragon.FlameView;
import dam.pmdm.spyrothedragon.MainActivity;
import dam.pmdm.spyrothedragon.R;
import dam.pmdm.spyrothedragon.models.Character;

public class CharactersAdapter extends RecyclerView.Adapter<CharactersAdapter.CharactersViewHolder> {

    private List<Character> list;

    public CharactersAdapter(List<Character> charactersList) {
        this.list = charactersList;
    }

    @NonNull
    @Override
    public CharactersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview, parent, false);
        return new CharactersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CharactersViewHolder holder, int position) {
        Character character = list.get(position);
        holder.nameTextView.setText(character.getName());

        // Cargar la imagen (simulado con un recurso drawable)
        int imageResId = holder.itemView.getContext().getResources().getIdentifier(character.getImage(), "drawable", holder.itemView.getContext().getPackageName());
        holder.imageImageView.setImageResource(imageResId);
        // Activar el Easter Egg
        if (character.getName().equals("Spyro")) {
            holder.itemView.setOnLongClickListener(view -> {
                ViewGroup parent = (ViewGroup) holder.itemView;

                // Eliminar fuego anterior si está activado
                for (int i = 0; i < parent.getChildCount(); i++) {
                    View child = parent.getChildAt(i);
                    if (child instanceof FlameView) {
                        parent.removeView(child);   // Eliminar el fuego
                    }
                }

                // Crea la instancia del fuego
                FlameView flameView = new FlameView(holder.itemView.getContext());
                flameView.setId(View.generateViewId());

                // Tamaño del fuego
                int fireWidth = holder.imageImageView.getWidth() / 2;
                int fireHeight = holder.imageImageView.getHeight() / 3;

                flameView.setLayoutParams(new ViewGroup.LayoutParams(fireWidth, fireHeight));

                // Posicionar el fuego
                float mouthOffsetX = holder.imageImageView.getWidth() * 0.29f;
                float mouthOffsetY = holder.imageImageView.getHeight() * 0.7f;

                flameView.setX(holder.imageImageView.getX() + mouthOffsetX);
                flameView.setY(holder.imageImageView.getY() + mouthOffsetY);

                parent.addView(flameView);

                // Rotar 180º el fuego
                flameView.setRotation(180f);

                // Iniciar la animación del fuego
                MainActivity.startAudio(holder.itemView.getContext(), R.raw.fire);
                flameView.startAnimation();

                // Avisar de activación del Easter Egg
                Toast.makeText(holder.itemView.getContext(), holder.itemView.getResources().getString(R.string.easter_egg), Toast.LENGTH_SHORT).show();

                // Ocultar después de 3 segundos
                flameView.postDelayed(() -> {
                    flameView.stopAnimation();
                    parent.removeView(flameView);
                }, 3000);

                return true;
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class CharactersViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView;
        ImageView imageImageView;

        public CharactersViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.name);
            imageImageView = itemView.findViewById(R.id.image);
        }
    }
}