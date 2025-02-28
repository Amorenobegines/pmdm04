package dam.pmdm.spyrothedragon;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.annotation.NonNull;

public class FlameView extends View {

    private final Paint flamePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path flamePath = new Path();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean isAnimating = false;
    private LinearGradient gradient;

    public FlameView(Context context) {
        super(context);
        setWillNotDraw(false);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        if (width == 0 || height == 0) return;

        flamePaint.setShader(gradient);

        flamePath.reset();
        flamePath.moveTo(width / 2f, height);
        flamePath.quadTo(width * 0.2f, height * 0.5f + (float) Math.random() * 20, width / 2f, height * 0.1f + (float) Math.random() * 30);
        flamePath.quadTo(width * 0.8f, height * 0.5f + (float) Math.random() * 20, width / 2f, height);

        canvas.drawPath(flamePath, flamePaint);
    }

    // Iniciar la animación
    public void startAnimation() {
        if (isAnimating) return;
        isAnimating = true;
        handler.post(animationRunnable);
    }

    // Detener la animación
    public void stopAnimation() {
        isAnimating = false;
        handler.removeCallbacks(animationRunnable);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        gradient = new LinearGradient(0, h, 0, 0,
                new int[]{Color.RED, Color.YELLOW, Color.TRANSPARENT},
                new float[]{0.2f, 0.6f, 1f},
                Shader.TileMode.CLAMP);
    }

    // Runnable para la animación
    private final Runnable animationRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isAnimating) return;
            invalidate();
            handler.postDelayed(this, 100);
        }
    };
}
