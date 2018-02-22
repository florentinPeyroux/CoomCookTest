package com.eservices.floamnx.coomcook.splashSceen;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.eservices.floamnx.coomcook.R;
import com.eservices.floamnx.coomcook.authentification.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SplashScreensActivity extends AppCompatActivity implements View.OnClickListener{

	private KenBurnsView mKenBurns;
	private ImageView mLogo;
	private TextView welcomeText;
	FirebaseAuth firebaseAuth;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);

		firebaseAuth = FirebaseAuth.getInstance();
		firebaseAuth.signOut();

		mKenBurns = (KenBurnsView) findViewById(R.id.ken_burns_images);
		mLogo = (ImageView) findViewById(R.id.logo);
		welcomeText = (TextView) findViewById(R.id.welcome_text);

		mKenBurns.setOnClickListener(this);
		setAnimation();
	}

	private void setAnimation() {
		mKenBurns.setImageResource(R.drawable.splash_screen);
		mLogo.setAlpha(1.0F);
		Animation anim = AnimationUtils.loadAnimation(this, R.anim.translate_top_to_center);
		mLogo.startAnimation(anim);
		ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(welcomeText, "alpha", 0.0F, 1.0F);
		alphaAnimation.setStartDelay(1700);
		alphaAnimation.setDuration(500);
		alphaAnimation.start();

	}

	@Override
	public void onClick(View view) {
		if (view == mKenBurns){
			startActivity(new Intent(getApplicationContext(), LoginActivity.class));
		}
	}
}
