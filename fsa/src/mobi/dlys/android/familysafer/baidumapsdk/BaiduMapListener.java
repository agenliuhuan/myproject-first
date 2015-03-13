package mobi.dlys.android.familysafer.baidumapsdk;

import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.search.poi.PoiResult;

import mobi.dlys.android.familysafer.baidumapsdk.BaiduMapView.MyMarker;
import android.graphics.Bitmap;

//地图接口通知事件类
public interface BaiduMapListener {

	// 地图加载完毕
	public void onMapLoaded();

	// 搜索结果回调
	public void onSearched(PoiResult result);

	// 点击覆盖物
	public void onMarkerClick(MyMarker mymarker);

	// 截图完成
	public void onSnapshotReady(Bitmap snapshot);
	
	//地图被移动
	public void OnMapChanged(MapStatus arg0);
	
	//地图点击
	public void OnMapClick();
}
