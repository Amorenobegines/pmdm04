package dam.pmdm.spyrothedragon;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import java.util.Objects;

import dam.pmdm.spyrothedragon.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    NavController navController = null;
    private NavController navGuideController = null;

    private Boolean needToStartGuide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflar la vista
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        // Configurar Toolbar
        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // Configurar Navigation
        Fragment navHostFragment = getSupportFragmentManager().findFragmentById(R.id.navHostFragment);
        if (navHostFragment != null) {
            navController = NavHostFragment.findNavController(navHostFragment);
            NavigationUI.setupWithNavController(binding.navView, navController);
            NavigationUI.setupActionBarWithNavController(this, navController);
        }

        binding.navView.setOnItemSelectedListener(this::selectedBottomMenu);    // Maneja el clic en el menú

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            // Si se navega a una pantalla donde se desea mostrar la flecha de atrás, habilítala
            getSupportActionBar().setDisplayHomeAsUpEnabled(destination.getId() != R.id.navigation_characters &&
                    destination.getId() != R.id.navigation_worlds &&
                    destination.getId() != R.id.navigation_collectibles);
        });
        // Comprobar si la guía ha sido vista antes
        needToStartGuide = getPreferences(MODE_PRIVATE).getBoolean("guideWatched", false);
    }

    /**
     *
     * @param menuItem El elemento del menú que se ha seleccionado.
     * @return true si el elemento ha sido seleccionado, false en caso contrario.
     */
    private boolean selectedBottomMenu(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.nav_characters) {
            if (Objects.requireNonNull(navController.getCurrentDestination()).getId() == R.id.navigation_worlds) {
                navController.navigate(R.id.action_worlds_characters);
            } else {
                navController.navigate(R.id.action_collectibles_characters);
            }
        }else if (menuItem.getItemId() == R.id.nav_worlds) {
            if (Objects.requireNonNull(navController.getCurrentDestination()).getId() == R.id.navigation_characters) {
                navController.navigate(R.id.action_characters_worlds);
            } else {
                navController.navigate(R.id.action_collectibles_worlds);
            }
        } else if (menuItem.getItemId() == R.id.nav_collectibles) {
            if (Objects.requireNonNull(navController.getCurrentDestination()).getId() == R.id.navigation_characters) {
                navController.navigate(R.id.action_characters_collectibles);
            } else {
                navController.navigate(R.id.action_worlds_collectibles);
            }
        }
        return true;
    }

    /**
     *
     * @param menu El menú de opciones en el que colocas tus artículos.
     *
     * @return true si se ha creado el menú, false en caso contrario.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Infla el menú
        getMenuInflater().inflate(R.menu.about_menu, menu);
        return true;
    }

    // Maneja el clic en el ítem de información
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Gestiona el clic en el ítem de información
        if (item.getItemId() == R.id.action_info) {
            showInfoDialog();  // Muestra el diálogo
            return true;
        }
        return super.onOptionsItemSelected(item);   // Maneja el clic en otro ítem
    }

    // Muestra el diálogo de información
    private void showInfoDialog() {
        // Crear un diálogo de información
        new AlertDialog.Builder(this)
                .setTitle(R.string.title_about) // Establece el título
                .setMessage(R.string.text_about)// Establece el mensaje
                .setPositiveButton(R.string.accept, null)// Establece el botón de aceptar
                .show();        // Muestra el diálogo
    }

    // Maneja el clic en la flecha de atrás
    public ActivityMainBinding getBinding() {
        return binding;     // Devuelve el binding
    }

    // Empieza la animación de audio
    @Override
    protected void onStart() {
        super.onStart();
        startAudio(this, R.raw.gema_sound);
        if (!needToStartGuide) {
            startGuide();
            getPreferences(MODE_PRIVATE).edit().putBoolean("needToStartGuide", true).apply();
        } else {
            binding.includeLayout.guideWelcome.setVisibility(View.GONE);
            binding.fullScreenFragmentContainer.setVisibility(View.GONE);
        }
    }

    /**
     * Muestra la guía de inicio
     */
    private void startGuide() {
        // Mantener el logo animado
        ImageView logoSpyro = binding.getRoot().findViewById(R.id.logo_spyro);
        logoSpyro.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale_rotale));

        // Cambiar statuView por Button
        Button statuButton = binding.getRoot().findViewById(R.id.statuButton); // Cambiar el ID al nuevo botón
        statuButton.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale_pulse));

        statuButton.setOnClickListener(v -> {
            startAudio(this, R.raw.gema_sound);
            v.clearAnimation();

            binding.includeLayout.guideWelcome.startAnimation(AnimationUtils.loadAnimation(this, R.anim.set_left));

            // Esperar a que termine la animación antes de ocultar la vista
            binding.includeLayout.guideWelcome.postDelayed(() ->
                    binding.includeLayout.guideWelcome.setVisibility(View.GONE), 400);

            binding.fullScreenFragmentContainer.startAnimation(AnimationUtils.loadAnimation(this, R.anim.set_right));

            // Esperar a que termine la animación antes de mostrar la vista
            binding.fullScreenFragmentContainer.postDelayed(() ->
                    binding.fullScreenFragmentContainer.setVisibility(View.VISIBLE), 400);

            navGuideController = NavHostFragment.findNavController(
                    Objects.requireNonNull(getSupportFragmentManager().findFragmentById(R.id.fullScreenFragmentContainer))
            );
            navGuideController.navigate(R.id.navigation_guide);
        });

        // Se libera el espacio de memoria
        navGuideController = null;
    }
    // Animación de audio
    public static void startAudio(Context context, int soundId) {
        MediaPlayer mediaPlayer = MediaPlayer.create(context, soundId);
        mediaPlayer.start();
    }
}