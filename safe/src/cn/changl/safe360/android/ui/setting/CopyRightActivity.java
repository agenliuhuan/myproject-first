package cn.changl.safe360.android.ui.setting;

import mobi.dlys.android.core.utils.ActivityUtils;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import cn.changl.safe360.android.R;
import cn.changl.safe360.android.ui.comm.BaseExActivity;
import cn.changl.safe360.android.ui.comm.TitleBarHolder;

public class CopyRightActivity extends BaseExActivity {
	protected TitleBarHolder mTitleBar;

	private ProgressBar mLoadingPb;

	public static void startActivity(Context context) {
		ActivityUtils.startActivity(context, CopyRightActivity.class);
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_copyright);
		initSubView();
		initData();
	}

	private void initData() {

	}

	private void initSubView() {
		mTitleBar = new TitleBarHolder(this);
		mTitleBar.mTitle.setText(R.string.activity_copyright_title);

		mLoadingPb = (ProgressBar) findViewById(R.id.pb_copyright_loading);
		WebView webview = (WebView) findViewById(R.id.copyRight_WebView);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.loadUrl("http://anntu.net/mobile/service.html");
		webview.setWebViewClient(new HelloWebViewClient());
		webview.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int newProgress) {
				if (100 != newProgress) {
					mLoadingPb.setVisibility(View.VISIBLE);
				}

				if (100 == newProgress) {
					mLoadingPb.setVisibility(View.INVISIBLE);
				}
			}

		});
	}

	private class HelloWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
	}

}
