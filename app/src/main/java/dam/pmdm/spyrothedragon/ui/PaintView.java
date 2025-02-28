package dam.pmdm.spyrothedragon.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.material.circularreveal.coordinatorlayout.CircularRevealCoordinatorLayout;

import dam.pmdm.spyrothedragon.R;

public class PaintView extends View {
    private Paint paint; // Fondo
    private Paint borderPaint; // Borde
    private RectF rect;

    private float cornerRadius;
    private float borderWidth; // Grosor del borde

    public PaintView(Context context) {
        super(context);
        init(context);
    }

    public PaintView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PaintView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(ContextCompat.getColor(context, R.color.pink_transparente)); // Color del fondo por defecto

        borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint.setStyle(Paint.Style.STROKE); // Estilo de trazo para el borde
        borderPaint.setColor(ContextCompat.getColor(context, R.color.blue)); // Color del borde
        borderWidth = 5f; // Grosor del borde por defecto
        borderPaint.setStrokeWidth(borderWidth);

        rect = new RectF();
        cornerRadius = 45f; // Radio de los bordes redondeados por defecto
    }

    public void setRectangleBounds(float left, float top, float right, float bottom) {
        rect.set(0, 0, right - left, bottom - top);
        layout((int) left, (int) top, (int) right, (int) bottom);
        invalidate();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint);
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, borderPaint);
    }
}
