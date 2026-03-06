package com.example.notes1

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notes1.adapter.NotesAdapter
import com.example.notes1.databinding.ActivityMainBinding
import com.example.notes1.model.Note
import com.example.notes1.viewmodel.NotesViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {



    companion object {
        private const val TAG = "MainActivity"
        const val EXTRA_NOTE = "extra_note"
        const val REQUEST_ADD_NOTE = 1001
    }

    // ViewModel survives rotation via SavedStateHandle
    private val viewModel: NotesViewModel by viewModels()
    private lateinit var adapter: NotesAdapter
    private lateinit var rvNotes: RecyclerView
    private lateinit var tvEmpty: TextView
    private lateinit var tvNoteCount: TextView
    private lateinit var etSearch: EditText

    // Permission launcher
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.d(TAG, "Storage permission granted")
            Toast.makeText(this, "Permission granted! You can now attach images.", Toast.LENGTH_SHORT).show()
        } else {
            Log.w(TAG, "Storage permission denied")
            Toast.makeText(this, "Permission denied. Image attachments unavailable.", Toast.LENGTH_LONG).show()
        }
    }

    // Activity result for AddNoteActivity
    private val addNoteLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val note = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                result.data?.getParcelableExtra(EXTRA_NOTE, Note::class.java)
            } else {
                @Suppress("DEPRECATION")
                result.data?.getParcelableExtra(EXTRA_NOTE)
            }
            note?.let {
                viewModel.addNote(it)
                Log.i(TAG, "Note added: ${it.title}")
                Toast.makeText(this, "Note saved!", Toast.LENGTH_SHORT).show()
            }
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate() called | savedInstanceState=${savedInstanceState != null}")
        setContentView(R.layout.activity_main)

        setupToolbar()
        setupViews()
        setupRecyclerView()
        setupSearch()
        observeViewModel()
        requestStoragePermission()
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause() called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d(TAG, "onSaveInstanceState() called — ViewModel handles state")
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        Log.d(TAG, "onRestoreInstanceState() called")
    }


    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
    }

    private fun setupViews() {
        rvNotes = findViewById(R.id.rvNotes)
        tvEmpty = findViewById(R.id.tvEmpty)
        tvNoteCount = findViewById(R.id.tvNoteCount)
        etSearch = findViewById(R.id.etSearch)

        findViewById<FloatingActionButton>(R.id.fabAddNote).setOnClickListener {
            Log.d(TAG, "FAB clicked — launching AddNoteActivity")
            val intent = Intent(this, AddNoteActivity::class.java)
            addNoteLauncher.launch(intent)
        }
    }

    private fun setupRecyclerView() {
        adapter = NotesAdapter(
            onNoteClick = { note ->
                Log.d(TAG, "Note clicked: ${note.title}")
                val intent = Intent(this, NoteDetailActivity::class.java)
                intent.putExtra(EXTRA_NOTE, note)
                startActivity(intent)
            },
            onNoteLongClick = { note ->
                showDeleteDialog(note)
                true
            }
        )

        rvNotes.layoutManager = LinearLayoutManager(this)
        rvNotes.adapter = adapter
        rvNotes.setHasFixedSize(true)
    }

    private fun setupSearch() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.setSearchQuery(s?.toString() ?: "")
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Restore search query after rotation
        viewModel.searchQuery.value?.let {
            if (it.isNotEmpty()) etSearch.setText(it)
        }
    }

    private fun observeViewModel() {
        viewModel.filteredNotes.observe(this) { notes ->
            adapter.submitList(notes.toList())
            tvEmpty.visibility = if (notes.isEmpty()) View.VISIBLE else View.GONE
            tvNoteCount.text = "${viewModel.getNoteCount()} note${if (viewModel.getNoteCount() != 1) "s" else ""}"
        }
    }



    private fun requestStoragePermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        when {
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED -> {
                Log.d(TAG, "Storage permission already granted")
            }
            shouldShowRequestPermissionRationale(permission) -> {
                Log.d(TAG, "Showing permission rationale")
                AlertDialog.Builder(this)
                    .setTitle("Storage Permission")
                    .setMessage("This app needs storage access to attach images to your notes.")
                    .setPositiveButton("Grant") { _, _ -> permissionLauncher.launch(permission) }
                    .setNegativeButton("Skip", null)
                    .show()
            }
            else -> {
                Log.d(TAG, "Requesting storage permission")
                permissionLauncher.launch(permission)
            }
        }
    }



    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sort -> {
                Toast.makeText(this, "Notes sorted by date", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.action_about -> {
                showAboutDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }



    private fun showDeleteDialog(note: Note) {
        AlertDialog.Builder(this)
            .setTitle("Delete Note")
            .setMessage("Delete \"${note.title}\"?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteNote(note.id)
                Log.i(TAG, "Note deleted: ${note.title}")
                Toast.makeText(this, "Note deleted", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showAboutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Notes App")
            .setMessage("A sample Android Notes App demonstrating:\n\n• RecyclerView with DiffUtil\n• ViewModel + SavedStateHandle\n• Runtime Permissions\n• Lifecycle Callbacks\n• Multi-screen navigation")
            .setPositiveButton("OK", null)
            .show()
    }
}
