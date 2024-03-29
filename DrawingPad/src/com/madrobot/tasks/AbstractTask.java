/*******************************************************************************
 * Copyright (c) 2011 MadRobot.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *  Elton Kent - initial API and implementation
 ******************************************************************************/
package com.madrobot.tasks;

import android.content.Context;
import android.os.AsyncTask;

abstract class AbstractTask extends AsyncTask<Object, Object, Object> {
	protected TaskNotifier notifier;
	protected Context appContext;

	AbstractTask(Context context, TaskNotifier notifier) {
		this.notifier = notifier;
		this.appContext = context;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
	}

	public TaskNotifier getNotifier() {
		return notifier;
	}

	public void setNotifier(TaskNotifier notifier) {
		this.notifier = notifier;
	}

	public Context getAppContext() {
		return appContext;
	}

	public void setAppContext(Context appContext) {
		this.appContext = appContext;
	}
	
	protected void onProgressUpdate(Object... objects) {
		/*
		 * if null is received then it indicates that the task has just started
		 */
		if(objects == null){
			notifier.onTaskStarted();
		} else{
			DataResponse response = (DataResponse) objects[0];
			if(response.getResponseStatus() > 0){
				notifier.onSuccess(response);
			} else{
				notifier.onError(response.getT());
			}
		}
	}
	
	protected void taskStarted(){
		notifier.onTaskStarted();
	}
}
