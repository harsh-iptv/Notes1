package com.example.notes1

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.notes1.model.Note


class AddNoteActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "AddNoteActivity"
        private const val KEY_TITLE = "saved_title"
        private const val KEY_CONTENT = "saved_content"
    }

    private lateinit var etTitle: EditText
    private lateinit var etContent: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate() | rotating=${savedInstanceState != null}")
        setContentView(R.layout.activity_add_note)

        setupToolbar()

        etTitle = findViewById(R.id.etTitle)
        etContent = findViewById(R.id.etContent)

        // Restore text fields on rotation
        savedInstanceState?.let {
            etTitle.setText(it.getString(KEY_TITLE, ""))
            etContent.setText(it.getString(KEY_CONTENT, ""))
            Log.d(TAG, "Restored title/content from savedInstanceState")
        }
    }

    override fun onStart() { super.onStart(); Log.d(TAG, "onStart()") }
    override fun onResume() { super.onResume(); Log.d(TAG, "onResume()") }
    override fun onPause() { super.onPause(); Log.d(TAG, "onPause()") }
    override fun onStop() { super.onStop(); Log.d(TAG, "onStop()") }
    override fun onDestroy() { super.onDestroy(); Log.d(TAG, "onDestroy()") }

    // Save EditText state across rotation
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_TITLE, etTitle.text.toString())
        outState.putString(KEY_CONTENT, etContent.text.toString())
        Log.d(TAG, "onSaveInstanceState() — text fields saved")
    }



    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "New Note"
        }
    }



    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_add_note, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> { onBackPressedDispatcher.onBackPressed(); true }
            R.id.action_save -> { saveNote(); true }
            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun saveNote() {
        val title = etTitle.text.toString().trim()
        val content = etContent.text.toString().trim()

        if (title.isEmpty()) {
            etTitle.error = "Title is required"
            etTitle.requestFocus()
            Log.w(TAG, "Save attempted with empty title")
            return
        }

        if (content.isEmpty()) {
            etContent.error = "Content cannot be empty"
            etContent.requestFocus()
            Log.w(TAG, "Save attempted with empty content")
            return
        }

        val note = Note(title = title, content = content, isActive=true, value1 = 0)
        Log.i(TAG, "Saving note: $title")

        val resultIntent = Intent()
        resultIntent.putExtra(MainActivity.EXTRA_NOTE, note)
        setResult(RESULT_OK, resultIntent)
        Toast.makeText(this, "Note saved!", Toast.LENGTH_SHORT).show()
        finish()
    }
}
