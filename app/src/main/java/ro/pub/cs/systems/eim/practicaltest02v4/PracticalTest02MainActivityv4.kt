package ro.pub.cs.systems.eim.practicaltest02v4

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
class PracticalTest02MainActivityv4 : AppCompatActivity() {
    private lateinit var serverPortEditText: EditText
    private lateinit var clientPortEditText: EditText
    private lateinit var clientAddressEditText: EditText
    private lateinit var wordEditText: EditText
    private lateinit var serverConnectButton: Button
    private lateinit var autocompleteTextView: TextView
    private var serverThread: ServerThread? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_practical_test02v4_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        serverPortEditText = findViewById(R.id.server_port_edit_text)
        clientAddressEditText = findViewById(R.id.client_address_edit_text)
        clientPortEditText = findViewById(R.id.client_port_edit_text)
        wordEditText = findViewById(R.id.client_word_edit_text)

        serverConnectButton = findViewById(R.id.connect_button)
        serverConnectButton.setOnClickListener {
            val serverPort = serverPortEditText.text.toString()
            if (serverPort.isEmpty()) {
                Toast.makeText(
                    applicationContext,
                    "[MAIN ACTIVITY] Server port should be filled!",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            serverThread?.stopThread()
            serverThread = ServerThread(serverPort.toInt())
            serverThread?.start()
            Toast.makeText(
                applicationContext,
                "[MAIN ACTIVITY] Server starting on port $serverPort",
                Toast.LENGTH_SHORT
            ).show()
        }

        autocompleteTextView = findViewById(R.id.autocomplete_text_view)
        wordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val clientAddress = clientAddressEditText.text.toString()
                val clientPort = clientPortEditText.text.toString()
                val query = s.toString().trim()

                if (query.isEmpty()) {
                    autocompleteTextView.text = ""
                    return
                }

                if (serverThread == null || !serverThread!!.isAlive) {
                    Toast.makeText(
                        applicationContext,
                        "[MAIN ACTIVITY] Server is not running!",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }

                if (clientAddress.isEmpty() || clientPort.isEmpty()) {
                    Toast.makeText(
                        applicationContext,
                        "[MAIN ACTIVITY] Client address and port must be set!",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }

                ClientThread(
                    clientAddress,
                    clientPort,
                    query,
                    autocompleteTextView,
                    applicationContext
                ).start()
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        serverThread?.stopThread()
    }
}