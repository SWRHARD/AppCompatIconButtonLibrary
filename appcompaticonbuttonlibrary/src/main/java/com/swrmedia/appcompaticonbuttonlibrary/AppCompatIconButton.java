package com.swrmedia.appcompaticonbuttonlibrary;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.ColorInt;
import androidx.annotation.Dimension;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

public class AppCompatIconButton extends AppCompatButton {

    private Bitmap icon;
    private Paint paint;
    private Rect srcRect;
    private Rect destRect;

    private boolean textAllCaps;
    @Dimension
    private int iconPadding;
    @Dimension
    private int iconSize;
    @ColorInt
    private int iconColor;
    @DrawableRes
    private int drawableId;

    private enum Align {
        left,
        right,
        left_of_text,
        right_of_text
    }

    private Align direction;

    public AppCompatIconButton(Context context) {
        this(context, null);
    }

    public AppCompatIconButton(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.ib_style);
    }

    public AppCompatIconButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        applyStyles(context, attrs, defStyle);
        init();
    }

    private void applyStyles(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.AppCompatIconButton, defStyleAttr, R.style.AppCompatIconButtonStyle);
        textAllCaps = typedArray.getBoolean(R.styleable.AppCompatIconButton_ib_textAllCaps,
                false);
        iconPadding = typedArray.getDimensionPixelSize(
                R.styleable.AppCompatIconButton_ib_iconPadding, 0);
        iconSize = typedArray.getDimensionPixelSize(
                R.styleable.AppCompatIconButton_ib_iconSize, 0);
        iconColor = typedArray.getColor(
                R.styleable.AppCompatIconButton_ib_iconColor, 0);
        drawableId = typedArray.getResourceId(R.styleable.AppCompatIconButton_ib_iconSrc, 0);
        direction = Align.values()[typedArray.getInt(R.styleable.AppCompatIconButton_ib_iconAlign, 0)];
        typedArray.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        int dx = (iconSize + iconPadding) / 2;
        if (direction == Align.left_of_text) {
            canvas.translate(dx, 0);
        } else if (direction == Align.right_of_text){
            canvas.translate(-dx, 0);
        }

        if (icon != null) {
            float textWidth = getPaint().measureText((String) getText());
            int left = 0;
            if (direction == Align.left_of_text) {
                left = (int) ((getWidth() / 2f) - (textWidth / 2f) - iconSize - iconPadding);
            } else if (direction == Align.right_of_text) {
                left = (int) ((getWidth() / 2f) + (textWidth / 2f) + dx + iconPadding);
            } else if (direction == Align.right) {
                left = getWidth() - iconSize;
            }
            int top = getHeight() / 2 - iconSize / 2;

            destRect.set(left, top, left + iconSize, top + iconSize);
            canvas.drawBitmap(icon, srcRect, destRect, paint);
        }
        super.onDraw(canvas);
        canvas.restore();
    }

    private void init() {
        if (drawableId != 0) {
            icon = drawableToBitmap(VectorDrawableCompat.create(getResources(),
                    drawableId, null));
        }
        if (icon != null) {
            paint = new Paint();
            paint.setColorFilter(new PorterDuffColorFilter(iconColor, PorterDuff.Mode.SRC_ATOP));
            srcRect = new Rect(0, 0, icon.getWidth(), icon.getHeight());
        }
        if (textAllCaps) {
            setText(getText().toString().toUpperCase());
        }
        destRect = new Rect();
    }

    private Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
}
