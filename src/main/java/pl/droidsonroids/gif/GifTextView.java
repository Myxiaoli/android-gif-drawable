package pl.droidsonroids.gif;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

import java.io.IOException;

/**
 * A {@link TextView} which handles GIFs as compound drawables. NOTE:
 * {@code android:drawableStart} and {@code android:drawableEnd} from XML are
 * not supported but can be set using
 * {@link #setCompoundDrawablesRelativeWithIntrinsicBounds(int, int, int, int)}
 *
 * @author koral--
 */
public class GifTextView extends TextView {

    /**
     * A corresponding superclass constructor wrapper.
     *
     * @param context
     */
    public GifTextView(Context context) {
        super(context);
    }

    /**
     * Like equivalent from superclass but also try to interpret compound drawables defined in XML
     * attributes as GIFs.
     *
     * @param context
     * @param attrs
     * @see TextView#TextView(Context, AttributeSet)
     */
    public GifTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        parseAttrs(attrs);
    }

    /**
     * Like equivalent from superclass but also try to interpret compound drawables defined in XML
     * attributes as GIFs.
     *
     * @param context
     * @param attrs
     * @param defStyle
     * @see TextView#TextView(Context, AttributeSet, int)
     */
    public GifTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        parseAttrs(attrs);
    }

    /**
     * Like equivalent from superclass but also try to interpret compound drawables defined in XML
     * attributes as GIFs.
     *
     * @param context
     * @param attrs
     * @param defStyle
     * @param defStyleRes
     * @see TextView#TextView(Context, AttributeSet, int, int)
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public GifTextView(Context context, AttributeSet attrs, int defStyle, int defStyleRes) {
        super(context, attrs, defStyle, defStyleRes);
        parseAttrs(attrs);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void parseAttrs(AttributeSet attrs) {
        if (attrs != null) {
            Drawable left = getGifOrDefaultDrawable(attrs.getAttributeResourceValue(GifImageView.ANDROID_NS, "drawableLeft", 0));
            Drawable right = getGifOrDefaultDrawable(attrs.getAttributeResourceValue(GifImageView.ANDROID_NS, "drawableRight", 0));
            Drawable top = getGifOrDefaultDrawable(attrs.getAttributeResourceValue(GifImageView.ANDROID_NS, "drawableTop", 0));
            Drawable bottom = getGifOrDefaultDrawable(attrs.getAttributeResourceValue(GifImageView.ANDROID_NS, "drawableBottom", 0));
            Drawable start = getGifOrDefaultDrawable(attrs.getAttributeResourceValue(GifImageView.ANDROID_NS, "drawableStart", 0));
            Drawable end = getGifOrDefaultDrawable(attrs.getAttributeResourceValue(GifImageView.ANDROID_NS, "drawableEnd", 0));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                if (getLayoutDirection() == LAYOUT_DIRECTION_LTR)
                {
                    if (start == null)
                        start = left;
                    if (end == null)
                        end = right;
                }
                else {
                    if (start == null)
                        start = right;
                    if (end == null)
                        end = left;
                }
                setCompoundDrawablesRelativeWithIntrinsicBounds(start, top, end, bottom);
            }
            else
            {
                setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
            }
            setBackgroundInternal(getGifOrDefaultDrawable(attrs.getAttributeResourceValue(GifImageView.ANDROID_NS, "background", 0)));
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    // setBackground
    @SuppressWarnings("deprecation")
    // setBackgroundDrawable
    private void setBackgroundInternal(Drawable bg) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setBackground(bg);
        } else {
            setBackgroundDrawable(bg);
        }
    }

    @Override
    public void setBackgroundResource(int resid) {
        setBackgroundInternal(getGifOrDefaultDrawable(resid));
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP) //Resources#getDrawable(int, Theme)
    @SuppressWarnings("deprecation") //Resources#getDrawable(int)
    private Drawable getGifOrDefaultDrawable(int resId) {
        if (resId == 0) {
            return null;
        }
        final Resources resources = getResources();
        if (!isInEditMode() && "drawable".equals(resources.getResourceTypeName(resId))) {
            try {
                return new GifDrawable(resources, resId);
            } catch (IOException | NotFoundException ignored) {
                // ignored
            }
        }
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP)
            return resources.getDrawable(resId, getContext().getTheme());
        else
            return resources.getDrawable(resId);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void setCompoundDrawablesRelativeWithIntrinsicBounds(int start, int top, int end, int bottom) {
        setCompoundDrawablesRelativeWithIntrinsicBounds(getGifOrDefaultDrawable(start), getGifOrDefaultDrawable(top), getGifOrDefaultDrawable(end), getGifOrDefaultDrawable(bottom));
    }

    @Override
    public void setCompoundDrawablesWithIntrinsicBounds(int left, int top, int right, int bottom) {
        setCompoundDrawablesWithIntrinsicBounds(getGifOrDefaultDrawable(left), getGifOrDefaultDrawable(top), getGifOrDefaultDrawable(right), getGifOrDefaultDrawable(bottom));
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        hideCompoundDrawables(getCompoundDrawables());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            hideCompoundDrawables(getCompoundDrawablesRelative());
        }
    }

    private void hideCompoundDrawables(Drawable[] drawables) {
        for (Drawable d : drawables) {
            if (d != null) {
                d.setVisible(false, false);
            }
        }
    }
}
