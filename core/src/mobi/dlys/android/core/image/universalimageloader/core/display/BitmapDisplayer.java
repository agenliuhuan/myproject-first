/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package mobi.dlys.android.core.image.universalimageloader.core.display;

import mobi.dlys.android.core.image.universalimageloader.core.assist.LoadedFrom;
import mobi.dlys.android.core.image.universalimageloader.core.imageaware.ImageAware;
import android.graphics.Bitmap;

/**
 * Displays {@link Bitmap} in {@link mobi.dlys.android.core.image.universalimageloader.core.imageaware.ImageAware}. Implementations can
 * apply some changes to Bitmap or any animation for displaying Bitmap.<br />
 * Implementations have to be thread-safe.
 *
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @see mobi.dlys.android.core.image.universalimageloader.core.imageaware.ImageAware
 * @see mobi.dlys.android.core.image.universalimageloader.core.assist.LoadedFrom
 * @since 1.5.6
 */
public interface BitmapDisplayer {
	/**
	 * Displays bitmap in {@link mobi.dlys.android.core.image.universalimageloader.core.imageaware.ImageAware}.
	 * <b>NOTE:</b> This method is called on UI thread so it's strongly recommended not to do any heavy work in it.
	 *
	 * @param bitmap     Source bitmap
	 * @param imageAware {@linkplain mobi.dlys.android.core.image.universalimageloader.core.imageaware.ImageAware Image aware view} to
	 *                   display Bitmap
	 * @param loadedFrom Source of loaded image
	 */
	void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom);
}
