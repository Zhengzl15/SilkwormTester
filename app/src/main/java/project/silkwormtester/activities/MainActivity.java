package project.silkwormtester.activities;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.nineoldandroids.view.ViewPropertyAnimator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import project.silkwormtester.R;
import project.silkwormtester.fragments.DetectionFragment;
import project.silkwormtester.fragments.OperatorFragment;
import project.silkwormtester.localdata.Config;

public class MainActivity extends FragmentActivity {
	private ArrayList<Fragment> fragments;
	private Fragment operator;
	private DetectionFragment detection;
	private ViewPager viewPager;
	private TextView tab_detection;
	private TextView tab_operator;
	private int line_width;
	private View line;

	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		pathCheck();
		tab_detection = (TextView) findViewById(R.id.tab_detection);
		tab_operator = (TextView) findViewById(R.id.tab_operator);
		line = findViewById(R.id.line);
		

		com.nineoldandroids.view.ViewPropertyAnimator.animate(tab_operator).scaleX(1.2f).setDuration(0);
		com.nineoldandroids.view.ViewPropertyAnimator.animate(tab_operator).scaleY(1.2f).setDuration(0);

		fragments = new ArrayList<Fragment>();
		operator = new OperatorFragment();
		detection = new DetectionFragment();
		detection.setTitle(tab_detection);
		fragments.add(operator);
		fragments.add(detection);
		line_width = getWindowManager().getDefaultDisplay().getWidth() / fragments.size();
		line.getLayoutParams().width = line_width;
		line.requestLayout();

		viewPager = (ViewPager) findViewById(R.id.viewPager);
		viewPager.setAdapter(new FragmentStatePagerAdapter(
				getSupportFragmentManager()) {

			@Override
			public int getCount() {
				return fragments.size();
			}

			@Override
			public Fragment getItem(int arg0) {
				return fragments.get(arg0);
			}
		});

		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				changeState(arg0);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				float tagerX = arg0 * line_width + arg2 / fragments.size();
				ViewPropertyAnimator.animate(line).translationX(tagerX)
						.setDuration(0);
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});

		tab_detection.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				viewPager.setCurrentItem(1);
				
			}
		});

		tab_operator.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				viewPager.setCurrentItem(0);
			}
		});
		viewPager.setCurrentItem(1);
	}

	/* ���ݴ����ֵ���ı�״̬ */
	private void changeState(int arg0) {
		if (arg0 == 0) {
			tab_operator.setTextColor(getResources().getColor(R.color.green));
			tab_detection.setTextColor(getResources().getColor(R.color.black));
			ViewPropertyAnimator.animate(tab_operator).scaleX(1.2f).setDuration(200);
			ViewPropertyAnimator.animate(tab_operator).scaleY(1.2f).setDuration(200);
			ViewPropertyAnimator.animate(tab_detection).scaleX(1.0f)
					.setDuration(200);
			ViewPropertyAnimator.animate(tab_detection).scaleY(1.0f)
					.setDuration(200);

		} else {
			tab_detection.setTextColor(getResources().getColor(R.color.green));
			tab_operator.setTextColor(getResources().getColor(R.color.black));
			ViewPropertyAnimator.animate(tab_operator).scaleX(1.0f).setDuration(200);
			ViewPropertyAnimator.animate(tab_operator).scaleY(1.0f).setDuration(200);
			ViewPropertyAnimator.animate(tab_detection).scaleX(1.2f)
					.setDuration(200);
			ViewPropertyAnimator.animate(tab_detection).scaleY(1.2f)
					.setDuration(200);
		}
	}

	private void pathCheck() {
		String sdPath = "";
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			sdPath = Environment.getExternalStorageDirectory().toString();
		}
		String baseDir = sdPath + Config.BASE_DIR;
		File base = new File(baseDir);
		if(!base.exists()) {
			base.mkdir();
		}
	}
	
}
