package ru.profi1c.engine.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.lang.ref.WeakReference;

import ru.profi1c.engine.Dbg;

public final class BroadcastManagerHelper {
	private static final String TAG = BroadcastManagerHelper.class.getSimpleName();
	private static final boolean DEBUG = Dbg.DEBUG;

	private final Context mContext;
	private final IntentFilter mFilter;
	private final WeakReference<OnReceiveListener> mRefListener;

	private boolean mRegistered;

	private BroadcastManagerHelper(Context context, IntentFilter filter,
			OnReceiveListener listener) {
		mContext = context;
		mFilter = filter;
		mRefListener = new WeakReference<OnReceiveListener>(listener);
	}

	@Override
	protected void finalize() throws Throwable {
		try {
			unregisterReceiver();
		} finally {
			super.finalize();
		}

	}

	/**
	 * @return The first sticky intent found that matches filter, or null if there are none.
	 */
	public Intent registerReceiver() {
		Intent i = mContext.registerReceiver(mReceiver, mFilter);
		mRegistered = true;
		return i;
	}

	public void unregisterReceiver() {
		if (mRegistered) {
			mContext.unregisterReceiver(mReceiver);
			mRegistered = false;
		}
	}

	/**
	 * Recipient of the broadcast notification
	 */
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (DEBUG) {
				Dbg.d(TAG, "onReceive, action = %s", action);
			}
			OnReceiveListener listener = mRefListener.get();
			if (listener != null) {
				listener.onReceive(action, intent);
			}
		}
	};

	/************************************* Inner classes *****************************************/

	public interface OnReceiveListener {
		void onReceive(String action, Intent data);
	}

	public static class Builder {

		private final Context mContext;
		private final IntentFilter iFilter;
		private OnReceiveListener mListener;

		public Builder(Context context) {
			mContext = context.getApplicationContext();
			iFilter = new IntentFilter();
		}

		public Builder addAction(final String action) {
			iFilter.addAction(action);
			return this;
		}

		public Builder setListener(OnReceiveListener listener) {
			mListener = listener;
			return this;
		}

		public BroadcastManagerHelper create() {
			return new BroadcastManagerHelper(mContext, iFilter, mListener);
		}
	}
}
