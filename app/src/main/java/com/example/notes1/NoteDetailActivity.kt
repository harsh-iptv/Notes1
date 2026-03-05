package com.example.notes1

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.notes1.model.Note

class NoteDetailActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "NoteDetailActivity"
    }

    private var note: Note? = null

    // ─── Lifecycle ────────────────────────────────────────────────────────

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate() | rotating=${savedInstanceState != null}")
        setContentView(R.layout.activity_note_detail)

        // Note is Parcelable — survives rotation automatically via Intent extras
        note = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(MainActivity.EXTRA_NOTE, Note::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(MainActivity.EXTRA_NOTE)
        }

        if (note == null) {
            Log.e(TAG, "No note passed to NoteDetailActivity")
            finish()
            return
        }

        setupToolbar()
        displayNote()
    }

    override fun onStart() { super.onStart(); Log.d(TAG, "onStart()") }
    override fun onResume() { super.onResume(); Log.d(TAG, "onResume()") }
    override fun onPause() { super.onPause(); Log.d(TAG, "onPause()") }
    override fun onStop() { super.onStop(); Log.d(TAG, "onStop()") }
    override fun onDestroy() { super.onDestroy(); Log.d(TAG, "onDestroy()") }

    // ─── Setup ────────────────────────────────────────────────────────────

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Note Detail"
        }
    }

    private fun displayNote() {
        note?.let {
            Log.i(TAG, "Displaying note: ${it.title}")
            findViewById<TextView>(R.id.tvDetailTitle).text = it.title
            findViewById<TextView>(R.id.tvDetailContent).text = it.content
            findViewById<TextView>(R.id.tvDetailDate).text = "Created: ${it.getFormattedDate()}"
            findViewById<TextView>(R.id.tvDetailWordCount).text = "Words: ${it.content.split("\\s+".toRegex()).filter { w -> w.isNotEmpty() }.size}"
            findViewById<TextView>(R.id.tvDetailCharCount).text = "Characters: ${it.content.length}"
        }
    }

    // ─── Menu ─────────────────────────────────────────────────────────────

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> { onBackPressedDispatcher.onBackPressed(); true }
            R.id.action_delete -> { showDeleteConfirmation(); true }
            R.id.action_share -> { shareNote(); true }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // ─── Actions ──────────────────────────────────────────────────────────

    private fun showDeleteConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Delete Note")
            .setMessage("Are you sure you want to delete this note?")
            .setPositiveButton("Delete") { _, _ ->
                Log.i(TAG, "Note deletion confirmed from detail screen")
                Toast.makeText(this, "Note deleted", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                finish()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun shareNote() {
        note?.let {
            val shareText = "${it.title}\n\n${it.content}\n\n— Shared from Notes App"
            val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(android.content.Intent.EXTRA_SUBJECT, it.title)
                putExtra(android.content.Intent.EXTRA_TEXT, shareText)
            }
            startActivity(android.content.Intent.createChooser(intent, "Share Note"))
            Log.d(TAG, "Sharing note: ${it.title}")
        }
    }
}
