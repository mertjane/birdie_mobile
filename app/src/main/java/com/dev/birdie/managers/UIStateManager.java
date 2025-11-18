package com.dev.birdie.managers;

import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.dev.birdie.R;
import com.dev.birdie.fragments.LoadingDialogFragment;
import com.google.android.material.button.MaterialButton;

/**
 * Manages UI state for login/authentication screens
 */
public class UIStateManager {

    private static final String LOADING_DIALOG_TAG = "LoadingDialog";

    private final ProgressBar progressBar;
    private final MaterialButton actionButton;
    private final FragmentManager fragmentManager;
    private final String defaultButtonText;

    private LoadingDialogFragment loadingDialog;

    public UIStateManager(AppCompatActivity activity,
                          ProgressBar progressBar,
                          MaterialButton actionButton,
                          String defaultButtonText) {
        this.progressBar = progressBar;
        this.actionButton = actionButton;
        this.fragmentManager = activity.getSupportFragmentManager();
        this.defaultButtonText = defaultButtonText;
    }

    /**
     * Shows loading state (progress bar, disables button)
     */
    public void showLoading() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }

        if (actionButton != null) {
            actionButton.setEnabled(false);
            actionButton.setText("");
        }
    }

    /**
     * Hides loading state (progress bar, enables button)
     */
    public void hideLoading() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }

        if (actionButton != null) {
            actionButton.setEnabled(true);
            actionButton.setText(defaultButtonText);
        }
    }

    /**
     * Shows loading dialog with message
     */
    public void showLoadingDialog(String message) {
        hideLoadingDialog(); // Hide any existing dialog first
        loadingDialog = LoadingDialogFragment.newInstance(message);
        loadingDialog.show(fragmentManager, LOADING_DIALOG_TAG);
    }

    /**
     * Hides loading dialog
     */
    public void hideLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isAdded()) {
            loadingDialog.dismissAllowingStateLoss();
            loadingDialog = null;
        }
    }

    /**
     * Updates loading dialog message
     */
    public void updateLoadingMessage(String message) {
        if (loadingDialog != null && loadingDialog.isAdded()) {
            loadingDialog.updateMessage(message);
        }
    }

    /**
     * Shows progress bar only
     */
    public void showProgressBar() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Hides progress bar only
     */
    public void hideProgressBar() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    /**
     * Cleanup method - call in onDestroy
     */
    public void cleanup() {
        hideLoadingDialog();
    }
}