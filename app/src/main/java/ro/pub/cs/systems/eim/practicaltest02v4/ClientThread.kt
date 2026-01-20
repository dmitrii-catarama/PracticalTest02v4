package ro.pub.cs.systems.eim.practicaltest02v4

import android.content.Context
import android.util.Log
import android.widget.TextView
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

class ClientThread(
    private val address: String,
    private val port: String,
    private val query: String,
    private val responseTextView: TextView,
    private val context: Context
) : Thread() {

    override fun run() {
        try {
            // 1. Deschidem conexiunea catre server
            // Convertim portul in Int, deoarece vine ca String din EditText
            val socket = Socket(address, port.toInt())
            Log.d("[CLIENT THREAD]", "Connected to server at $address:$port")

            // 2. Initializam stream-urile de citire si scriere
            val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
            val writer = PrintWriter(socket.getOutputStream(), true)

            // 3. Trimitem datele (query-ul) catre server
            // Serverul va primi asta, va face cererea OkHttp si va raspunde
            writer.println(query)
            Log.d("[CLIENT THREAD]", "Sent query: $query")


            val result = StringBuilder()
            var line: String?

            while (reader.readLine().also { line = it } != null) {
                result.append(line).append("\n")
            }

            val finalResult = result.toString()
            Log.d("[CLIENT THREAD]", "Received data: $finalResult")

            responseTextView.post {
                responseTextView.text = finalResult
            }

            // 6. Inchidem socket-ul
            socket.close()

        } catch (e: IOException) {
            Log.e("[CLIENT THREAD]", "An exception has occurred: " + e.message)
            // Optional: Afisam eroarea in TextView pentru debugging
            responseTextView.post {
                responseTextView.text = "Error: ${e.message}"
            }
        } catch (e: NumberFormatException) {
            Log.e("[CLIENT THREAD]", "Invalid port number")
        }
    }
}