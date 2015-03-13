package mobi.dlys.android.familysafer.biz.vo.event;

import mobi.dlys.android.core.mvc.BaseObject;

import com.j256.ormlite.field.DatabaseField;

public class SOSVoice extends BaseObject {
	private static final long serialVersionUID = -2807472280148261704L;

	@DatabaseField(id = true)
	public int voiceSosId;

	@DatabaseField
	public String voiceUri;

	@DatabaseField
	public String voiceUrl;

	@DatabaseField
	public String filename;

	@DatabaseField
	public boolean isValid;

	@DatabaseField
	public int duration;
}
