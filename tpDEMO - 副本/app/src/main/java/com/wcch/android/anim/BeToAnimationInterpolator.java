package com.wcch.android.anim;

import android.view.animation.Interpolator;

/**
 * @author created by Lzq
 * @time：2021/8/4
 * @Des：
 */
public class BeToAnimationInterpolator implements Interpolator {

    private int animType = 0;

    public BeToAnimationInterpolator(int animType) {
        this.animType = animType;
    }

    /**
     * Maps a value representing the elapsed fraction of an animation to a value that represents
     * the interpolated fraction. This interpolated value is then multiplied by the change in
     * value of an animation to derive the animated value at the current elapsed animation time.
     *
     * @param input A value between 0 and 1.0 indicating our current point
     *              in the animation where 0 represents the start and 1.0 represents
     *              the end
     * @return The interpolation value. This value can be more than 1.0 for
     * interpolators which overshoot their targets, or less than 0 for
     * interpolators that undershoot their targets.
     */
    @Override
    public float getInterpolation(float input) {
        if (animType == 0) {
            return (float)(Math.cos((input + 1) * Math.PI) / 2.0f) + 0.5f;
        } else if (animType == 1) {
            return 0;
        } else if (animType == 2) {
            return 0;
        } else {
            return 0;
        }
    }
}
