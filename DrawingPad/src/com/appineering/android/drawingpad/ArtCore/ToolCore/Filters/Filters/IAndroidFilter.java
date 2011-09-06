package com.appineering.android.drawingpad.ArtCore.ToolCore.Filters.Filters;

import com.appineering.android.drawingpad.ArtCore.ToolCore.Filters.Core.WorkingImage;

public interface IAndroidFilter {

	public WorkingImage process(WorkingImage imageIn);
}
