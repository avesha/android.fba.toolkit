package ru.profi1c.engine.app.ui;

import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog.Builder;

import java.util.List;

import ru.profi1c.engine.Const;

public class AlertDialogFragment extends BaseAlertDialogFragment {

	public static final int DIALOG_ID_NOT_SPECIFIED = Const.NOT_SPECIFIED;

    public static void show(FragmentActivity activity, int dialogId, CharSequence title,
            CharSequence message, String positiveButtonText, boolean cancelable) {

		ArgumentsBuilder builder = new ArgumentsBuilder(activity, dialogId)
				.setTitle(title)
				.setMessage(message)
				.setPositiveButton(positiveButtonText)
				.setCancelable(cancelable);

		show(activity, AlertDialogFragment.class, builder.create());
	}

	public static void show(FragmentActivity activity, int dialogId, CharSequence title,
            CharSequence message, String positiveButtonText, String negativeButtonText,
            boolean cancelable) {

		ArgumentsBuilder builder = new ArgumentsBuilder(activity, dialogId)
				.setTitle(title)
				.setMessage(message)
				.setPositiveButton(positiveButtonText)
				.setNegativeButton(negativeButtonText)
				.setCancelable(cancelable);

		show(activity, AlertDialogFragment.class, builder.create());
	}

    public static void show(FragmentActivity activity, int dialogId, CharSequence title,
            CharSequence message, String positiveButtonText, String negativeButtonText,
            String neutralButtonText, boolean cancelable) {

		ArgumentsBuilder builder = new ArgumentsBuilder(activity, dialogId)
				.setTitle(title)
				.setMessage(message)
				.setPositiveButton(positiveButtonText)
				.setNegativeButton(negativeButtonText)
				.setNeutralButton(neutralButtonText)
				.setCancelable(cancelable);

		show(activity, AlertDialogFragment.class, builder.create());
	}

    public static void showsSingleChoiceItems(FragmentActivity activity, int dialogId,
            CharSequence title, List<? extends IDescription> items, int checkedItem) {

		ArgumentsBuilder builder = new ArgumentsBuilder(activity, dialogId)
				.setTitle(title)
				.setSingleChoiceItems(items, checkedItem)
				.setCancelable(false);

		show(activity, AlertDialogFragment.class, builder.create());
	}

    public static void showContextMenu(FragmentActivity activity, int dialogId, CharSequence title,
            List<? extends IActionItem> items, int layoutResId) {

		ArgumentsBuilder builder = new ArgumentsBuilder(activity, dialogId)
				.setTitle(title)
				.setCustomActionItems(items, layoutResId)
				.setCancelable(true);

		show(activity, AlertDialogFragment.class, builder.create());
	}

	@Override
	protected void onBuildDialog(Builder builder) {

	}

}
