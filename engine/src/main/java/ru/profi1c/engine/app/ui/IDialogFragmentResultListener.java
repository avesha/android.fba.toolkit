package ru.profi1c.engine.app.ui;

/**
 * This interface must be implemented by activities that contain this fragment
 */
public interface IDialogFragmentResultListener {

    /**
     * Triggered when the user successfully select action in dialog
     *
     * @param dialog The dialog that received the action.
     * @param data   Custom user data, for example: the button that was clicked (BUTTON_POSITIVE,
     *               BUTTON_NEGATIVE or BUTTON_NEUTRAL) or the position of the item clicked.
     */
    void onDialogFragmentResult(BaseDialogFragment dialog, Object data);

}
