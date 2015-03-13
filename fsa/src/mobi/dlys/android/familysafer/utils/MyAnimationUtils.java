package mobi.dlys.android.familysafer.utils;

import mobi.dlys.android.familysafer.R;
import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;

public class MyAnimationUtils {

	public static void hideBottomView(final View rootview, final View v, final Context context) {
		Animation anim = AnimationUtils.loadAnimation(context, R.anim.view_translate_bottom_out);
		anim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				v.setVisibility(View.GONE);
				Animation anim2 = AnimationUtils.loadAnimation(context, R.anim.view_alpha_out);
				anim2.setAnimationListener(new AnimationListener() {

					@Override
					public void onAnimationStart(Animation animation) {

					}

					@Override
					public void onAnimationEnd(Animation animation) {
						rootview.setVisibility(View.GONE);

					}

					@Override
					public void onAnimationRepeat(Animation animation) {

					}
				});
				rootview.startAnimation(anim2);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}
		});

		v.startAnimation(anim);

	}

	public static void showBottomView(final View rootview, final View v, Context context) {
		Animation anim = AnimationUtils.loadAnimation(context, R.anim.view_translate_bottom_in);
		v.startAnimation(anim);
		v.setVisibility(View.VISIBLE);

		Animation anim2 = AnimationUtils.loadAnimation(context, R.anim.view_alpha_in);
		rootview.startAnimation(anim2);
		rootview.setVisibility(View.VISIBLE);

	}

	public static void hideRightView(final View rootview, final View v, Context context) {
		Animation anim = AnimationUtils.loadAnimation(context, R.anim.translate_between_interface_right_out);
		v.startAnimation(anim);
		v.setVisibility(View.GONE);

		Animation anim2 = AnimationUtils.loadAnimation(context, R.anim.view_alpha_out);
		rootview.startAnimation(anim2);
		rootview.setVisibility(View.GONE);
	}

	public static void showRightView(final View rootview, final View v, Context context) {
		Animation anim = AnimationUtils.loadAnimation(context, R.anim.translate_between_interface_right_in);
		v.startAnimation(anim);
		v.setVisibility(View.VISIBLE);

		Animation anim2 = AnimationUtils.loadAnimation(context, R.anim.view_alpha_in);
		rootview.startAnimation(anim2);
		rootview.setVisibility(View.VISIBLE);

	}

}
