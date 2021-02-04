package com.liferiet.shoppinglists.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableInt;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.liferiet.shoppinglists.R;
import com.liferiet.shoppinglists.databinding.DialogCreateListBinding;

public class CreateListDialogFragment extends DialogFragment {

    private static String TAG = CreateListDialogFragment.class.getSimpleName();
    private CreateListDialogListener listener;
    private TextInputEditText listNameEditText;
    private TextInputLayout textInputLayout;

    public interface CreateListDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(inflater.inflate(R.layout.dialog_create_list, null))
                .setPositiveButton("CREATE", null);
        // Create the AlertDialog object and return it
        Log.d(TAG, "onCreateDialog");
        return builder.create();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        DialogCreateListBinding binding = DialogCreateListBinding.inflate(inflater, container, false);
        listNameEditText = binding.dialogListName;
        textInputLayout = binding.textInputLayout;
        Log.d(TAG, "onCreateView");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (CreateListDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement NoticeDialogListener");
        }

    }

    @Override
    public void onStart() {
        super.onStart();

        final AlertDialog d = (AlertDialog)getDialog();
        if(d != null)
        {
            Button positiveButton = d.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view) {
                    Log.d("FAB_CLICK", "yeeey correct On click Event");
                    String value = listNameEditText.getText().toString();
                    if (value.isEmpty()){
                        Log.d(TAG, "onClick, empty value");
                        textInputLayout.setError("Nazwa nie moze byc pusta");


                    } else {
                        Log.d(TAG, "onClick, value: " + value);
                        //onDialogPositiveClick(CreateListDialogFragment.this);
                        d.dismiss();
                    }
                }
            });
        }

    }

    @Override
    public void onResume() {
        super.onResume();


    }

}
