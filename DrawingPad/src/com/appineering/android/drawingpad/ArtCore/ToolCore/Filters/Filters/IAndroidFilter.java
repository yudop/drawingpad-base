package com.appineering.android.drawingpad.ArtCore.ToolCore.Filters.Filters;

import com.appineering.android.drawingpad.ArtCore.ToolCore.Filters.Core.AndroidImage;

public interface IAndroidFilter {

	public AndroidImage process(AndroidImage imageIn);
}
