/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.pets;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.pets.data.PetContract;
import com.example.android.pets.data.PetContract.PetEntry;
import com.example.android.pets.data.PetDBHelper;

import static com.example.android.pets.data.PetContract.PetEntry.COLUMN_BREED;
import static com.example.android.pets.data.PetContract.PetEntry.COLUMN_GENDER;
import static com.example.android.pets.data.PetContract.PetEntry.COLUMN_NAME;
import static com.example.android.pets.data.PetContract.PetEntry.COLUMN_WEIGHT;
import static com.example.android.pets.data.PetContract.PetEntry.CONTENT_URI;
import static com.example.android.pets.data.PetContract.PetEntry.GENDER_FEMALE;
import static com.example.android.pets.data.PetContract.PetEntry.GENDER_MALE;
import static com.example.android.pets.data.PetContract.PetEntry.GENDER_UNKNOWN;


/**
 * Allows user to create a new pet or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /** Identifier for the pet data loader */
    private static final int PET_LOADER_2 = 1;

    private PetDBHelper mDBHelper;

    /** EditText field to enter the pet's name */
    private EditText mNameEditText;

    /** EditText field to enter the pet's breed */
    private EditText mBreedEditText;

    /** EditText field to enter the pet's weight */
    private EditText mWeightEditText;

    /** EditText field to enter the pet's gender */
    private Spinner mGenderSpinner;

    /**
     * Gender of the pet. The possible values are:
     * 0 for unknown gender, 1 for male, 2 for female.
     */
    private int mGender = GENDER_UNKNOWN;
    Uri currentUri;
    // to check whether items are changed or not
    private boolean mPetHasChanged = false;
    // set touch listner on every view

    private View.OnTouchListener mTouchListner = new View.OnTouchListener(){

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mPetHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // get the uri from the intent
        currentUri =  getIntent().getData();
        if(currentUri != null){
            setTitle("Edit pet");
            // Initialize a loader to read the pet data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(PET_LOADER_2, null, this);
        }
        else{
            setTitle("Add pet");
            invalidateOptionsMenu();
        }


        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_pet_name);
        mBreedEditText = (EditText) findViewById(R.id.edit_pet_breed);
        mWeightEditText = (EditText) findViewById(R.id.edit_pet_weight);
        mGenderSpinner = (Spinner) findViewById(R.id.spinner_gender);
        mDBHelper = new PetDBHelper(this);
        mNameEditText.setOnTouchListener(mTouchListner);
        mBreedEditText.setOnTouchListener(mTouchListner);
        mWeightEditText.setOnTouchListener(mTouchListner);
        mGenderSpinner.setOnTouchListener(mTouchListner);

        setupSpinner();

    }
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deletePet();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    /**
     * Perform the deletion of the pet in the database.
     */
    private void deletePet() {
        // TODO: Implement this method
        // Defines a variable to contain the number of rows deleted
        int mRowsDeleted = 0;
        if(currentUri!=null) {
            mRowsDeleted = getContentResolver().delete(currentUri, null, null);
            if (mRowsDeleted > 0) {
                Toast.makeText(this,R.string.editor_delete_pet_successful, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this,R.string.editor_delete_pet_failed, Toast.LENGTH_SHORT).show();
            }
        }
        // Close the activity
        finish();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //return super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if(currentUri == null){
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListner){
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard,discardButtonClickListner);
        builder.setNegativeButton(R.string.keep_editing,new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if(dialog!=null){
                    dialog.dismiss();
                }
            }
        });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if (!mPetHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }


    /**
     * Setup the dropdown spinner that allows the user to select the gender of the pet.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_gender_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mGenderSpinner.setAdapter(genderSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.gender_male))) {
                        mGender = PetContract.PetEntry.GENDER_MALE; // Male
                    } else if (selection.equals(getString(R.string.gender_female))) {
                        mGender = PetContract.PetEntry.GENDER_FEMALE; // Female
                    } else {
                        mGender = GENDER_UNKNOWN; // Unknown
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mGender = GENDER_UNKNOWN; // Unknown
            }
        });
    }
    // method to insert data in database from editor values
    private void savePet(){

        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        // get the values from the edit text fields
        String petName = mNameEditText.getText().toString().trim();
        String petBreed = mBreedEditText.getText().toString().trim();

        String petWeight = mWeightEditText.getText().toString().trim();
        if (currentUri == null &&
                TextUtils.isEmpty(petName) && TextUtils.isEmpty(petBreed) &&
                TextUtils.isEmpty(petWeight) && mGender == PetEntry.GENDER_UNKNOWN) {return;}
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME,petName);
        values.put(COLUMN_BREED,petBreed);
        values.put(COLUMN_GENDER,mGender);
        int weight = 0;

        if(!TextUtils.isEmpty(petWeight)){
            weight = Integer.parseInt(petWeight);
        }

        values.put(COLUMN_WEIGHT,weight);
        if(currentUri == null) {
        // insert the data into dtabase
        Uri newUri = getContentResolver().insert(CONTENT_URI,values);

            if (newUri == null) {
                Toast.makeText(this, R.string.pet_save_fail, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.pet_save_success, Toast.LENGTH_SHORT).show();
            }
        }
        else {
            // Otherwise this is an EXISTING pet, so update the pet with content URI: mCurrentPetUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentPetUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(currentUri, values, null, null);
            if(rowsAffected == 0){
                Toast.makeText(this,"Unable to update pet",Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this,"Pet updated Successfully",Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:

                // insert data into database
                savePet();
                // return to parent activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Do nothing for now
                showDeleteConfirmationDialog();

                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                if(!mPetHasChanged) {
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }
                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListner = new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // User clicked "Discard" button, navigate to parent activity.
                        NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    }
                };
                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListner);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //Define a projection that specifies the columns from the table we care about.
                String[] projection = {
                        COLUMN_NAME,
                        COLUMN_BREED, COLUMN_GENDER, COLUMN_WEIGHT };

        return new CursorLoader(this,currentUri,projection,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
               if (data == null || data.getCount() < 1) {
                        return;
                    }
        if(data.moveToFirst()) {
            int nameColumnIndex = data.getColumnIndex(COLUMN_NAME);
            int breedColumnIndex = data.getColumnIndex(COLUMN_BREED);
            int weightColumnIndex = data.getColumnIndex(COLUMN_WEIGHT);
            String name = data.getString(nameColumnIndex);
            String breed = data.getString(breedColumnIndex);
            int weight = data.getInt(weightColumnIndex);
            mNameEditText.setText(name);
            mBreedEditText.setText(breed);
            mWeightEditText.setText(Integer.toString(weight));
            int genderColumnIndex = data.getColumnIndex(COLUMN_GENDER);
            int gender = data.getInt(genderColumnIndex);
            switch (gender) {
                case GENDER_MALE:
                    mGenderSpinner.setSelection(1);
                    break;
                case GENDER_FEMALE:
                    mGenderSpinner.setSelection(2);
                    break;
                default:
                    mGenderSpinner.setSelection(0);
                    break;
            }
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
               mNameEditText.setText("");
                mBreedEditText.setText("");
                mWeightEditText.setText("");
                mGenderSpinner.setSelection(0); // Select "Unknown" gender
    }
}