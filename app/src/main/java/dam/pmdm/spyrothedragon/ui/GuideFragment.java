package dam.pmdm.spyrothedragon.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import dam.pmdm.spyrothedragon.MainActivity;
import dam.pmdm.spyrothedragon.R;
import dam.pmdm.spyrothedragon.databinding.FragmentGuideBinding;

public class GuideFragment extends Fragment {

    private FragmentGuideBinding binding;
    private MainActivity mainActivity;
    private int clickCount = 0;

    public GuideFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentGuideBinding.inflate(inflater, container, false);
        mainActivity = (MainActivity) getActivity();
        int statusBarHeight = getStatusBarHeight();
        PaintView paintView = binding.roundedRectangle;

        // Recorre los diferentes pasos de la guía
        showNextStep(statusBarHeight, paintView, R.id.nav_characters);

        binding.buttonSaltar.setOnClickListener(v -> {
            // Al pulsar el botón se cierra la guía
            MainActivity.startAudio(getActivity(), R.raw.vamos);
            closeGuide();
        });

        paintView.setOnClickListener(v -> {
            nextStep(statusBarHeight, paintView);
        });

        binding.buttonSig.setOnClickListener(v -> {
            nextStep(statusBarHeight, paintView);
        });

        return binding.getRoot();
    }

    private void nextStep(int statusBarHeight, PaintView paintView) {
        clickCount++;
        switch (clickCount) {
            case 1: // Mundos
                showNextStep(statusBarHeight, paintView, R.id.nav_worlds);
                mainActivity.getBinding().navView.setSelectedItemId(R.id.nav_worlds);
                break;
            case 2: // Coleccionables
                showNextStep(statusBarHeight, paintView, R.id.nav_collectibles);
                mainActivity.getBinding().navView.setSelectedItemId(R.id.nav_collectibles);
                break;
            case 3: // Icono info
                showNextStep(statusBarHeight, paintView, R.id.action_info);
                binding.buttonSig.setText(R.string.guide_fin);
                break;
            case 4: // Resumen de la guía
                // Parar la animación del rectángulo si aun está activa
                if (paintView.getAnimation() != null && paintView.getAnimation().isInitialized()) {
                    paintView.getAnimation().cancel();
                }

                // Ocultar el rectángulo
                paintView.setVisibility(View.GONE);

                binding.includeLayout.guideResume.setVisibility(View.VISIBLE);

                ImageView logoSpyro = binding.getRoot().findViewById(R.id.logoSpyro);
                logoSpyro.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.scale_rotale));

                ImageView gemView = binding.getRoot().findViewById(R.id.diamondImage);
                gemView.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.scale_pulse));

                gemView.setOnClickListener(view -> {
                    MainActivity.startAudio(getActivity(), R.raw.vamos);
                    view.clearAnimation();
                    closeGuide();
                });
                break;
        }

        MainActivity.startAudio(getActivity(), R.raw.efecto);
    }

    private void closeGuide() {
        binding.includeLayout.guideResume.setVisibility(View.GONE);
        mainActivity.getBinding().fullScreenFragmentContainer.setVisibility(View.GONE);
        mainActivity.getBinding().includeLayout.guideWelcome.setVisibility(View.GONE);
        if (clickCount > 0)
            mainActivity.getBinding().navView.setSelectedItemId(R.id.nav_characters);
        getParentFragmentManager().beginTransaction().remove(this).commit();
    }

    private void showNextStep(int statusBarHeight, PaintView paintView, int idView) {
        if (paintView.getAnimation() != null && paintView.getAnimation().isInitialized())
            paintView.getAnimation().cancel();
        paintView.setVisibility(View.INVISIBLE);

        switch (clickCount) {
            case 0: // Personajes
                binding.text.setText(getString(R.string.guide_combined,
                        getString(R.string.guide_inf_pers),
                        getString(R.string.guide_inf_siguiente)));
                break;
            case 1: // Mundos
                binding.text.setText(getString(R.string.guide_combined,
                        getString(R.string.guide_inf_mundo),
                        getString(R.string.guide_inf_siguiente)));
                break;
            case 2: // Coleccionables
                binding.text.setText(getString(R.string.guide_combined,
                        getString(R.string.guide_inf_collec),
                        getString(R.string.guide_inf_siguiente)));
                break;
            case 3: // Icono info
                binding.text.setText(getString(R.string.guide_inf_acerca));
                break;
        }

        // Animación de las vistas
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(binding.text, "scaleX", 0f, 1.0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(binding.text, "scaleY", 0f, 1.0f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(binding.buttonSaltar, "alpha", 0f, 1.0f);
        ObjectAnimator alpha2 = ObjectAnimator.ofFloat(binding.buttonSig, "alpha", 0f, 1.0f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY, alpha, alpha2);
        animatorSet.setDuration(1000);

        animatorSet.start();

        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                displayRectPhase(statusBarHeight, paintView, idView);
            }
        });

    }

    private void displayRectPhase(int statusBarHeight, PaintView paintView, int idView) {
        // Obtener coordenadas y tamaño del elemento a resaltar
        View targetView = requireActivity().findViewById(idView);
        int[] location = new int[2];
        targetView.getLocationOnScreen(location);
        float targetX = location[0];
        float targetY = location[1];
        float targetWidth = targetView.getWidth();
        float targetHeight = targetView.getHeight();

        // Definir el tamaño y posición del resaltado
        float right = targetX + targetWidth;
        float bottom = targetY + targetHeight - statusBarHeight;

        // Actualizar las propiedades de la vista en función del elemento a resaltar
        paintView.setRectangleBounds(targetX, targetY - statusBarHeight, right, bottom);

        paintView.setVisibility(View.VISIBLE);

        // Animación de resaltado
        paintView.setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fade_emerge));
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}