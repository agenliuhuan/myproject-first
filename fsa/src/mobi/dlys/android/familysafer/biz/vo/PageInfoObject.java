package mobi.dlys.android.familysafer.biz.vo;

import mobi.dlys.android.core.mvc.BaseObject;
import mobi.dlys.android.familysafer.protobuf.FamilySaferProtobuf.FamilySaferPb.PageInfo;

import com.j256.ormlite.field.DatabaseField;

public class PageInfoObject extends BaseObject {
	private static final long serialVersionUID = -4402706989626265897L;

	// 类型ID (家人列表，家人请求列表，通知列表(弃用)，线索列表, 我的求救列表，家人求救通知，家人到达通知，已确认通知）
	@DatabaseField(id = true)
	private int typeId;

	// 当前从服务器请求的页数
	@DatabaseField
	private int pageNo;

	// 每页大小
	@DatabaseField
	private int pageSize;

	// 是否最后一页
	@DatabaseField
	private boolean lastPage;

	// 总页数
	@DatabaseField
	private int totalPage;

	// 总记录数
	@DatabaseField
	private int totalResult;

	// 当前从缓存读取的页数
	@DatabaseField
	private int readCachePageNo;
	
	// 通知类型
	@DatabaseField
	private int eventType;

	public static PageInfoObject createFromPb(PageInfo pageInfo) {
		PageInfoObject pageInfoObject = new PageInfoObject();
		if (null != pageInfo) {
			pageInfoObject.setPageNo(pageInfo.getPageNo());
			pageInfoObject.setPageSize(pageInfo.getPageSize());
			pageInfoObject.setLastPage(pageInfo.getLastPage());
			pageInfoObject.setTotalPage(pageInfo.getTotalPage());
			pageInfoObject.setTotalResult(pageInfo.getTotalResult());

			if (1 == pageInfo.getPageNo()) {
				pageInfoObject.setReadCachePageNo(1);
			}
		}

		return pageInfoObject;
	}

	public static void updatePageInfo(PageInfoObject pageInfoObject, PageInfo pageInfo) {
		if (null == pageInfoObject) {
			pageInfoObject = new PageInfoObject();
		}
		if (null != pageInfo) {
			pageInfoObject.setPageNo(pageInfo.getPageNo());
			pageInfoObject.setPageSize(pageInfo.getPageSize());
			pageInfoObject.setLastPage(pageInfo.getLastPage());
			pageInfoObject.setTotalPage(pageInfo.getTotalPage());
			pageInfoObject.setTotalResult(pageInfo.getTotalResult());

			if (1 == pageInfo.getPageNo()) {
				pageInfoObject.setReadCachePageNo(1);
			}
		}
	}

	public int getTypeId() {
		return typeId;
	}

	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public boolean isLastPage() {
		return lastPage;
	}

	public void setLastPage(boolean lastPage) {
		this.lastPage = lastPage;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public int getTotalResult() {
		return totalResult;
	}

	public void setTotalResult(int totalResult) {
		this.totalResult = totalResult;
	}

	public int getReadCachePageNo() {
		return readCachePageNo;
	}

	public void setReadCachePageNo(int readCachePageNo) {
		this.readCachePageNo = readCachePageNo;
	}

	public int getEventType() {
		return eventType;
	}

	public void setEventType(int eventType) {
		this.eventType = eventType;
	}

	
}
