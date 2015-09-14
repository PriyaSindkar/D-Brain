package com.webmyne.android.d_brain.ui.Helpers;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;


import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;


public class AnimationHelper {
    private Context mContext;
    private AnimatorSet animatorSet;

    public static PopupAnimationEnd animInterfaceObj;

    public void setInterFaceObj(PopupAnimationEnd obj){
        this.animInterfaceObj = obj;
    }

    public AnimationHelper(){
    }

    public AnimatorSet getAnimatorSet() {
        return animatorSet;
    }

    public void initPowerButtonAnimation(ImageView btn) {
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(btn, "scaleX", 1f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(btn, "scaleY", 1f);

        scaleDownX.setDuration(1000);
        scaleDownY.setDuration(1000);

        Animator animator = ObjectAnimator.ofFloat(btn,"alpha",1f,0.3f);
        animator.setDuration(1000);

        animatorSet = new AnimatorSet();
        animatorSet.play(animator).with(scaleDownX).with(scaleDownY);
    }

    public void startPowerButtonAnimation() {
        animatorSet.start();
    }
    public void cancelPowerButtonAnimation() {
        animatorSet.cancel();
    }

    public void rotateViewClockwise(ImageView imageview){
        ObjectAnimator imageViewObjectAnimator = ObjectAnimator.ofFloat(imageview , "rotation", 0f, 180f);
        imageViewObjectAnimator.setDuration(500);
        imageViewObjectAnimator.start();
    }

    public void rotateViewAntiClockwise(ImageView imageview){
        ObjectAnimator imageViewObjectAnimator = ObjectAnimator.ofFloat(imageview, "rotation", 180f, 360f);
        imageViewObjectAnimator.setDuration(500);
        imageViewObjectAnimator.start();
    }

    public void viewPopUpMenu(LinearLayout mRevealView) {
        int cx = (mRevealView.getLeft() + mRevealView.getRight());
        int cy = mRevealView.getBottom();
        //int cy = (mRevealView.getTop() + mRevealView.getBottom());
        int radius = Math.max(mRevealView.getWidth(), mRevealView.getHeight());

        SupportAnimator popUpAnimation = ViewAnimationUtils.createCircularReveal(mRevealView, cx, cy, 0, radius);
        popUpAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        popUpAnimation.setDuration(500);
        popUpAnimation.start();
    }


    public  void closePopUpMenu(LinearLayout mRevealView) {

        int cx = (mRevealView.getLeft() + mRevealView.getRight());
        int cy = mRevealView.getBottom();
        //int cy = (mRevealView.getTop() + mRevealView.getBottom());
        int radius = Math.max(mRevealView.getWidth(), mRevealView.getHeight());

        SupportAnimator popUpAnimation = ViewAnimationUtils.createCircularReveal(mRevealView, cx, cy, 0, radius);
        popUpAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        popUpAnimation.setDuration(500);

        SupportAnimator animator_reverse = popUpAnimation.reverse();
        animator_reverse.start();

        animator_reverse.addListener(new SupportAnimator.AnimatorListener() {
            @Override
            public void onAnimationStart() {

            }

            @Override
            public void onAnimationEnd() {
                animInterfaceObj.animationCompleted();
            }

            @Override
            public void onAnimationCancel() {

            }

            @Override
            public void onAnimationRepeat() {

            }
        });
    }

    public void rotateViewClockwiseLeftToUp(ImageView imageview){
        ObjectAnimator imageViewObjectAnimator = ObjectAnimator.ofFloat(imageview, "rotation", 0f, 90f);
        imageViewObjectAnimator.setDuration(300);
        imageViewObjectAnimator.start();
    }

    public void rotateViewAntiClockwiseLeftToUp(ImageView imageview){
        ObjectAnimator imageViewObjectAnimator = ObjectAnimator.ofFloat(imageview, "rotation", 90f, 0f);
        imageViewObjectAnimator.setDuration(300);
        imageViewObjectAnimator.start();
    }

    public void viewPopUpMenuFromBottomLeft(LinearLayout mRevealView) {
        int cx = mRevealView.getBottom();
        int cy = (mRevealView.getLeft() + mRevealView.getRight());
        int radius = Math.max(mRevealView.getWidth(), mRevealView.getHeight());

        SupportAnimator popUpAnimation = ViewAnimationUtils.createCircularReveal(mRevealView, cx, cy, 0, radius);
        popUpAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        popUpAnimation.setDuration(200);
        popUpAnimation.start();
    }

    public  void closePopUpMenuFromBottomLeft(LinearLayout mRevealView) {

        int cx = mRevealView.getTop();
        int cy = (mRevealView.getLeft() + mRevealView.getRight());

        int radius = Math.max(mRevealView.getWidth(), mRevealView.getHeight());

        SupportAnimator popUpAnimation = ViewAnimationUtils.createCircularReveal(mRevealView, cx, cy, 0, radius);
        popUpAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        popUpAnimation.setDuration(200);

        SupportAnimator animator_reverse = popUpAnimation.reverse();
        animator_reverse.start();

        animator_reverse.addListener(new SupportAnimator.AnimatorListener() {
            @Override
            public void onAnimationStart() {

            }

            @Override
            public void onAnimationEnd() {
                animInterfaceObj.animationCompleted();
            }

            @Override
            public void onAnimationCancel() {

            }

            @Override
            public void onAnimationRepeat() {

            }
        });
    }

}
